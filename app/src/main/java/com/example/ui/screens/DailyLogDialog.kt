package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.DailySiteLogEntity

@Composable
fun DailyLogDialog(
    log: DailySiteLogEntity?,
    projectId: Long,
    onDismiss: () -> Unit,
    onSave: (DailySiteLogEntity) -> Unit
) {
    var weather by remember { mutableStateOf(log?.weather ?: "آفتابی و صاف") }
    var temperature by remember { mutableStateOf(log?.temperature ?: "۲۴°C") }
    var totalWorkers by remember { mutableIntStateOf(log?.totalWorkersPresent ?: 12) }
    var breakdown by remember { mutableStateOf(log?.workersBreakdown ?: "آرماتوربند: ۶ نفر، قالب‌بند: ۴ نفر، کارگر: ۲ نفر") }
    var machinery by remember { mutableStateOf(log?.machineryActive ?: "جرثقیل برجی تاور کرین") }
    var completedWork by remember { mutableStateOf(log?.completedWorkSummary ?: "") }
    var delays by remember { mutableStateOf(log?.delaysAndObstacles ?: "") }
    var safetyNotes by remember { mutableStateOf(log?.safetyAndHseNotes ?: "رعایت کامل ضوابط HSE") }
    var reporterName by remember { mutableStateOf(log?.reporterName ?: "دفتر فنی کارگاه") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (log == null) "ثبت گزارش روزانه کارگاه ساختمانی" else "ویرایش گزارش روزانه",
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = weather,
                        onValueChange = { weather = it },
                        label = { Text("وضعیت هوا") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = temperature,
                        onValueChange = { temperature = it },
                        label = { Text("دما") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = totalWorkers.toString(),
                    onValueChange = { totalWorkers = it.toIntOrNull() ?: 0 },
                    label = { Text("تعداد کل نفرات حاضر در کارگاه") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("log_input_workers"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = breakdown,
                    onValueChange = { breakdown = it },
                    label = { Text("تفکیک اکیپ‌های فعال") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = machinery,
                    onValueChange = { machinery = it },
                    label = { Text("ماشین‌آلات و تجهیزات فعال") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = completedWork,
                    onValueChange = { completedWork = it },
                    label = { Text("شرح عملیات انجام شده امروز") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("log_input_work"),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = delays,
                    onValueChange = { delays = it },
                    label = { Text("موانع کارگاه، معارضات یا تاخیرات") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                OutlinedTextField(
                    value = safetyNotes,
                    onValueChange = { safetyNotes = it },
                    label = { Text("نکات ایمنی و بهداشت (HSE)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                OutlinedTextField(
                    value = reporterName,
                    onValueChange = { reporterName = it },
                    label = { Text("نام تکمیل‌کننده گزارش") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = (log ?: DailySiteLogEntity(
                        projectId = projectId,
                        date = System.currentTimeMillis()
                    )).copy(
                        weather = weather,
                        temperature = temperature,
                        totalWorkersPresent = totalWorkers,
                        workersBreakdown = breakdown,
                        machineryActive = machinery,
                        completedWorkSummary = completedWork,
                        delaysAndObstacles = delays,
                        safetyAndHseNotes = safetyNotes,
                        reporterName = reporterName
                    )
                    onSave(updated)
                    onDismiss()
                },
                modifier = Modifier.testTag("log_dialog_save")
            ) {
                Text("ثبت گزارش")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}
