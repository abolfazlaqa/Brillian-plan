package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectEntity
import com.example.ui.theme.PersianGold
import com.example.ui.theme.PersianGoldBright
import com.example.ui.theme.PersianVioletPrimary
import com.example.ui.theme.TaskCompletedGreen
import com.example.util.DateUtils

@Composable
fun ProjectManagementDialog(
    projects: List<ProjectEntity>,
    activeProjectId: Long,
    onSelectProject: (Long) -> Unit,
    onCreateProject: (name: String, code: String, manager: String, location: String, startDays: Long, finishDays: Long, notes: String) -> Unit,
    onUpdateProject: (ProjectEntity) -> Unit,
    onDeleteProject: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var isCreatingNew by remember { mutableStateOf(false) }
    var editingProject by remember { mutableStateOf<ProjectEntity?>(null) }
    var projectToDelete by remember { mutableStateOf<ProjectEntity?>(null) }

    // Form fields
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var managerName by remember { mutableStateOf("") }
    var siteLocation by remember { mutableStateOf("") }
    var durationDaysStr by remember { mutableStateOf("180") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PersianVioletPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apartment,
                            contentDescription = null,
                            tint = PersianGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isCreatingNew) "تعریف پروژه جدید"
                        else if (editingProject != null) "ویرایش پروژه"
                        else "مدیریت و انتخاب پروژه‌ها",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "بستن")
                }
            }
        },
        text = {
            if (isCreatingNew || editingProject != null) {
                // Form for creating or editing project
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "مشخصات کارگاه و پروژه ساختمانی را وارد نمایید:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("نام پروژه *") },
                        placeholder = { Text("مثلاً: ساختمان مسکونی برلیان - فاز ۱") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_project_name")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text("کد پروژه") },
                            placeholder = { Text("PRJ-01") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_project_code")
                        )
                        OutlinedTextField(
                            value = durationDaysStr,
                            onValueChange = { durationDaysStr = it.filter { ch -> ch.isDigit() } },
                            label = { Text("مدت زمان (روز)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_project_duration")
                        )
                    }

                    OutlinedTextField(
                        value = managerName,
                        onValueChange = { managerName = it },
                        label = { Text("مدیر پروژه / مهندس ناظر") },
                        placeholder = { Text("مهندس رحیمی") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_project_manager")
                    )

                    OutlinedTextField(
                        value = siteLocation,
                        onValueChange = { siteLocation = it },
                        label = { Text("محل / آدرس کارگاه") },
                        placeholder = { Text("تهران، کارگاه مرکزی") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_project_location")
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("توضیحات و مشخصات فنی") },
                        placeholder = { Text("تعداد طبقات، متراژ زیربنا، نوع اسکلت...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("input_project_notes")
                    )
                }
            } else {
                // List of projects
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "پروژه‌های ثبت شده (${projects.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PersianVioletPrimary
                        )
                        FilledTonalButton(
                            onClick = {
                                name = ""
                                code = "PRJ-${projects.size + 1}"
                                managerName = ""
                                siteLocation = ""
                                durationDaysStr = "180"
                                notes = ""
                                editingProject = null
                                isCreatingNew = true
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = PersianVioletPrimary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("btn_new_project_start")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("پروژه جدید", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (projects.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("هیچ پروژه‌ای تعریف نشده است.")
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(projects, key = { it.id }) { proj ->
                                val isActive = proj.id == activeProjectId
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSelectProject(proj.id) }
                                        .testTag("project_item_${proj.id}"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surface
                                    ),
                                    border = if (isActive) androidx.compose.foundation.BorderStroke(2.dp, PersianGold) else null,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                if (isActive) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = "فعال",
                                                        tint = PersianGold,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                }
                                                Column {
                                                    Text(
                                                        text = proj.name,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "کد: ${proj.code} • سرپرست: ${proj.managerName}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            Row {
                                                IconButton(
                                                    onClick = {
                                                        editingProject = proj
                                                        name = proj.name
                                                        code = proj.code
                                                        managerName = proj.managerName
                                                        siteLocation = proj.siteLocation
                                                        val days = ((proj.finishDate - proj.startDate) / (24L * 60 * 60 * 1000)).coerceAtLeast(1)
                                                        durationDaysStr = days.toString()
                                                        notes = proj.notes
                                                        isCreatingNew = false
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = "ویرایش",
                                                        tint = PersianVioletPrimary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                IconButton(
                                                    onClick = { projectToDelete = proj },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "حذف",
                                                        tint = MaterialTheme.colorScheme.error,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }

                                        if (proj.siteLocation.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.LocationOn,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = proj.siteLocation,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
        },
        confirmButton = {
            if (isCreatingNew || editingProject != null) {
                Button(
                    onClick = {
                        val durationDays = durationDaysStr.toLongOrNull() ?: 180L
                        val startMillis = editingProject?.startDate ?: System.currentTimeMillis()
                        val finishMillis = startMillis + (durationDays * 24L * 60 * 60 * 1000)

                        if (editingProject != null) {
                            onUpdateProject(
                                editingProject!!.copy(
                                    name = name.ifBlank { editingProject!!.name },
                                    code = code.ifBlank { editingProject!!.code },
                                    managerName = managerName,
                                    siteLocation = siteLocation,
                                    startDate = startMillis,
                                    finishDate = finishMillis,
                                    notes = notes
                                )
                            )
                            editingProject = null
                        } else {
                            onCreateProject(
                                name.ifBlank { "پروژه جدید برلیان" },
                                code.ifBlank { "PRJ-${System.currentTimeMillis() % 1000}" },
                                managerName,
                                siteLocation,
                                startMillis,
                                finishMillis,
                                notes
                            )
                            isCreatingNew = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PersianVioletPrimary),
                    modifier = Modifier.testTag("btn_save_project")
                ) {
                    Text("ذخیره و فعال‌سازی", color = Color.White)
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PersianVioletPrimary)
                ) {
                    Text("بستن", color = Color.White)
                }
            }
        },
        dismissButton = {
            if (isCreatingNew || editingProject != null) {
                OutlinedButton(
                    onClick = {
                        isCreatingNew = false
                        editingProject = null
                    }
                ) {
                    Text("انصراف")
                }
            }
        }
    )

    // Confirmation dialog for deleting a project
    if (projectToDelete != null) {
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = { Text("حذف پروژه") },
            text = {
                Text("آیا از حذف پروژه «${projectToDelete?.name}» و تمام فعالیت‌ها، منابع و گزارش‌های روزانه آن اطمینان دارید؟ این عمل غیرقابل بازگشت است.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        projectToDelete?.let { onDeleteProject(it.id) }
                        projectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("بله، حذف کن")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { projectToDelete = null }) {
                    Text("انصراف")
                }
            }
        )
    }
}
