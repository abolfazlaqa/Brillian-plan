package com.example.data.msp

import com.example.data.model.ProjectEntity
import com.example.data.model.ResourceEntity
import com.example.data.model.TaskEntity
import com.example.util.DateUtils

object MspXmlExporter {

    fun exportToXml(
        project: ProjectEntity,
        tasks: List<TaskEntity>,
        resources: List<ResourceEntity>
    ): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
        sb.append("<Project xmlns=\"http://schemas.microsoft.com/project\">\n")
        sb.append("  <Name>").append(escapeXml(project.name)).append("</Name>\n")
        sb.append("  <Title>").append(escapeXml(project.name)).append("</Title>\n")
        sb.append("  <Company>Brilliant Plan</Company>\n")
        sb.append("  <Manager>").append(escapeXml(project.managerName)).append("</Manager>\n")
        sb.append("  <StartDate>").append(DateUtils.formatIsoUtc(project.startDate)).append("</StartDate>\n")
        sb.append("  <FinishDate>").append(DateUtils.formatIsoUtc(project.finishDate)).append("</FinishDate>\n")

        // Tasks block
        sb.append("  <Tasks>\n")
        tasks.forEachIndexed { index, task ->
            val durationHours = task.durationDays * 8
            sb.append("    <Task>\n")
            sb.append("      <UID>").append(task.uid.takeIf { it > 0 } ?: (index + 1)).append("</UID>\n")
            sb.append("      <ID>").append(index + 1).append("</ID>\n")
            sb.append("      <Name>").append(escapeXml(task.name)).append("</Name>\n")
            sb.append("      <OutlineNumber>").append(escapeXml(task.wbs)).append("</OutlineNumber>\n")
            sb.append("      <OutlineLevel>").append(task.outlineLevel).append("</OutlineLevel>\n")
            sb.append("      <Duration>PT").append(durationHours).append("H0M0S</Duration>\n")
            sb.append("      <Start>").append(DateUtils.formatIsoUtc(task.startDate)).append("</Start>\n")
            sb.append("      <Finish>").append(DateUtils.formatIsoUtc(task.finishDate)).append("</Finish>\n")
            sb.append("      <PercentComplete>").append(task.percentComplete).append("</PercentComplete>\n")
            sb.append("      <Summary>").append(if (task.isSummary) "1" else "0").append("</Summary>\n")
            sb.append("      <Critical>").append(if (task.isCritical) "1" else "0").append("</Critical>\n")
            sb.append("      <Milestone>").append(if (task.isMilestone) "1" else "0").append("</Milestone>\n")
            if (task.notes.isNotBlank()) {
                sb.append("      <Notes>").append(escapeXml(task.notes)).append("</Notes>\n")
            }
            sb.append("    </Task>\n")
        }
        sb.append("  </Tasks>\n")

        // Resources block
        sb.append("  <Resources>\n")
        resources.forEachIndexed { index, res ->
            sb.append("    <Resource>\n")
            sb.append("      <UID>").append(res.uid.takeIf { it > 0 } ?: (index + 1)).append("</UID>\n")
            sb.append("      <ID>").append(index + 1).append("</ID>\n")
            sb.append("      <Name>").append(escapeXml(res.name)).append("</Name>\n")
            sb.append("      <Type>").append(if (res.type == "نیروی انسانی") "1" else "2").append("</Type>\n")
            sb.append("      <StandardRate>").append(res.dailyRate).append("</StandardRate>\n")
            sb.append("    </Resource>\n")
        }
        sb.append("  </Resources>\n")

        sb.append("</Project>")
        return sb.toString()
    }

    private fun escapeXml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}
