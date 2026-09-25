package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.DailySiteLogEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ResourceEntity
import com.example.data.model.TaskEntity
import com.example.data.repository.ConstructionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream

enum class TaskFilter(val titleFa: String) {
    ALL("همه فعالیت‌ها"),
    IN_PROGRESS("در حال اجرا"),
    REMAINING("باقیمانده"),
    COMPLETED("تکمیل شده"),
    CRITICAL("مسیر بحرانی (CPM)")
}

data class ProjectMetrics(
    val overallProgress: Int = 0,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val remainingTasks: Int = 0,
    val criticalTasks: Int = 0,
    val totalDays: Int = 0,
    val totalLaborActive: Int = 0,
    val phaseProgressMap: Map<String, Int> = emptyMap()
)

class ConstructionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ConstructionRepository =
        ConstructionRepository(AppDatabase.getDatabase(application))

    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedProjectId = MutableStateFlow<Long>(1L)
    val selectedProjectId: StateFlow<Long> = _selectedProjectId.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val currentProject: StateFlow<ProjectEntity?> = _selectedProjectId.flatMapLatest { id ->
        repository.getProject(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allTasks: StateFlow<List<TaskEntity>> = _selectedProjectId.flatMapLatest { id ->
        repository.getTasks(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allResources: StateFlow<List<ResourceEntity>> = _selectedProjectId.flatMapLatest { id ->
        repository.getResources(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val allDailyLogs: StateFlow<List<DailySiteLogEntity>> = _selectedProjectId.flatMapLatest { id ->
        repository.getDailyLogs(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtering & search
    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow(TaskFilter.ALL)
    val selectedPhaseFilter = MutableStateFlow("ALL")

    // Filtered tasks
    val filteredTasks: StateFlow<List<TaskEntity>> = combine(
        allTasks,
        searchQuery,
        selectedFilter,
        selectedPhaseFilter
    ) { tasks, query, filter, phase ->
        tasks.filter { task ->
            val matchesQuery = query.isBlank() ||
                    task.name.contains(query, ignoreCase = true) ||
                    task.wbs.contains(query, ignoreCase = true) ||
                    task.assignedTrade.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                TaskFilter.ALL -> true
                TaskFilter.IN_PROGRESS -> task.isInProgress
                TaskFilter.REMAINING -> !task.isCompleted
                TaskFilter.COMPLETED -> task.isCompleted
                TaskFilter.CRITICAL -> task.isCritical
            }

            val matchesPhase = phase == "ALL" || task.phase == phase

            matchesQuery && matchesFilter && matchesPhase
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculated metrics
    val projectMetrics: StateFlow<ProjectMetrics> = allTasks.combine(allDailyLogs) { tasks, logs ->
        val nonSummaryTasks = tasks.filter { !it.isSummary && !it.isMilestone }
        val total = nonSummaryTasks.size
        val completed = nonSummaryTasks.count { it.isCompleted }
        val inProgress = nonSummaryTasks.count { it.isInProgress }
        val remaining = nonSummaryTasks.count { !it.isCompleted }
        val critical = nonSummaryTasks.count { it.isCritical }

        // Weighted progress based on duration
        val totalDuration = nonSummaryTasks.sumOf { it.durationDays }.coerceAtLeast(1)
        val completedDuration = nonSummaryTasks.sumOf { it.durationDays * (it.percentComplete / 100.0) }
        val overallPercent = ((completedDuration / totalDuration) * 100).toInt().coerceIn(0, 100)

        // Phase progress
        val phaseMap = mutableMapOf<String, Int>()
        val byPhase = nonSummaryTasks.groupBy { it.phase }
        for ((pName, pTasks) in byPhase) {
            val pDur = pTasks.sumOf { it.durationDays }.coerceAtLeast(1)
            val pDone = pTasks.sumOf { it.durationDays * (it.percentComplete / 100.0) }
            phaseMap[pName] = ((pDone / pDur) * 100).toInt().coerceIn(0, 100)
        }

        val latestWorkers = logs.firstOrNull()?.totalWorkersPresent ?: nonSummaryTasks.filter { it.isInProgress }.sumOf { it.requiredLaborCount }

        ProjectMetrics(
            overallProgress = overallPercent,
            totalTasks = total,
            completedTasks = completed,
            inProgressTasks = inProgress,
            remainingTasks = remaining,
            criticalTasks = critical,
            totalDays = totalDuration,
            totalLaborActive = latestWorkers,
            phaseProgressMap = phaseMap
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProjectMetrics())

    // Feedback message channel
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
            // Make sure selected project exists
            allProjects.collect { projects ->
                if (projects.isNotEmpty() && projects.none { it.id == _selectedProjectId.value }) {
                    _selectedProjectId.value = projects.first().id
                }
            }
        }
    }

    fun selectProject(projectId: Long) {
        _selectedProjectId.value = projectId
    }

    fun updateTaskProgress(taskId: Long, newPercent: Int) {
        viewModelScope.launch {
            repository.updateTaskProgress(taskId, newPercent)
            _userMessage.emit("پیشرفت کار به‌روزرسانی شد: $newPercent%")
        }
    }

    fun saveTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.saveTask(task.copy(projectId = _selectedProjectId.value))
            _userMessage.emit("فعالیت با موفقیت ذخیره شد")
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            _userMessage.emit("فعالیت حذف شد")
        }
    }

    fun saveDailyLog(log: DailySiteLogEntity) {
        viewModelScope.launch {
            repository.saveDailyLog(log.copy(projectId = _selectedProjectId.value))
            _userMessage.emit("گزارش روزانه کارگاه ثبت شد")
        }
    }

    fun deleteDailyLog(logId: Long) {
        viewModelScope.launch {
            repository.deleteDailyLog(logId)
            _userMessage.emit("گزارش روزانه حذف شد")
        }
    }

    fun saveResource(resource: ResourceEntity) {
        viewModelScope.launch {
            repository.saveResource(resource.copy(projectId = _selectedProjectId.value))
            _userMessage.emit("منبع / اکیپ ذخیره شد")
        }
    }

    fun deleteResource(resourceId: Long) {
        viewModelScope.launch {
            repository.deleteResource(resourceId)
            _userMessage.emit("منبع حذف شد")
        }
    }

    fun resetToSampleProject() {
        viewModelScope.launch {
            val newId = repository.seedSampleProject()
            _selectedProjectId.value = newId
            _userMessage.emit("پروژه جامع نمونه ساختمانی با موفقیت بارگذاری شد")
        }
    }

    fun importMspFile(uri: Uri) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val stream: InputStream? = context.contentResolver.openInputStream(uri)
                if (stream != null) {
                    val newProjId = repository.importMspXml(stream)
                    _selectedProjectId.value = newProjId
                    _userMessage.emit("پروژه مایکروسافت پروجکت (MSP) با موفقیت وارد گردید!")
                } else {
                    _userMessage.emit("خطا در باز کردن فایل")
                }
            } catch (e: Exception) {
                _userMessage.emit("خطا در پردازش فایل MSP: ${e.localizedMessage ?: "فرمت نامعتبر"}")
            }
        }
    }

    fun importCsvTasks(csvContent: String) {
        viewModelScope.launch {
            try {
                repository.importCsvTasks(csvContent, _selectedProjectId.value)
                _userMessage.emit("فعالیت‌ها از CSV با موفقیت وارد شدند")
            } catch (e: Exception) {
                _userMessage.emit("خطا در پردازش CSV: ${e.localizedMessage}")
            }
        }
    }

    suspend fun exportMspXml(): String {
        return repository.exportProjectToMspXml(_selectedProjectId.value)
    }

    suspend fun exportCsv(): String {
        return repository.exportTasksToCsv(_selectedProjectId.value)
    }
}
