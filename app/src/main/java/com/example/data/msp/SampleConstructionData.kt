package com.example.data.msp

import com.example.data.model.DailySiteLogEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ResourceEntity
import com.example.data.model.TaskEntity
import com.example.util.DateUtils

object SampleConstructionData {

    fun createSampleProject(): ProjectEntity {
        val now = System.currentTimeMillis()
        val start = DateUtils.addDays(now, -45) // started 45 days ago
        val finish = DateUtils.addDays(now, 160) // finishes in 160 days
        return ProjectEntity(
            id = 1L,
            name = "پروژه احداث ساختمان ۵ طبقه مسکونی پردیس",
            code = "BLD-1405",
            siteLocation = "تهران، منطقه ۵، خیابان سازمان آب",
            managerName = "مهندس علیرضا رضایی",
            startDate = start,
            finishDate = finish,
            status = "در حال اجرا",
            baselineCost = 14500000000.0,
            notes = "اسکلت بتن آرمه با سقف تیرچه و وافل، زیربنای کل ۲۴۰۰ متر مربع"
        )
    }

    fun createSampleResources(projectId: Long): List<ResourceEntity> {
        return listOf(
            ResourceEntity(
                projectId = projectId,
                uid = 1,
                name = "اکیپ آرماتوربند (استادکار + کارگر)",
                type = "نیروی انسانی",
                unit = "نفر",
                countAvailable = 8,
                dailyRate = 1800000.0,
                supervisorName = "استاد مرادی",
                phoneContact = "09121112233"
            ),
            ResourceEntity(
                projectId = projectId,
                uid = 2,
                name = "اکیپ قالب‌بند فلزی و چوبی",
                type = "نیروی انسانی",
                unit = "نفر",
                countAvailable = 6,
                dailyRate = 1750000.0,
                supervisorName = "استاد اکبری",
                phoneContact = "09122223344"
            ),
            ResourceEntity(
                projectId = projectId,
                uid = 3,
                name = "کارگر ساده ساختمانی",
                type = "نیروی انسانی",
                unit = "نفر",
                countAvailable = 10,
                dailyRate = 950000.0,
                supervisorName = "مسئول تدارکات",
                phoneContact = "09123334455"
            ),
            ResourceEntity(
                projectId = projectId,
                uid = 4,
                name = "اکیپ بتن‌ریزی و پمپاژ",
                type = "نیروی انسانی",
                unit = "نفر",
                countAvailable = 5,
                dailyRate = 2000000.0,
                supervisorName = "شرکت بتن آماده",
                phoneContact = "09124445566"
            ),
            ResourceEntity(
                projectId = projectId,
                uid = 5,
                name = "اکیپ بنا و دیوارچین (هبلکس)",
                type = "نیروی انسانی",
                unit = "نفر",
                countAvailable = 4,
                dailyRate = 1600000.0,
                supervisorName = "استاد رحمانی",
                phoneContact = "09125556677"
            ),
            ResourceEntity(
                projectId = projectId,
                uid = 6,
                name = "اکیپ برقکار و سیم‌کشی",
                type = "نیروی انسانی",
                unit = "نفر",
                countAvailable = 3,
                dailyRate = 1700000.0,
                supervisorName = "مهندس حسینی",
                phoneContact = "09126667788"
            ),
            ResourceEntity(
                projectId = projectId,
                uid = 7,
                name = "اکیپ لوله‌کش آب و فاضلاب",
                type = "نیروی انسانی",
                unit = "نفر",
                countAvailable = 3,
                dailyRate = 1700000.0,
                supervisorName = "استاد صادقی",
                phoneContact = "09127778899"
            ),
            ResourceEntity(
                projectId = projectId,
                uid = 8,
                name = "تاور کرین (جرثقیل برجی)",
                type = "ماشین‌آلات",
                unit = "دستگاه",
                countAvailable = 1,
                dailyRate = 4500000.0,
                supervisorName = "اپراتور کریمی",
                phoneContact = "09128889900"
            ),
            ResourceEntity(
                projectId = projectId,
                uid = 9,
                name = "بیل مکانیکی کوماتسو و کامیون",
                type = "ماشین‌آلات",
                unit = "دستگاه",
                countAvailable = 2,
                dailyRate = 7000000.0,
                supervisorName = "پیمانکار خاکبرداری",
                phoneContact = "09129990011"
            )
        )
    }

    fun createSampleTasks(projectId: Long): List<TaskEntity> {
        val now = System.currentTimeMillis()
        var cur = DateUtils.addDays(now, -45)

        fun step(days: Int): Pair<Long, Long> {
            val start = cur
            val finish = DateUtils.addDays(start, days)
            cur = finish
            return Pair(start, finish)
        }

        val tasks = mutableListOf<TaskEntity>()
        var uid = 1

        // Phase 1: تخریب و خاکبرداری
        val p1Dates = Pair(DateUtils.addDays(now, -45), DateUtils.addDays(now, -25))
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 1,
                wbs = "1",
                outlineLevel = 1,
                isSummary = true,
                name = "فاز اول: تجهیز کارگاه و خاکبرداری",
                phase = "خاکبرداری و پی‌کنی",
                durationDays = 20,
                startDate = p1Dates.first,
                finishDate = p1Dates.second,
                percentComplete = 100,
                isCritical = true,
                requiredLaborCount = 8,
                sortOrder = 1
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 2,
                wbs = "1.1",
                outlineLevel = 2,
                isSummary = false,
                name = "تجهیز کارگاه و فنس‌کشی و ایمنی کارگاه",
                phase = "خاکبرداری و پی‌کنی",
                durationDays = 5,
                startDate = DateUtils.addDays(now, -45),
                finishDate = DateUtils.addDays(now, -40),
                percentComplete = 100,
                isCritical = true,
                requiredLaborCount = 4,
                assignedTrade = "کارگر ساده",
                sortOrder = 2
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 3,
                wbs = "1.2",
                outlineLevel = 2,
                isSummary = false,
                name = "گودبرداری و خاکبرداری مکانیزه با بیل مکانیکی",
                phase = "خاکبرداری و پی‌کنی",
                durationDays = 10,
                startDate = DateUtils.addDays(now, -40),
                finishDate = DateUtils.addDays(now, -30),
                percentComplete = 100,
                isCritical = true,
                requiredLaborCount = 4,
                assignedTrade = "ماشین‌آلات",
                predecessors = "2FS",
                sortOrder = 3
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 4,
                wbs = "1.3",
                outlineLevel = 2,
                isSummary = false,
                name = "پایدارسازی و نیلینگ دیواره‌های گود",
                phase = "خاکبرداری و پی‌کنی",
                durationDays = 5,
                startDate = DateUtils.addDays(now, -30),
                finishDate = DateUtils.addDays(now, -25),
                percentComplete = 100,
                isCritical = true,
                requiredLaborCount = 6,
                assignedTrade = "اکیپ تخصصی نیلینگ",
                predecessors = "3FS",
                sortOrder = 4
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 5,
                wbs = "1.4",
                outlineLevel = 2,
                isSummary = false,
                name = "نقطه عطف: اتمام کامل گودبرداری و تایید نظام مهندسی",
                phase = "خاکبرداری و پی‌کنی",
                durationDays = 0,
                startDate = DateUtils.addDays(now, -25),
                finishDate = DateUtils.addDays(now, -25),
                percentComplete = 100,
                isMilestone = true,
                isCritical = true,
                requiredLaborCount = 0,
                predecessors = "4FS",
                sortOrder = 5
            )
        )

        // Phase 2: فونداسیون و اسکلت بتنی
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 6,
                wbs = "2",
                outlineLevel = 1,
                isSummary = true,
                name = "فاز دوم: فونداسیون و اسکلت سازه بتنی",
                phase = "فونداسیون و اسکلت",
                durationDays = 55,
                startDate = DateUtils.addDays(now, -25),
                finishDate = DateUtils.addDays(now, 30),
                percentComplete = 68,
                isCritical = true,
                requiredLaborCount = 14,
                sortOrder = 6
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 7,
                wbs = "2.1",
                outlineLevel = 2,
                isSummary = false,
                name = "بتن مگر و خط‌کشی آکس‌ها",
                phase = "فونداسیون و اسکلت",
                durationDays = 3,
                startDate = DateUtils.addDays(now, -25),
                finishDate = DateUtils.addDays(now, -22),
                percentComplete = 100,
                isCritical = true,
                requiredLaborCount = 4,
                assignedTrade = "کارگر ساده",
                sortOrder = 7
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 8,
                wbs = "2.2",
                outlineLevel = 2,
                isSummary = false,
                name = "آرماتوربندی شبکه تحتانی و فوقانی فونداسیون",
                phase = "فونداسیون و اسکلت",
                durationDays = 8,
                startDate = DateUtils.addDays(now, -22),
                finishDate = DateUtils.addDays(now, -14),
                percentComplete = 100,
                isCritical = true,
                requiredLaborCount = 8,
                assignedTrade = "اکیپ آرماتوربند",
                predecessors = "7FS",
                sortOrder = 8
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 9,
                wbs = "2.3",
                outlineLevel = 2,
                isSummary = false,
                name = "قالب‌بندی و بتن‌ریزی یکپارچه فونداسیون رادیه",
                phase = "فونداسیون و اسکلت",
                durationDays = 4,
                startDate = DateUtils.addDays(now, -14),
                finishDate = DateUtils.addDays(now, -10),
                percentComplete = 100,
                isCritical = true,
                requiredLaborCount = 10,
                assignedTrade = "اکیپ بتن‌ریزی",
                predecessors = "8FS",
                sortOrder = 9
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 10,
                wbs = "2.4",
                outlineLevel = 2,
                isSummary = false,
                name = "اسکلت طبقه زیرزمین و همکف (ستون‌ها و سقف)",
                phase = "فونداسیون و اسکلت",
                durationDays = 12,
                startDate = DateUtils.addDays(now, -10),
                finishDate = DateUtils.addDays(now, 2),
                percentComplete = 100,
                isCritical = true,
                requiredLaborCount = 12,
                assignedTrade = "آرماتوربند و قالب‌بند",
                predecessors = "9FS",
                sortOrder = 10
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 11,
                wbs = "2.5",
                outlineLevel = 2,
                isSummary = false,
                name = "ستون‌ها و سقف طبقات اول و دوم (آرماتوربندی و قالب‌بندی)",
                phase = "فونداسیون و اسکلت",
                durationDays = 14,
                startDate = DateUtils.addDays(now, 2),
                finishDate = DateUtils.addDays(now, 16),
                percentComplete = 50,
                isCritical = true,
                requiredLaborCount = 14,
                assignedTrade = "آرماتوربند و قالب‌بند",
                predecessors = "10FS",
                notes = "در حال حاضر آرماتوربندی ستون‌های طبقه ۲ در حال اجراست",
                sortOrder = 11
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 12,
                wbs = "2.6",
                outlineLevel = 2,
                isSummary = false,
                name = "ستون‌ها و سقف طبقات سوم و چهارم و خرپشته",
                phase = "فونداسیون و اسکلت",
                durationDays = 14,
                startDate = DateUtils.addDays(now, 16),
                finishDate = DateUtils.addDays(now, 30),
                percentComplete = 0,
                isCritical = true,
                requiredLaborCount = 12,
                assignedTrade = "آرماتوربند و قالب‌بند",
                predecessors = "11FS",
                sortOrder = 12
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 13,
                wbs = "2.7",
                outlineLevel = 2,
                isSummary = false,
                name = "نقطه عطف: اتمام اسکلت بتنی کامل ساختمان",
                phase = "فونداسیون و اسکلت",
                durationDays = 0,
                startDate = DateUtils.addDays(now, 30),
                finishDate = DateUtils.addDays(now, 30),
                percentComplete = 0,
                isMilestone = true,
                isCritical = true,
                requiredLaborCount = 0,
                predecessors = "12FS",
                sortOrder = 13
            )
        )

        // Phase 3: سفت‌کاری و وال‌پست
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 14,
                wbs = "3",
                outlineLevel = 1,
                isSummary = true,
                name = "فاز سوم: سفت‌کاری، وال‌پست و تیغه‌چینی",
                phase = "سفت‌کاری و دیوارچینی",
                durationDays = 35,
                startDate = DateUtils.addDays(now, 10),
                finishDate = DateUtils.addDays(now, 45),
                percentComplete = 15,
                isCritical = false,
                requiredLaborCount = 8,
                sortOrder = 14
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 15,
                wbs = "3.1",
                outlineLevel = 2,
                isSummary = false,
                name = "نصب و جوشکاری وال‌پست‌ها و وادارها (پیوست ششم)",
                phase = "سفت‌کاری و دیوارچینی",
                durationDays = 12,
                startDate = DateUtils.addDays(now, 10),
                finishDate = DateUtils.addDays(now, 22),
                percentComplete = 35,
                isCritical = false,
                requiredLaborCount = 4,
                assignedTrade = "جوشکار وال‌پست",
                predecessors = "10FS",
                sortOrder = 15
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 16,
                wbs = "3.2",
                outlineLevel = 2,
                isSummary = false,
                name = "تیغه‌چینی دیوارهای پیرامونی و داخلی با بلوک هبلکس",
                phase = "سفت‌کاری و دیوارچینی",
                durationDays = 20,
                startDate = DateUtils.addDays(now, 18),
                finishDate = DateUtils.addDays(now, 38),
                percentComplete = 10,
                isCritical = false,
                requiredLaborCount = 6,
                assignedTrade = "بنا و دیوارچین",
                predecessors = "15SS+4d",
                sortOrder = 16
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 17,
                wbs = "3.3",
                outlineLevel = 2,
                isSummary = false,
                name = "نصب نعل درگاه‌ها و فریم‌های فلزی درها و پنجره‌ها",
                phase = "سفت‌کاری و دیوارچینی",
                durationDays = 7,
                startDate = DateUtils.addDays(now, 38),
                finishDate = DateUtils.addDays(now, 45),
                percentComplete = 0,
                isCritical = false,
                requiredLaborCount = 3,
                assignedTrade = "آهنگر و بنا",
                predecessors = "16FS",
                sortOrder = 17
            )
        )

        // Phase 4: تاسیسات مکانیکی و برقی
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 18,
                wbs = "4",
                outlineLevel = 1,
                isSummary = true,
                name = "فاز چهارم: تاسیسات مکانیکی و الکتریکی",
                phase = "تاسیسات مکانیکی و برقی",
                durationDays = 40,
                startDate = DateUtils.addDays(now, 35),
                finishDate = DateUtils.addDays(now, 75),
                percentComplete = 0,
                isCritical = false,
                requiredLaborCount = 6,
                sortOrder = 18
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 19,
                wbs = "4.1",
                outlineLevel = 2,
                isSummary = false,
                name = "لوله‌کشی آب مصرفی و فاضلاب (پوش‌فیت و پنج‌لایه)",
                phase = "تاسیسات مکانیکی و برقی",
                durationDays = 15,
                startDate = DateUtils.addDays(now, 35),
                finishDate = DateUtils.addDays(now, 50),
                percentComplete = 0,
                isCritical = false,
                requiredLaborCount = 3,
                assignedTrade = "اکیپ لوله‌کش",
                predecessors = "16FS",
                sortOrder = 19
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 20,
                wbs = "4.2",
                outlineLevel = 2,
                isSummary = false,
                name = "لوله‌گذاری برق، قوطی‌گذاری و سیم‌کشی طبقات",
                phase = "تاسیسات مکانیکی و برقی",
                durationDays = 15,
                startDate = DateUtils.addDays(now, 45),
                finishDate = DateUtils.addDays(now, 60),
                percentComplete = 0,
                isCritical = false,
                requiredLaborCount = 3,
                assignedTrade = "اکیپ برقکار",
                predecessors = "19SS+5d",
                sortOrder = 20
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 21,
                wbs = "4.3",
                outlineLevel = 2,
                isSummary = false,
                name = "کانال‌کشی داکت اسپلیت و کانال کولر",
                phase = "تاسیسات مکانیکی و برقی",
                durationDays = 12,
                startDate = DateUtils.addDays(now, 55),
                finishDate = DateUtils.addDays(now, 67),
                percentComplete = 0,
                isCritical = false,
                requiredLaborCount = 2,
                assignedTrade = "کانال‌ساز",
                predecessors = "20SS+5d",
                sortOrder = 21
            )
        )

        // Phase 5: نازک‌کاری و فینیشینگ
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 22,
                wbs = "5",
                outlineLevel = 1,
                isSummary = true,
                name = "فاز پنجم: نازک‌کاری و تزئینات داخلی",
                phase = "نازک‌کاری و فینیشینگ",
                durationDays = 50,
                startDate = DateUtils.addDays(now, 65),
                finishDate = DateUtils.addDays(now, 115),
                percentComplete = 0,
                isCritical = true,
                requiredLaborCount = 10,
                sortOrder = 22
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 23,
                wbs = "5.1",
                outlineLevel = 2,
                isSummary = false,
                name = "گچ و خاک دیوارهای داخلی و سقف‌ها",
                phase = "نازک‌کاری و فینیشینگ",
                durationDays = 18,
                startDate = DateUtils.addDays(now, 65),
                finishDate = DateUtils.addDays(now, 83),
                percentComplete = 0,
                isCritical = true,
                requiredLaborCount = 6,
                assignedTrade = "اکیپ گچ‌کار",
                predecessors = "21FS",
                sortOrder = 23
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 24,
                wbs = "5.2",
                outlineLevel = 2,
                isSummary = false,
                name = "کف‌سازی، شیب‌بندی و عایق‌کاری رطوبتی ایزوگام",
                phase = "نازک‌کاری و فینیشینگ",
                durationDays = 12,
                startDate = DateUtils.addDays(now, 80),
                finishDate = DateUtils.addDays(now, 92),
                percentComplete = 0,
                isCritical = true,
                requiredLaborCount = 4,
                assignedTrade = "بنا و ایزوگام‌کار",
                predecessors = "23SS+10d",
                sortOrder = 24
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 25,
                wbs = "5.3",
                outlineLevel = 2,
                isSummary = false,
                name = "سرامیک کف و کاشی‌کاری سرویس‌ها و آشپزخانه",
                phase = "نازک‌کاری و فینیشینگ",
                durationDays = 16,
                startDate = DateUtils.addDays(now, 92),
                finishDate = DateUtils.addDays(now, 108),
                percentComplete = 0,
                isCritical = true,
                requiredLaborCount = 5,
                assignedTrade = "کاشی‌کار",
                predecessors = "24FS",
                sortOrder = 25
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 26,
                wbs = "5.4",
                outlineLevel = 2,
                isSummary = false,
                name = "سفیدکاری، نقاشی و نصب کناف سقف کاذب",
                phase = "نازک‌کاری و فینیشینگ",
                durationDays = 14,
                startDate = DateUtils.addDays(now, 101),
                finishDate = DateUtils.addDays(now, 115),
                percentComplete = 0,
                isCritical = true,
                requiredLaborCount = 6,
                assignedTrade = "نقاش و کناف‌کار",
                predecessors = "25SS+8d",
                sortOrder = 26
            )
        )

        // Phase 6: نما، آسانسور و تحویل
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 27,
                wbs = "6",
                outlineLevel = 1,
                isSummary = true,
                name = "فاز ششم: نما، آسانسور، محوطه‌سازی و پایان‌کار",
                phase = "نما و محوطه‌سازی",
                durationDays = 45,
                startDate = DateUtils.addDays(now, 115),
                finishDate = DateUtils.addDays(now, 160),
                percentComplete = 0,
                isCritical = true,
                requiredLaborCount = 8,
                sortOrder = 27
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 28,
                wbs = "6.1",
                outlineLevel = 2,
                isSummary = false,
                name = "شاسی‌کشی، سنگ‌کاری و اجرای نمای مدرن ترکیبی",
                phase = "نما و محوطه‌سازی",
                durationDays = 25,
                startDate = DateUtils.addDays(now, 115),
                finishDate = DateUtils.addDays(now, 140),
                percentComplete = 0,
                isCritical = true,
                requiredLaborCount = 5,
                assignedTrade = "نماکار و سنگ‌کار",
                predecessors = "26FS",
                sortOrder = 28
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 29,
                wbs = "6.2",
                outlineLevel = 2,
                isSummary = false,
                name = "آهن‌کشی چاه آسانسور، ریل‌گذاری و نصب موتور و کابین",
                phase = "نما و محوطه‌سازی",
                durationDays = 18,
                startDate = DateUtils.addDays(now, 125),
                finishDate = DateUtils.addDays(now, 143),
                percentComplete = 0,
                isCritical = false,
                requiredLaborCount = 3,
                assignedTrade = "نصاب آسانسور",
                predecessors = "28SS+10d",
                sortOrder = 29
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 30,
                wbs = "6.3",
                outlineLevel = 2,
                isSummary = false,
                name = "محوطه‌سازی، موزاییک پارکینگ، درب ضدسرقت و نصبیات کلیدپریز",
                phase = "نما و محوطه‌سازی",
                durationDays = 15,
                startDate = DateUtils.addDays(now, 143),
                finishDate = DateUtils.addDays(now, 158),
                percentComplete = 0,
                isCritical = true,
                requiredLaborCount = 4,
                assignedTrade = "اکیپ تاسیسات و بنا",
                predecessors = "29FS",
                sortOrder = 30
            )
        )
        tasks.add(
            TaskEntity(
                projectId = projectId,
                uid = uid++,
                idInProject = 31,
                wbs = "6.4",
                outlineLevel = 2,
                isSummary = false,
                name = "نقطه عطف پایانی: اخذ تاییدیه استاندارد، پایان‌کار و تحویل موقت",
                phase = "نما و محوطه‌سازی",
                durationDays = 0,
                startDate = DateUtils.addDays(now, 160),
                finishDate = DateUtils.addDays(now, 160),
                percentComplete = 0,
                isMilestone = true,
                isCritical = true,
                requiredLaborCount = 0,
                predecessors = "30FS",
                sortOrder = 31
            )
        )

        return tasks
    }

    fun createSampleDailyLogs(projectId: Long): List<DailySiteLogEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            DailySiteLogEntity(
                projectId = projectId,
                date = now,
                weather = "آفتابی، صاف و عالی برای بتن‌ریزی",
                temperature = "۲۵°C",
                totalWorkersPresent = 14,
                workersBreakdown = "آرماتوربند: ۶ نفر، قالب‌بند: ۴ نفر، کارگر ساده: ۳ نفر، مهندس ناظر: ۱ نفر",
                machineryActive = "جرثقیل برجی تاور کرین و یک دستگاه قیچی آرماتوربندی",
                completedWorkSummary = "تکمیل آرماتوربندی ستون‌های محور C و D طبقه دوم و آغاز قالب‌بندی ستون‌های جنوبی.",
                delaysAndObstacles = "ترافیک سنگین صبحگاهی مانع ورود به موقع یک سرویس میلگرد شد که تا ظهر رفع گردید.",
                safetyAndHseNotes = "کمربند ایمنی (هارنس) برای کارگران ستون‌ها الزامی شد و چک گردید.",
                reporterName = "مهندس علیرضا رضایی (سرپرست کارگاه)"
            ),
            DailySiteLogEntity(
                projectId = projectId,
                date = DateUtils.addDays(now, -1),
                weather = "نیمه ابری با وزش باد ملایم",
                temperature = "۲۲°C",
                totalWorkersPresent = 12,
                workersBreakdown = "آرماتوربند: ۶ نفر، جوشکار وال‌پست: ۲ نفر، کارگر ساده: ۴ نفر",
                machineryActive = "تاور کرین و ترانس جوشکاری",
                completedWorkSummary = "خاموت‌گذاری ستون‌های طبقه دوم و جوشکاری وادارهای میانی در طبقه اول.",
                delaysAndObstacles = "بدون تاخیر یا مانع.",
                safetyAndHseNotes = "کپسول آتش‌نشانی در مجاورت محل جوشکاری مستقر شد.",
                reporterName = "دفتر فنی کارگاه"
            )
        )
    }
}
