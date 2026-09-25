package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.data.model.TaskEntity
import com.example.ui.ConstructionViewModel
import com.example.ui.components.GanttChartView

@Composable
fun GanttScreen(
    viewModel: ConstructionViewModel,
    onTaskClick: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.allTasks.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        GanttChartView(
            tasks = tasks,
            onTaskClick = onTaskClick
        )
    }
}
