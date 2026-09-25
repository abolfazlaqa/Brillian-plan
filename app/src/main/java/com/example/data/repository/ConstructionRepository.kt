package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.DailySiteLogEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ResourceEntity
import com.example.data.model.TaskEntity
import com.example.data.msp.MspCsvHandler
import com.example.data.msp.MspParsedProject
import com.example.data.msp.MspXmlExporter
import com.example.data.msp.MspXmlParser
import com.example.data.msp.SampleConstructionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.io.InputStream

class ConstructionRepository(private val database: AppDatabase) {

    private val projectDao = database.projectDao()
    private val taskDao = database.taskDao()
    private val resourceDao = database.resourceDao()
    private val dailySiteLogDao = database.dailySiteLogDao()

    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    fun getProject(projectId: Long): Flow<ProjectEntity?> = projectDao.getProjectById(projectId)

    fun getTasks(projectId: Long): Flow<List<TaskEntity>> = taskDao.getTasksForProject(projectId)

    fun getResources(projectId: Long): Flow<List<ResourceEntity>> = resourceDao.getResourcesForProject(projectId)

    fun getDailyLogs(projectId: Long): Flow<List<DailySiteLogEntity>> = dailySiteLogDao.getLogsForProject(projectId)

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val count = projectDao.getProjectCount()
        if (count == 0) {
            seedSampleProject()
        }
    }

    suspend fun seedSampleProject() = withContext(Dispatchers.IO) {
        val project = SampleConstructionData.createSampleProject()
        val newProjId = projectDao.insertProject(project.copy(id = 0))

        val tasks = SampleConstructionData.createSampleTasks(newProjId)
        taskDao.insertTasks(tasks)

        val resources = SampleConstructionData.createSampleResources(newProjId)
        resourceDao.insertResources(resources)

        val logs = SampleConstructionData.createSampleDailyLogs(newProjId)
        dailySiteLogDao.insertLogs(logs)

        newProjId
    }

    suspend fun importMspXml(inputStream: InputStream): Long = withContext(Dispatchers.IO) {
        val parsed: MspParsedProject = MspXmlParser.parse(inputStream, targetProjectId = 0)
        val newProjId = projectDao.insertProject(parsed.project.copy(id = 0))

        val tasksWithId = parsed.tasks.map { it.copy(projectId = newProjId, id = 0) }
        if (tasksWithId.isNotEmpty()) {
            taskDao.insertTasks(tasksWithId)
        }

        val resourcesWithId = parsed.resources.map { it.copy(projectId = newProjId, id = 0) }
        if (resourcesWithId.isNotEmpty()) {
            resourceDao.insertResources(resourcesWithId)
        }

        newProjId
    }

    suspend fun importMspXmlString(xmlContent: String): Long = withContext(Dispatchers.IO) {
        val parsed = MspXmlParser.parseString(xmlContent, targetProjectId = 0)
        val newProjId = projectDao.insertProject(parsed.project.copy(id = 0))

        val tasksWithId = parsed.tasks.map { it.copy(projectId = newProjId, id = 0) }
        if (tasksWithId.isNotEmpty()) {
            taskDao.insertTasks(tasksWithId)
        }

        val resourcesWithId = parsed.resources.map { it.copy(projectId = newProjId, id = 0) }
        if (resourcesWithId.isNotEmpty()) {
            resourceDao.insertResources(resourcesWithId)
        }

        newProjId
    }

    suspend fun importCsvTasks(csvContent: String, projectId: Long) = withContext(Dispatchers.IO) {
        val tasks = MspCsvHandler.parseCsv(csvContent, projectId)
        if (tasks.isNotEmpty()) {
            taskDao.insertTasks(tasks)
        }
    }

    suspend fun exportProjectToMspXml(projectId: Long): String = withContext(Dispatchers.IO) {
        val project = projectDao.getProjectByIdDirect(projectId)
            ?: throw IllegalStateException("پروژه یافت نشد")
        val tasks = taskDao.getTasksForProjectDirect(projectId)
        val resources = resourceDao.getResourcesForProjectDirect(projectId)
        MspXmlExporter.exportToXml(project, tasks, resources)
    }

    suspend fun exportTasksToCsv(projectId: Long): String = withContext(Dispatchers.IO) {
        val tasks = taskDao.getTasksForProjectDirect(projectId)
        MspCsvHandler.exportToCsv(tasks)
    }

    suspend fun updateTaskProgress(taskId: Long, percent: Int) = withContext(Dispatchers.IO) {
        taskDao.updateTaskProgress(taskId, percent.coerceIn(0, 100))
    }

    suspend fun saveTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        if (task.id == 0L) {
            taskDao.insertTask(task)
        } else {
            taskDao.updateTask(task)
        }
    }

    suspend fun deleteTask(taskId: Long) = withContext(Dispatchers.IO) {
        taskDao.deleteTaskById(taskId)
    }

    suspend fun saveDailyLog(log: DailySiteLogEntity) = withContext(Dispatchers.IO) {
        if (log.id == 0L) {
            dailySiteLogDao.insertLog(log)
        } else {
            dailySiteLogDao.updateLog(log)
        }
    }

    suspend fun deleteDailyLog(logId: Long) = withContext(Dispatchers.IO) {
        dailySiteLogDao.deleteLogById(logId)
    }

    suspend fun saveResource(resource: ResourceEntity) = withContext(Dispatchers.IO) {
        if (resource.id == 0L) {
            resourceDao.insertResource(resource)
        } else {
            resourceDao.updateResource(resource)
        }
    }

    suspend fun deleteResource(resourceId: Long) = withContext(Dispatchers.IO) {
        resourceDao.deleteResourceById(resourceId)
    }

    suspend fun updateProject(project: ProjectEntity) = withContext(Dispatchers.IO) {
        projectDao.updateProject(project)
    }
}
