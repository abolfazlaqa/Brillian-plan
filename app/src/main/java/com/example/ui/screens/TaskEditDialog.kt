package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.ui.theme.CriticalPathRed
import com.example.util.DateUtils

@Composable
fun TaskEditDialog(
    task: TaskEntity?,
    projectId: Long,
    onDismiss: () -> Unit,
    onSave: (TaskEntity) -> Unit,
    onDelete: ((Long) -> Unit)? = null
) {
    var name by remember { mutableStateOf(task?.name ?: "") }
    var wbs by remember { mutableStateOf(task?.wbs ?: "1.1") }
    var phase by remember { mutableStateOf(task?.phase ?: "سازه و بتن‌ریزی") }
    var durationDays by remember { mutableIntStateOf(task?.durationDays ?: 5) }
    var percentComplete by remember { mutableFloatStateOf(task?.percentComplete?.toFloat() ?: 0f) }
    var isCritical by remember { mutableStateOf(task?.isCritical ?: false) }
    var isMilestone by remember { mutableStateOf(task?.isMilestone ?: false) }
    var requiredLabor by remember { mutableIntStateOf(task?.requiredLaborCount ?: 4) }
    var trade by remember { mutableStateOf(task?.assignedTrade ?: "اکیپ اجرایی") }
    var notes by remember { mutableStateOf(task?.notes ?: "") }

    val startDate = task?.startDate ?: System.currentTimeMillis()
    val finishDate = DateUtils.addDays(startDate, durationDays.coerceAtLeast(1))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (task == null) "افزودن فعالیت جدید (WBS)" else "ویرایش فعالیت زمانبندی",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("عنوان فعالیت (Task Name)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_input_name"),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = wbs,
                        onValueChange = { wbs = it },
                        label = { Text("کد WBS") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("task_input_wbs"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = durationDays.toString(),
                        onValueChange = { durationDays = it.toIntOrNull() ?: 1 },
                        label = { Text("مدت زمان (روز)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("task_input_duration"),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = phase,
                    onValueChange = { phase = it },
                    label = { Text("فاز اجرایی (Phase)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Progress slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "درصد پیشرفت فیزیکی:",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${DateUtils.toPersianDigits(percentComplete.toInt().toString())}%",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = percentComplete,
                        onValueChange = { percentComplete = it },
                        valueRange = 0f..100f,
                        steps = 19,
                        modifier = Modifier.testTag("task_slider_percent")
                    )
                }

                // Manpower & Trade
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = requiredLabor.toString(),
                        onValueChange = { requiredLabor = it.toIntOrNull() ?: 0 },
                        label = { Text("نیروی کار (نفر)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = trade,
                        onValueChange = { trade = it },
                        label = { Text("تخصص / اکیپ") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Critical Path & Milestone switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "مسیر بحرانی (Critical Path):")
                    Switch(
                        checked = isCritical,
                        onCheckedChange = { isCritical = it },
                        modifier = Modifier.testTag("task_switch_critical")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "نقطه عطف (Milestone):")
                    Switch(
                        checked = isMilestone,
                        onCheckedChange = { isMilestone = it }
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("توضیحات و نکات اجرایی کارگاه") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val updated = (task ?: TaskEntity(
                            projectId = projectId,
                            uid = (100..9999).random(),
                            wbs = wbs,
                            name = name,
                            startDate = startDate,
                            finishDate = finishDate
                        )).copy(
                            name = name.trim(),
                            wbs = wbs.trim(),
                            phase = phase.trim(),
                            durationDays = durationDays.coerceAtLeast(0),
                            percentComplete = percentComplete.toInt().coerceIn(0, 100),
                            isCritical = isCritical,
                            isMilestone = isMilestone,
                            requiredLaborCount = requiredLabor,
                            assignedTrade = trade.trim(),
                            notes = notes.trim(),
                            finishDate = finishDate
                        )
                        onSave(updated)
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("task_dialog_save")
            ) {
                Text("ذخیره فعالیت")
            }
        },
        dismissButton = {
            Row {
                if (task != null && onDelete != null) {
                    TextButton(
                        onClick = {
                            onDelete(task.id)
                            onDismiss()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = CriticalPathRed),
                        modifier = Modifier.testTag("task_dialog_delete")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حذف")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("انصراف")
                }
            }
        }
    )
}
