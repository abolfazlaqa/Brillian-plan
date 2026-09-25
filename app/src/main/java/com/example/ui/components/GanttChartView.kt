package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.ui.theme.CriticalPathRed
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.TaskCompletedGreen
import com.example.util.DateUtils

enum class GanttZoom(val dayWidthDp: Float, val label: String) {
    COMPACT(14f, "فشرده"),
    NORMAL(28f, "عادی"),
    DETAILED(44f, "روزانه دقیق")
}

@Composable
fun GanttChartView(
    tasks: List<TaskEntity>,
    onTaskClick: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tasks.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "فعالیتی برای نمایش در گانت چارت یافت نشد.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    var currentZoom by remember { mutableStateOf(GanttZoom.NORMAL) }

    // Calculate project start & finish timeline boundaries
    val minStart = remember(tasks) {
        val nonZero = tasks.map { it.startDate }.filter { it > 0 }
        if (nonZero.isNotEmpty()) nonZero.minOrNull()!! else System.currentTimeMillis()
    }
    val maxFinish = remember(tasks) {
        val nonZero = tasks.map { it.finishDate }.filter { it > 0 }
        if (nonZero.isNotEmpty()) nonZero.maxOrNull()!! else DateUtils.addDays(minStart, 60)
    }

    val totalDays = remember(minStart, maxFinish) {
        DateUtils.calculateDaysBetween(minStart, maxFinish).coerceAtLeast(30)
    }

    val timelineWidthDp = (totalDays * currentZoom.dayWidthDp).dp
    val scrollState = rememberScrollState()
    val listState = rememberLazyListState()

    val todayTime = remember { System.currentTimeMillis() }
    val todayOffsetDays = remember(minStart, todayTime) {
        ((todayTime - minStart) / (1000L * 60 * 60 * 24)).toFloat()
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Toolbar for Zoom & Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "نمای گانت (Gantt Chart)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))

            // Zoom buttons
            IconButton(
                onClick = {
                    currentZoom = when (currentZoom) {
                        GanttZoom.DETAILED -> GanttZoom.NORMAL
                        GanttZoom.NORMAL -> GanttZoom.COMPACT
                        GanttZoom.COMPACT -> GanttZoom.COMPACT
                    }
                },
                modifier = Modifier.testTag("gantt_zoom_out")
            ) {
                Icon(Icons.Default.ZoomOut, contentDescription = "بزرگنمایی کمتر")
            }

            IconButton(
                onClick = {
                    currentZoom = when (currentZoom) {
                        GanttZoom.COMPACT -> GanttZoom.NORMAL
                        GanttZoom.NORMAL -> GanttZoom.DETAILED
                        GanttZoom.DETAILED -> GanttZoom.DETAILED
                    }
                },
                modifier = Modifier.testTag("gantt_zoom_in")
            ) {
                Icon(Icons.Default.ZoomIn, contentDescription = "بزرگنمایی بیشتر")
            }
        }

        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = TaskCompletedGreen, label = "تکمیل شده")
            Spacer(modifier = Modifier.width(12.dp))
            LegendItem(color = CriticalPathRed, label = "مسیر بحرانی (CPM)")
            Spacer(modifier = Modifier.width(12.dp))
            LegendItem(color = SafetyAmber, label = "در حال اجرا")
            Spacer(modifier = Modifier.width(12.dp))
            LegendItem(color = Color(0xFF64748B), label = "برنامه‌ریزی شده")
        }

        // Main Gantt Container
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Fixed Left Pane: Task Titles & Duration
                Column(
                    modifier = Modifier
                        .width(170.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "فعالیت / مدت (روز)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Left Task List Rows
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(tasks, key = { it.id }) { task ->
                            val padStart = ((task.outlineLevel - 1) * 10).coerceAtMost(30).dp
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clickable { onTaskClick(task) }
                                    .padding(start = padStart, end = 6.dp, top = 4.dp, bottom = 4.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Column {
                                    Text(
                                        text = "${task.wbs} ${task.name}",
                                        style = if (task.isSummary) MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                        else MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = if (task.isCritical) CriticalPathRed else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (task.isMilestone) "نقطه عطف" else "${task.durationDays} روز • ${task.percentComplete}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Right Scrollable Pane: Time Scale & Gantt Bars
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .horizontalScroll(scrollState)
                ) {
                    Column(modifier = Modifier.width(timelineWidthDp)) {
                        // Timeline Header (Days/Weeks)
                        TimelineHeader(
                            minStart = minStart,
                            totalDays = totalDays,
                            dayWidthDp = currentZoom.dayWidthDp
                        )

                        // Bars
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Grid Canvas for days & Today line
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val dayW = currentZoom.dayWidthDp.dp.toPx()
                                // Vertical grid lines
                                for (i in 0..totalDays) {
                                    val x = i * dayW
                                    drawLine(
                                        color = Color.LightGray.copy(alpha = 0.3f),
                                        start = Offset(x, 0f),
                                        end = Offset(x, size.height),
                                        strokeWidth = 1f
                                    )
                                }

                                // Today Red Line
                                if (todayOffsetDays in 0f..totalDays.toFloat()) {
                                    val todayX = todayOffsetDays * dayW
                                    drawLine(
                                        color = Color.Red,
                                        start = Offset(todayX, 0f),
                                        end = Offset(todayX, size.height),
                                        strokeWidth = 3f
                                    )
                                }
                            }

                            // Render Gantt Task Rows synchronized with Left Pane
                            Column {
                                tasks.forEach { task ->
                                    GanttBarRow(
                                        task = task,
                                        minStart = minStart,
                                        dayWidthDp = currentZoom.dayWidthDp,
                                        onTaskClick = { onTaskClick(task) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineHeader(
    minStart: Long,
    totalDays: Int,
    dayWidthDp: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
    ) {
        val step = if (dayWidthDp < 20f) 5 else if (dayWidthDp < 35f) 2 else 1
        var dayIndex = 0
        while (dayIndex < totalDays) {
            val date = DateUtils.addDays(minStart, dayIndex)
            val width = (step * dayWidthDp).dp
            val shortDate = DateUtils.formatShortDate(date)

            Box(
                modifier = Modifier
                    .width(width)
                    .fillMaxHeight()
                    .border(0.5.dp, Color.LightGray.copy(alpha = 0.4f))
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "روز ${DateUtils.toPersianDigits((dayIndex + 1).toString())}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = DateUtils.toPersianDigits(shortDate),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            dayIndex += step
        }
    }
}

@Composable
private fun GanttBarRow(
    task: TaskEntity,
    minStart: Long,
    dayWidthDp: Float,
    onTaskClick: () -> Unit
) {
    val startDayOffset = ((task.startDate - minStart) / (1000L * 60 * 60 * 24)).toInt().coerceAtLeast(0)
    val leftPadding = (startDayOffset * dayWidthDp).dp
    val barWidth = (task.durationDays.coerceAtLeast(1) * dayWidthDp).dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onTaskClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (task.isMilestone) {
            // Milestone diamond
            Box(
                modifier = Modifier
                    .offset(x = leftPadding)
                    .size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = "Milestone",
                    tint = if (task.isCritical) CriticalPathRed else SafetyAmber,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            // Task Bar
            val barColor = when {
                task.isCompleted -> TaskCompletedGreen
                task.isCritical -> CriticalPathRed
                task.isInProgress -> SafetyAmber
                else -> Color(0xFF64748B)
            }

            Box(
                modifier = Modifier
                    .offset(x = leftPadding)
                    .width(barWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(barColor.copy(alpha = 0.25f))
                    .border(1.dp, barColor, RoundedCornerShape(6.dp))
            ) {
                // Progress fill inside bar
                if (task.percentComplete > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(task.percentComplete / 100f)
                            .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                            .background(barColor)
                    )
                }

                // Label on bar if wide enough
                if (barWidth > 45.dp) {
                    Text(
                        text = "${task.percentComplete}%",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp
        )
    }
}
