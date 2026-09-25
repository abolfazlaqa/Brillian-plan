package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_site_logs",
    indices = [Index("projectId")]
)
data class DailySiteLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val date: Long = System.currentTimeMillis(),
    val weather: String = "آفتابی و معتدل",
    val temperature: String = "۲۴°C",
    val totalWorkersPresent: Int = 12,
    val workersBreakdown: String = "آرماتوربند: ۴ نفر، قالب‌بند: ۴ نفر، کارگر ساده: ۴ نفر",
    val machineryActive: String = "یک دستگاه جرثقیل و بتونیر",
    val completedWorkSummary: String = "",
    val delaysAndObstacles: String = "", // موانع کار یا تاخیرات
    val safetyAndHseNotes: String = "تمامی پرسنل کلاه و کفش ایمنی داشتند.",
    val reporterName: String = "دفتر فنی کارگاه",
    val approvedBySupervisor: Boolean = true
)
