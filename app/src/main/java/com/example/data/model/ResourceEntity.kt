package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "resources",
    indices = [Index("projectId")]
)
data class ResourceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val uid: Int = 0,
    val name: String, // e.g. اکیپ آرماتوربند، اکیپ قالب‌بند، جوشکار اسکلت، کارگر ساده، بیل مکانیکی
    val type: String = "نیروی انسانی", // نیروی انسانی، ماشین‌آلات، تجهیزات
    val unit: String = "نفر", // نفر، دستگاه، شیفت
    val countAvailable: Int = 5, // ظرفیت موجود در کارگاه
    val dailyRate: Double = 0.0, // هزینه روزانه (تومان / ریال)
    val supervisorName: String = "", // سرپرست اکیپ / پیمانکار جزء
    val phoneContact: String = ""
)
