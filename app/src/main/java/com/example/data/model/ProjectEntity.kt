package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val code: String = "PRJ-01",
    val siteLocation: String = "کارگاه مرکزی",
    val managerName: String = "مهندس ناظر / مدیر پروژه",
    val startDate: Long = System.currentTimeMillis(),
    val finishDate: Long = System.currentTimeMillis() + (180L * 24 * 60 * 60 * 1000), // ~6 months
    val status: String = "در حال اجرا", // در حال اجرا، تکمیل شده، با تاخیر
    val baselineCost: Double = 0.0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
