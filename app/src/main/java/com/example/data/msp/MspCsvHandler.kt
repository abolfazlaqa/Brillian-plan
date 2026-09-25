package com.example.data.msp

import com.example.data.model.ProjectEntity
import com.example.data.model.TaskEntity
import com.example.util.DateUtils

object MspCsvHandler {

    fun exportToCsv(tasks: List<TaskEntity>): String {
        val sb = StringBuilder()
        sb.append("WBS,Name,DurationDays,StartDate,FinishDate,PercentComplete,IsCritical,LaborCount,Trade,Notes\n")
        tasks.forEach { t ->
            sb.append("\"").append(t.wbs).append("\",")
            sb.append("\"").append(t.name.replace("\"", "\"\"")).append("\",")
            sb.append(t.durationDays).append(",")
            sb.append("\"").append(DateUtils.formatGregorian(t.startDate)).append("\",")
            sb.append("\"").append(DateUtils.formatGregorian(t.finishDate)).append("\",")
            sb.append(t.percentComplete).append(",")
            sb.append(if (t.isCritical) "YES" else "NO").append(",")
            sb.append(t.requiredLaborCount).append(",")
            sb.append("\"").append(t.assignedTrade).append("\",")
            sb.append("\"").append(t.notes.replace("\"", "\"\"")).append("\"\n")
        }
        return sb.toString()
    }

    fun parseCsv(csvText: String, projectId: Long): List<TaskEntity> {
        val lines = csvText.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()

        val tasks = mutableListOf<TaskEntity>()
        var uid = 1
        val startIndex = if (lines[0].contains("Name", ignoreCase = true) || lines[0].contains("نام", ignoreCase = true)) 1 else 0

        for (i in startIndex until lines.size) {
            val line = lines[i]
            val tokens = parseCsvLine(line)
            if (tokens.size >= 2) {
                val wbs = tokens.getOrNull(0) ?: "$uid"
                val name = tokens.getOrNull(1) ?: "فعالیت $uid"
                val duration = tokens.getOrNull(2)?.toIntOrNull() ?: 3
                val start = DateUtils.parseIsoDate(tokens.getOrNull(3) ?: "")
                    .takeIf { it > 0 } ?: System.currentTimeMillis()
                val finish = DateUtils.parseIsoDate(tokens.getOrNull(4) ?: "")
                    .takeIf { it > 0 } ?: DateUtils.addDays(start, duration)
                val percent = tokens.getOrNull(5)?.replace("%", "")?.trim()?.toIntOrNull() ?: 0
                val critical = tokens.getOrNull(6)?.equals("YES", ignoreCase = true) ?: false
                val labor = tokens.getOrNull(7)?.toIntOrNull() ?: 3
                val trade = tokens.getOrNull(8) ?: "کارگر ساده"
                val notes = tokens.getOrNull(9) ?: ""

                tasks.add(
                    TaskEntity(
                        projectId = projectId,
                        uid = uid,
                        idInProject = uid,
                        wbs = wbs,
                        outlineLevel = if (wbs.contains(".")) 2 else 1,
                        isSummary = wbs.length == 1,
                        name = name,
                        phase = if (wbs.length == 1) name else "عمومی",
                        durationDays = duration,
                        startDate = start,
                        finishDate = finish,
                        percentComplete = percent.coerceIn(0, 100),
                        isCritical = critical,
                        requiredLaborCount = labor,
                        assignedTrade = trade,
                        notes = notes,
                        sortOrder = uid
                    )
                )
                uid++
            }
        }
        return tasks
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var inQuotes = false
        val cur = StringBuilder()
        for (ch in line) {
            if (ch == '\"') {
                inQuotes = !inQuotes
            } else if ((ch == ',' || ch == ';') && !inQuotes) {
                result.add(cur.toString().trim())
                cur.clear()
            } else {
                cur.append(ch)
            }
        }
        result.add(cur.toString().trim())
        return result
    }
}
