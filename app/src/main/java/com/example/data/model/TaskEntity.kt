package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    indices = [
        Index("projectId"),
        Index("uid")
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val uid: Int, // MS Project UID
    val idInProject: Int = 1, // Task ID (1, 2, 3...)
    val wbs: String = "1",
    val outlineLevel: Int = 1,
    val isSummary: Boolean = false,
    val name: String,
    val phase: String = "عمومی",
    val durationDays: Int = 1,
    val startDate: Long,
    val finishDate: Long,
    val percentComplete: Int = 0, // 0 to 100
    val predecessors: String = "", // e.g. "1FS", "2"
    val isCritical: Boolean = false,
    val isMilestone: Boolean = false,
    val requiredLaborCount: Int = 2, // تعداد نفرات نیروی کار مورد نیاز
    val assignedTrade: String = "کارگر ساده", // e.g. آرماتوربند، قالب‌بند، جوشکار، بنا
    val notes: String = "",
    val sortOrder: Int = 0
) {
    val isCompleted: Boolean get() = percentComplete >= 100
    val isInProgress: Boolean get() = percentComplete in 1..99
    val isNotStarted: Boolean get() = percentComplete == 0

    // Calculates remaining work in days
    val remainingDays: Int
        get() {
            if (isCompleted) return 0
            val doneFraction = percentComplete / 100.0
            val doneDays = (durationDays * doneFraction).toInt()
            return (durationDays - doneDays).coerceAtLeast(1)
        }
}
