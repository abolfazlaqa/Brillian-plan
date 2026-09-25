package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.data.model.DailySiteLogEntity
import com.example.data.model.ResourceEntity
import com.example.data.model.TaskEntity
import com.example.ui.ConstructionViewModel
import com.example.ui.screens.DailyLogDialog
import com.example.ui.screens.DailyLogsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GanttScreen
import com.example.ui.screens.ManpowerScreen
import com.example.ui.screens.MspImportExportScreen
import com.example.ui.screens.ProjectManagementDialog
import com.example.ui.screens.ResourceEditDialog
import com.example.ui.screens.TaskEditDialog
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianVioletPrimary

enum class AppNavScreen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("داشبورد", Icons.Default.Dashboard, Icons.Outlined.Dashboard),
    TASKS("فعالیت‌ها", Icons.Default.FormatListNumbered, Icons.Outlined.FormatListNumbered),
    GANTT("گانت چارت", Icons.Default.AutoGraph, Icons.Outlined.AutoGraph),
    MANPOWER("نیروی کار", Icons.Default.Engineering, Icons.Outlined.Engineering),
    DAILY_LOGS("گزارش روزانه", Icons.Default.Assignment, Icons.Outlined.Assignment),
    MSP_TOOLS("ابزار MSP", Icons.Default.FileUpload, Icons.Default.ImportExport)
}

class MainActivity : ComponentActivity() {

    private val viewModel: ConstructionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: ConstructionViewModel) {
    var currentScreen by remember { mutableStateOf(AppNavScreen.DASHBOARD) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog states
    var showProjectDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
    var showTaskDialog by remember { mutableStateOf(false) }

    var editingLog by remember { mutableStateOf<DailySiteLogEntity?>(null) }
    var showLogDialog by remember { mutableStateOf(false) }

    var editingResource by remember { mutableStateOf<ResourceEntity?>(null) }
    var showResourceDialog by remember { mutableStateOf(false) }

    // Observers
    val currentProject by viewModel.currentProject.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()
    val metrics by viewModel.projectMetrics.collectAsState()
    val dailyLogs by viewModel.allDailyLogs.collectAsState()
    val selectedProjectId by viewModel.selectedProjectId.collectAsState()

    // Handle user messages
    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // BackHandler: return to Dashboard if on sub-screens
    BackHandler(enabled = currentScreen != AppNavScreen.DASHBOARD) {
        currentScreen = AppNavScreen.DASHBOARD
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { showProjectDialog = true },
                        modifier = Modifier.testTag("action_manage_projects")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apartment,
                            contentDescription = "مدیریت پروژه‌ها",
                            tint = PersianGold
                        )
                    }
                },
                title = {
                    Text(
                        text = "برلیان پلن | Brilliant Plan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(
                        onClick = { currentScreen = AppNavScreen.MSP_TOOLS },
                        modifier = Modifier.testTag("action_msp_tools")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ImportExport,
                            contentDescription = "وارد کردن و خروجی MSP",
                            tint = if (currentScreen == AppNavScreen.MSP_TOOLS) PersianGold else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                listOf(
                    AppNavScreen.DASHBOARD,
                    AppNavScreen.TASKS,
                    AppNavScreen.GANTT,
                    AppNavScreen.MANPOWER,
                    AppNavScreen.DAILY_LOGS
                ).forEach { screen ->
                    val selected = currentScreen == screen
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("nav_item_${screen.name}")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppNavScreen.DASHBOARD -> {
                    DashboardScreen(
                        currentProject = currentProject,
                        projects = allProjects,
                        metrics = metrics,
                        latestLog = dailyLogs.firstOrNull(),
                        onSelectProject = { viewModel.selectProject(it) },
                        onOpenProjectManagement = { showProjectDialog = true },
                        onNavigateToTasks = { currentScreen = AppNavScreen.TASKS },
                        onNavigateToGantt = { currentScreen = AppNavScreen.GANTT },
                        onNavigateToImport = { currentScreen = AppNavScreen.MSP_TOOLS },
                        onNavigateToLogs = { currentScreen = AppNavScreen.DAILY_LOGS },
                        onAddTask = {
                            editingTask = null
                            showTaskDialog = true
                        },
                        onNewDailyLog = {
                            editingLog = null
                            showLogDialog = true
                        },
                        onResetSample = {
                            viewModel.resetToSampleProject()
                        }
                    )
                }

                AppNavScreen.TASKS -> {
                    TasksScreen(
                        viewModel = viewModel,
                        onTaskClick = { task ->
                            editingTask = task
                            showTaskDialog = true
                        },
                        onAddTask = {
                            editingTask = null
                            showTaskDialog = true
                        }
                    )
                }

                AppNavScreen.GANTT -> {
                    GanttScreen(
                        viewModel = viewModel,
                        onTaskClick = { task ->
                            editingTask = task
                            showTaskDialog = true
                        }
                    )
                }

                AppNavScreen.MANPOWER -> {
                    ManpowerScreen(
                        viewModel = viewModel,
                        onResourceClick = { res ->
                            editingResource = res
                            showResourceDialog = true
                        },
                        onAddResource = {
                            editingResource = null
                            showResourceDialog = true
                        }
                    )
                }

                AppNavScreen.DAILY_LOGS -> {
                    DailyLogsScreen(
                        viewModel = viewModel,
                        onLogClick = { log ->
                            editingLog = log
                            showLogDialog = true
                        },
                        onAddLog = {
                            editingLog = null
                            showLogDialog = true
                        }
                    )
                }

                AppNavScreen.MSP_TOOLS -> {
                    MspImportExportScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // Task Edit/Create Dialog
    if (showTaskDialog) {
        TaskEditDialog(
            task = editingTask,
            projectId = selectedProjectId,
            onDismiss = { showTaskDialog = false },
            onSave = { task ->
                viewModel.saveTask(task)
            },
            onDelete = { taskId ->
                viewModel.deleteTask(taskId)
            }
        )
    }

    // Daily Log Dialog
    if (showLogDialog) {
        DailyLogDialog(
            log = editingLog,
            projectId = selectedProjectId,
            onDismiss = { showLogDialog = false },
            onSave = { log ->
                viewModel.saveDailyLog(log)
            }
        )
    }

    // Resource Dialog
    if (showResourceDialog) {
        ResourceEditDialog(
            resource = editingResource,
            projectId = selectedProjectId,
            onDismiss = { showResourceDialog = false },
            onSave = { res ->
                viewModel.saveResource(res)
            },
            onDelete = { resId ->
                viewModel.deleteResource(resId)
            }
        )
    }

    // Project Management Dialog
    if (showProjectDialog) {
        ProjectManagementDialog(
            projects = allProjects,
            activeProjectId = selectedProjectId,
            onSelectProject = {
                viewModel.selectProject(it)
                showProjectDialog = false
            },
            onCreateProject = { name, code, manager, location, start, finish, notes ->
                viewModel.createProject(
                    name = name,
                    code = code,
                    managerName = manager,
                    siteLocation = location,
                    startDate = start,
                    finishDate = finish,
                    notes = notes
                )
                showProjectDialog = false
            },
            onUpdateProject = { proj ->
                viewModel.updateCurrentProject(proj)
            },
            onDeleteProject = { id ->
                viewModel.deleteProject(id)
            },
            onDismiss = { showProjectDialog = false }
        )
    }
}
