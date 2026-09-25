package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    private val gregorianFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun formatDate(timestamp: Long): String {
        if (timestamp <= 0) return "-"
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)
        val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
        val jalaliStr = String.format(Locale.US, "%04d/%02d/%02d", jy, jm, jd)
        val gregStr = gregorianFormat.format(Date(timestamp))
        return "$jalaliStr ($gregStr)"
    }

    fun formatShortDate(timestamp: Long): String {
        if (timestamp <= 0) return "-"
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)
        val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
        return String.format(Locale.US, "%02d/%02d", jm, jd)
    }

    fun formatGregorian(timestamp: Long): String {
        if (timestamp <= 0) return ""
        return gregorianFormat.format(Date(timestamp))
    }

    fun formatIsoUtc(timestamp: Long): String {
        return isoFormat.format(Date(timestamp))
    }

    fun parseIsoDate(isoString: String): Long {
        return try {
            val clean = if (isoString.contains(".")) {
                isoString.substringBefore(".")
            } else {
                isoString
            }
            val formats = listOf(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US),
                SimpleDateFormat("yyyy-MM-dd", Locale.US),
                SimpleDateFormat("yyyy/MM/dd", Locale.US)
            )
            for (fmt in formats) {
                try {
                    val date = fmt.parse(clean)
                    if (date != null) return date.time
                } catch (_: Exception) {}
            }
            0L
        } catch (_: Exception) {
            0L
        }
    }

    fun calculateDaysBetween(start: Long, end: Long): Int {
        if (start <= 0 || end <= 0 || end < start) return 1
        val diff = end - start
        val days = (diff / (1000L * 60 * 60 * 24)).toInt()
        return if (days <= 0) 1 else days
    }

    fun addDays(timestamp: Long, days: Int): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            add(Calendar.DAY_OF_YEAR, days)
        }
        return cal.timeInMillis
    }

    // Accurate Gregorian to Jalali converter
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy2 = gy - 1600
        val gm2 = gm - 1
        val gd2 = gd - 1

        var gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400
        for (i in 0 until gm2) {
            gDayNo += gDaysInMonth[i]
        }
        if (gm2 > 1 && ((gy2 % 4 == 0 && gy2 % 100 != 0) || (gy2 % 400 == 0))) {
            gDayNo++
        }
        gDayNo += gd2

        var jDayNo = gDayNo - 79
        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        for (i in 0..10) {
            if (jDayNo < jDaysInMonth[i]) {
                jm = i
                break
            }
            jDayNo -= jDaysInMonth[i]
            jm = 11
        }
        val jd = jDayNo + 1
        return Triple(jy, jm + 1, jd)
    }

    fun toPersianDigits(text: String): String {
        val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val sb = java.lang.StringBuilder()
        for (ch in text) {
            if (ch in '0'..'9') {
                sb.append(persianDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }
}
