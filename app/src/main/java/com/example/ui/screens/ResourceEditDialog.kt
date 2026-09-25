package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.data.model.ResourceEntity
import com.example.ui.theme.CriticalPathRed

@Composable
fun ResourceEditDialog(
    resource: ResourceEntity?,
    projectId: Long,
    onDismiss: () -> Unit,
    onSave: (ResourceEntity) -> Unit,
    onDelete: ((Long) -> Unit)? = null
) {
    var name by remember { mutableStateOf(resource?.name ?: "") }
    var type by remember { mutableStateOf(resource?.type ?: "نیروی انسانی") }
    var unit by remember { mutableStateOf(resource?.unit ?: "نفر") }
    var count by remember { mutableIntStateOf(resource?.countAvailable ?: 5) }
    var supervisor by remember { mutableStateOf(resource?.supervisorName ?: "") }
    var phone by remember { mutableStateOf(resource?.phoneContact ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (resource == null) "افزودن منبع / اکیپ کارگاهی" else "ویرایش منبع / اکیپ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("عنوان منبع یا اکیپ (مانند: اکیپ آرماتوربند)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("resource_input_name"),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = { type = it },
                        label = { Text("نوع (نیروی انسانی / ماشین‌آلات)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("واحد (نفر / دستگاه)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = count.toString(),
                    onValueChange = { count = it.toIntOrNull() ?: 1 },
                    label = { Text("ظرفیت / تعداد موجود در کارگاه") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("resource_input_count"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = supervisor,
                    onValueChange = { supervisor = it },
                    label = { Text("نام سرپرست اکیپ / پیمانکار جزء") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("شماره تماس") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val updated = (resource ?: ResourceEntity(
                            projectId = projectId,
                            uid = (100..999).random(),
                            name = name
                        )).copy(
                            name = name.trim(),
                            type = type.trim(),
                            unit = unit.trim(),
                            countAvailable = count,
                            supervisorName = supervisor.trim(),
                            phoneContact = phone.trim()
                        )
                        onSave(updated)
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("resource_dialog_save")
            ) {
                Text("ذخیره")
            }
        },
        dismissButton = {
            Row {
                if (resource != null && onDelete != null) {
                    TextButton(
                        onClick = {
                            onDelete(resource.id)
                            onDismiss()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = CriticalPathRed)
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
