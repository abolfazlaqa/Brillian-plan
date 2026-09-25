package com.example

import com.example.data.msp.MspCsvHandler
import com.example.data.msp.MspXmlParser
import com.example.data.msp.SampleConstructionData
import com.example.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {

    @Test
    fun testMspDurationParsing() {
        assertEquals(10, MspXmlParser.parseMspDurationToDays("PT80H0M0S"))
        assertEquals(1, MspXmlParser.parseMspDurationToDays("PT8H0M0S"))
        assertEquals(5, MspXmlParser.parseMspDurationToDays("P5D"))
        assertEquals(3, MspXmlParser.parseMspDurationToDays("24"))
    }

    @Test
    fun testDateUtilsJalaliConversion() {
        val (jy, jm, jd) = DateUtils.gregorianToJalali(2026, 3, 21)
        assertEquals(1405, jy)
        assertEquals(1, jm)
        assertEquals(1, jd)

        val persianDigits = DateUtils.toPersianDigits("1405/01/01")
        assertEquals("۱۴۰۵/۰۱/۰۱", persianDigits)
    }

    @Test
    fun testSampleDataGeneration() {
        val project = SampleConstructionData.createSampleProject()
        assertNotNull(project)
        assertEquals("BLD-1405", project.code)

        val tasks = SampleConstructionData.createSampleTasks(1L)
        assertTrue(tasks.size > 20)
        assertTrue(tasks.any { it.isCritical })
        assertTrue(tasks.any { it.isMilestone })

        val resources = SampleConstructionData.createSampleResources(1L)
        assertTrue(resources.isNotEmpty())
    }

    @Test
    fun testMspXmlParserFromString() {
        val xmlSample = """
            <?xml version="1.0" encoding="UTF-8"?>
            <Project xmlns="http://schemas.microsoft.com/project">
              <Name>پروژه تست کارگاه</Name>
              <StartDate>2026-04-01T08:00:00</StartDate>
              <FinishDate>2026-06-01T17:00:00</FinishDate>
              <Tasks>
                <Task>
                  <UID>1</UID>
                  <ID>1</ID>
                  <Name>فونداسیون</Name>
                  <Duration>PT40H0M0S</Duration>
                  <Start>2026-04-01T08:00:00</Start>
                  <Finish>2026-04-06T17:00:00</Finish>
                  <PercentComplete>60</PercentComplete>
                  <Summary>0</Summary>
                  <Critical>1</Critical>
                  <OutlineNumber>1.1</OutlineNumber>
                </Task>
              </Tasks>
              <Resources>
                <Resource>
                  <UID>1</UID>
                  <Name>اکیپ آرماتوربند</Name>
                  <Type>1</Type>
                </Resource>
              </Resources>
            </Project>
        """.trimIndent()

        val parsed = MspXmlParser.parseString(xmlSample, targetProjectId = 10L)
        assertEquals("پروژه تست کارگاه", parsed.project.name)
        assertEquals(1, parsed.tasks.size)
        assertEquals("فونداسیون", parsed.tasks[0].name)
        assertEquals(5, parsed.tasks[0].durationDays)
        assertEquals(60, parsed.tasks[0].percentComplete)
        assertTrue(parsed.tasks[0].isCritical)
        assertEquals(1, parsed.resources.size)
        assertEquals("اکیپ آرماتوربند", parsed.resources[0].name)
    }

    @Test
    fun testMspCsvHandler() {
        val csv = """
            WBS,Name,DurationDays,StartDate,FinishDate,PercentComplete,IsCritical,LaborCount,Trade,Notes
            "1.1","گودبرداری",10,"2026/04/01","2026/04/11",100,YES,4,"ماشین‌آلات","تست"
        """.trimIndent()

        val parsed = MspCsvHandler.parseCsv(csv, 1L)
        assertEquals(1, parsed.size)
        assertEquals("گودبرداری", parsed[0].name)
        assertEquals(10, parsed[0].durationDays)
        assertEquals(100, parsed[0].percentComplete)
        assertTrue(parsed[0].isCritical)
    }
}
