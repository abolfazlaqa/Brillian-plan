package com.example.data.msp

import com.example.data.model.ProjectEntity
import com.example.data.model.ResourceEntity
import com.example.data.model.TaskEntity
import com.example.util.DateUtils
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.io.StringReader

data class MspParsedProject(
    val project: ProjectEntity,
    val tasks: List<TaskEntity>,
    val resources: List<ResourceEntity>
)

object MspXmlParser {

    private fun createParser(): XmlPullParser {
        val factory = org.xmlpull.v1.XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = false
        return factory.newPullParser()
    }

    fun parse(inputStream: InputStream, targetProjectId: Long = 0L): MspParsedProject {
        val parser = createParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, "UTF-8")
        return parseXml(parser, targetProjectId)
    }

    fun parseString(xmlContent: String, targetProjectId: Long = 0L): MspParsedProject {
        val parser = createParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(StringReader(xmlContent))
        return parseXml(parser, targetProjectId)
    }

    private fun parseXml(parser: XmlPullParser, targetProjectId: Long): MspParsedProject {
        var projectName = "پروژه وارد شده از MSP"
        var startDate = System.currentTimeMillis()
        var finishDate = DateUtils.addDays(startDate, 120)

        val tasks = mutableListOf<TaskEntity>()
        val resources = mutableListOf<ResourceEntity>()

        var eventType = parser.eventType
        var currentSection = ""

        // Task temp variables
        var taskUid = 0
        var taskId = 0
        var taskName = ""
        var taskWbs = ""
        var taskOutlineLevel = 1
        var taskIsSummary = false
        var taskDurationDays = 1
        var taskStart = 0L
        var taskFinish = 0L
        var taskPercent = 0
        var taskCritical = false
        var taskMilestone = false
        var taskPredecessors = StringBuilder()
        var currentPhase = "عمومی"

        // Resource temp variables
        var resUid = 0
        var resName = ""
        var resType = "نیروی انسانی"

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tag = parser.name ?: ""

            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (tag) {
                        "Tasks" -> currentSection = "Tasks"
                        "Resources" -> currentSection = "Resources"
                        "Task" -> {
                            taskUid = 0
                            taskId = 0
                            taskName = ""
                            taskWbs = ""
                            taskOutlineLevel = 1
                            taskIsSummary = false
                            taskDurationDays = 1
                            taskStart = 0L
                            taskFinish = 0L
                            taskPercent = 0
                            taskCritical = false
                            taskMilestone = false
                            taskPredecessors = StringBuilder()
                        }
                        "Resource" -> {
                            resUid = 0
                            resName = ""
                            resType = "نیروی انسانی"
                        }
                        "Name" -> {
                            val text = parser.nextText()
                            if (currentSection == "Tasks") taskName = text
                            else if (currentSection == "Resources") resName = text
                            else if (projectName == "پروژه وارد شده از MSP" && text.isNotBlank()) projectName = text
                        }
                        "StartDate" -> {
                            if (currentSection.isEmpty()) {
                                startDate = DateUtils.parseIsoDate(parser.nextText())
                            }
                        }
                        "FinishDate" -> {
                            if (currentSection.isEmpty()) {
                                finishDate = DateUtils.parseIsoDate(parser.nextText())
                            }
                        }
                        "UID" -> {
                            val uid = parser.nextText().toIntOrNull() ?: 0
                            if (currentSection == "Tasks") taskUid = uid
                            else if (currentSection == "Resources") resUid = uid
                        }
                        "ID" -> {
                            if (currentSection == "Tasks") {
                                taskId = parser.nextText().toIntOrNull() ?: 0
                            }
                        }
                        "OutlineNumber", "WBS" -> {
                            if (currentSection == "Tasks") taskWbs = parser.nextText()
                        }
                        "OutlineLevel" -> {
                            if (currentSection == "Tasks") taskOutlineLevel = parser.nextText().toIntOrNull() ?: 1
                        }
                        "Summary" -> {
                            if (currentSection == "Tasks") taskIsSummary = parser.nextText() == "1"
                        }
                        "Critical" -> {
                            if (currentSection == "Tasks") taskCritical = parser.nextText() == "1"
                        }
                        "Milestone" -> {
                            if (currentSection == "Tasks") taskMilestone = parser.nextText() == "1"
                        }
                        "PercentComplete" -> {
                            if (currentSection == "Tasks") taskPercent = parser.nextText().toIntOrNull() ?: 0
                        }
                        "Duration" -> {
                            if (currentSection == "Tasks") {
                                val durStr = parser.nextText()
                                taskDurationDays = parseMspDurationToDays(durStr)
                            }
                        }
                        "Start" -> {
                            if (currentSection == "Tasks") taskStart = DateUtils.parseIsoDate(parser.nextText())
                        }
                        "Finish" -> {
                            if (currentSection == "Tasks") taskFinish = DateUtils.parseIsoDate(parser.nextText())
                        }
                        "PredecessorUID" -> {
                            if (currentSection == "Tasks") {
                                val pred = parser.nextText()
                                if (pred.isNotBlank()) {
                                    if (taskPredecessors.isNotEmpty()) taskPredecessors.append(", ")
                                    taskPredecessors.append(pred)
                                }
                            }
                        }
                        "Type" -> {
                            if (currentSection == "Resources") {
                                val t = parser.nextText()
                                resType = if (t == "1") "نیروی انسانی" else "ماشین‌آلات / تجهیزات"
                            }
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    when (tag) {
                        "Tasks" -> currentSection = ""
                        "Resources" -> currentSection = ""
                        "Task" -> {
                            // Only include valid non-root tasks (MSP often has UID 0 for project summary)
                            if (taskName.isNotBlank() && taskUid > 0) {
                                if (taskStart <= 0L) taskStart = startDate
                                if (taskFinish <= 0L) {
                                    taskFinish = DateUtils.addDays(taskStart, taskDurationDays.coerceAtLeast(1))
                                }
                                if (taskIsSummary) {
                                    currentPhase = taskName
                                }
                                val labor = if (taskIsSummary) 0 else if (taskMilestone) 0 else 4

                                val t = TaskEntity(
                                    projectId = targetProjectId,
                                    uid = taskUid,
                                    idInProject = if (taskId > 0) taskId else taskUid,
                                    wbs = if (taskWbs.isNotBlank()) taskWbs else taskUid.toString(),
                                    outlineLevel = taskOutlineLevel,
                                    isSummary = taskIsSummary,
                                    name = taskName,
                                    phase = currentPhase,
                                    durationDays = taskDurationDays,
                                    startDate = taskStart,
                                    finishDate = taskFinish,
                                    percentComplete = taskPercent.coerceIn(0, 100),
                                    predecessors = taskPredecessors.toString(),
                                    isCritical = taskCritical,
                                    isMilestone = taskMilestone,
                                    requiredLaborCount = labor,
                                    assignedTrade = if (taskIsSummary) "" else "اکیپ اجرایی",
                                    sortOrder = taskUid
                                )
                                tasks.add(t)
                            }
                        }
                        "Resource" -> {
                            if (resName.isNotBlank() && resUid > 0) {
                                val r = ResourceEntity(
                                    projectId = targetProjectId,
                                    uid = resUid,
                                    name = resName,
                                    type = resType,
                                    unit = if (resType == "نیروی انسانی") "نفر" else "دستگاه",
                                    countAvailable = 5
                                )
                                resources.add(r)
                            }
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        val project = ProjectEntity(
            id = targetProjectId,
            name = projectName,
            code = "MSP-" + (100..999).random(),
            siteLocation = "کارگاه ساختمانی",
            startDate = startDate,
            finishDate = finishDate,
            status = "در حال اجرا",
            notes = "وارد شده از فایل Microsoft Project XML"
        )

        return MspParsedProject(project, tasks, resources)
    }

    // Converts MSP duration (e.g. PT80H0M0S, PT16H, P5D, or 80) to days
    fun parseMspDurationToDays(durStr: String): Int {
        if (durStr.isBlank()) return 1
        try {
            val clean = durStr.trim().uppercase()
            if (clean.startsWith("PT")) {
                var hours = 0
                val hIndex = clean.indexOf('H')
                if (hIndex > 2) {
                    val hStr = clean.substring(2, hIndex)
                    hours = hStr.toIntOrNull() ?: 0
                }
                val days = hours / 8
                return if (days <= 0 && hours > 0) 1 else days
            } else if (clean.startsWith("P") && clean.endsWith("D")) {
                val dStr = clean.substring(1, clean.length - 1)
                return dStr.toIntOrNull() ?: 1
            } else {
                val rawNumber = clean.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 8
                return if (rawNumber >= 8) rawNumber / 8 else rawNumber
            }
        } catch (_: Exception) {
            return 1
        }
    }
}
