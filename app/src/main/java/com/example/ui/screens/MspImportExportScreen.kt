package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ConstructionViewModel
import com.example.ui.theme.PersianGold
import kotlinx.coroutines.launch

@Composable
fun MspImportExportScreen(
    viewModel: ConstructionViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var xmlPasteText by remember { mutableStateOf("") }
    var csvPasteText by remember { mutableStateOf("") }
    var exportedPreview by remember { mutableStateOf("") }

    // File picker for MS Project XML
    val mspFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.importMspFile(uri)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Guide Card for MS Project
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "راهنما",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "نحوه خروجی گرفتن از مایکروسافت پروجکت (MSP):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "در نرم‌افزار MS Project سیستم خود به منوی File > Save As رفته و در قسمت Save as type گزینه XML Format (*.xml) را انتخاب کنید. سپس فایل حاصل را در اینجا بارگذاری نمایید.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Section 1: File Import (MSP XML)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = "وارد کردن MSP",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "۱. انتخاب و وارد کردن فایل MSP (.xml)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "فایل پروژه خروجی داده شده از نرم‌افزار مایکروسافت پروجکت را انتخاب کنید تا کلیه فعالیت‌ها، زمانبندی، ساختار WBS و منابع به صورت خودکار ایجاد شوند.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        mspFilePicker.launch(arrayOf("text/xml", "application/xml", "*/*"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_select_msp_file")
                ) {
                    Icon(Icons.Default.Description, contentDescription = "انتخاب فایل")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("انتخاب فایل XML از حافظه دستگاه")
                }
            }
        }

        // Section 2: Direct Paste (For Quick Testing on Android Emulator)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "۲. درج مستقیم متن XML یا CSV فعالیت‌ها",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = xmlPasteText,
                    onValueChange = { xmlPasteText = it },
                    label = { Text("متن XML پروژه مایکروسافت پروجکت یا CSV") },
                    placeholder = { Text("محتوای <Project>...</Project> را اینجا قرار دهید...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("input_msp_paste_xml"),
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            if (xmlPasteText.isNotBlank()) {
                                if (xmlPasteText.contains("<Project", ignoreCase = true)) {
                                    scope.launch {
                                        try {
                                            val repository = com.example.data.repository.ConstructionRepository(
                                                com.example.data.local.AppDatabase.getDatabase(context)
                                            )
                                            val newId = repository.importMspXmlString(xmlPasteText)
                                            viewModel.selectProject(newId)
                                            Toast.makeText(context, "پروژه MSP با موفقیت وارد شد!", Toast.LENGTH_SHORT).show()
                                            xmlPasteText = ""
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "خطا در پردازش: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } else {
                                    viewModel.importCsvTasks(xmlPasteText)
                                    xmlPasteText = ""
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("پردازش و بارگذاری داده‌ها")
                    }

                    OutlinedButton(
                        onClick = { xmlPasteText = "" },
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Text("پاک کردن")
                    }
                }
            }
        }

        // Section 3: Export Current Project (to MS Project XML or CSV)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "خروجی گرفتن",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "۳. خروجی گرفتن برای MS Project یا اکسل",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val xml = viewModel.exportMspXml()
                                    exportedPreview = xml
                                    copyToClipboard(context, xml, "فایل XML پروژه کپی شد")
                                    shareText(context, xml, "خروجی MS Project XML")
                                } catch (e: Exception) {
                                    Toast.makeText(context, "خطا در خروجی: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_export_msp_xml")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "اشتراک XML")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("خروجی MSP XML")
                    }

                    FilledTonalButton(
                        onClick = {
                            scope.launch {
                                try {
                                    val csv = viewModel.exportCsv()
                                    exportedPreview = csv
                                    copyToClipboard(context, csv, "فایل CSV فعالیت‌ها کپی شد")
                                    shareText(context, csv, "خروجی CSV فعالیت‌های کارگاه")
                                } catch (e: Exception) {
                                    Toast.makeText(context, "خطا در خروجی: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_export_csv")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "اشتراک CSV")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("خروجی CSV / اکسل")
                    }
                }

                if (exportedPreview.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = exportedPreview.take(300) + "\n... (بقیه متن در کلیپ‌بورد کپی شد)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("پیش‌نمایش خروجی") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Section 4: Load Construction Sample Project
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "۴. پروژه نمونه جامع کارگاهی",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "احداث ساختمان ۵ طبقه مسکونی با تمامی مراحل (خاکبرداری، اسکلت بتنی، سفت‌کاری، تاسیسات، نازک‌کاری و نما) به همراه اکیپ‌های فعال و درصد پیشرفت واقعی جهت بررسی و آزمایش امکانات برنامه.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { viewModel.resetToSampleProject() },
                    colors = ButtonDefaults.buttonColors(containerColor = PersianGold, contentColor = Color(0xFF160E33)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_reset_sample_project")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "بارگذاری نمونه")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("بارگذاری مجدد پروژه نمونه ساختمانی")
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

private fun copyToClipboard(context: Context, text: String, message: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("MSP Export", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

private fun shareText(context: Context, text: String, title: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_TITLE, title)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, title)
    context.startActivity(shareIntent)
}
