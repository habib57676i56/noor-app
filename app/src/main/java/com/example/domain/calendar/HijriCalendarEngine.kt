package com.example.domain.calendar

import android.os.Build
import com.example.domain.model.HijriDateModel
import com.example.domain.model.IslamicOccasion
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField
import java.util.Calendar
import kotlin.math.floor

data class CalendarDayCell(
    val gregorianYear: Int,
    val gregorianMonth: Int,
    val gregorianDay: Int,
    val dayOfWeek: Int, // Calendar.SUNDAY .. SATURDAY
    val hijri: HijriDateModel,
    val isToday: Boolean,
    val isWhiteDay: Boolean,
    val isSunnahFastDay: Boolean,
    val isMonthStart: Boolean,
    val occasionNameAr: String? = null,
    val occasionNameEn: String? = null
)

object HijriCalendarEngine {

    val hijriMonthsAr = listOf(
        "محرّم", "صفر", "ربيع الأول", "ربيع الآخر",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوّال", "ذو القعدة", "ذو الحجة"
    )

    val hijriMonthsEn = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Ula", "Jumada al-Akhirah", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    val gregorianMonthsAr = listOf(
        "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
        "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )

    val gregorianMonthsEn = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val weekDaysAr = listOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
    val weekDaysEn = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    private data class OccasionDef(
        val id: String,
        val month: Int,
        val day: Int,
        val nameAr: String,
        val nameEn: String,
        val descAr: String,
        val descEn: String
    )

    private val occasions = listOf(
        OccasionDef("new_year", 1, 1, "رأس السنة الهجرية", "Islamic New Year", "بداية العام الهجري الجديد وذكرى الهجرة النبوية.", "Beginning of the new Hijri year."),
        OccasionDef("ashura", 1, 10, "يوم عاشوراء", "Day of Ashura", "يُستحب صيامه؛ يكفّر السنة التي قبله.", "Recommended fasting day expiating the previous year."),
        OccasionDef("mawlid", 3, 12, "ذكرى المولد النبوي", "Prophet's Birthday", "ذكرى مولد النبي محمد ﷺ في ربيع الأول.", "Commemoration of the birth of Prophet Muhammad (PBUH)."),
        OccasionDef("isra_miraj", 7, 27, "الإسراء والمعراج", "Isra and Mi'raj", "ذكرى رحلة الإسراء والمعراج وفرض الصلوات الخمس.", "Night Journey and Ascension when five daily prayers were prescribed."),
        OccasionDef("shaban_mid", 8, 15, "ليلة النصف من شعبان", "Mid-Sha'ban", "ليلة مباركة ترفع فيها الأعمال ويُستحب فيها الدعاء.", "Blessed night of forgiveness and supplication."),
        OccasionDef("ramadan_start", 9, 1, "بداية شهر رمضان", "Start of Ramadan", "أول أيام شهر الصيام والقرآن والرحمة.", "First day of the blessed month of fasting and Quran."),
        OccasionDef("laylat_qadr", 9, 27, "ليلة القدر (المرجوة)", "Laylat al-Qadr (27th)", "ليلة خير من ألف شهر في العشر الأواخر من رمضان.", "Better than a thousand months in the last ten nights of Ramadan."),
        OccasionDef("eid_fitr", 10, 1, "عيد الفطر المبارك", "Eid al-Fitr", "يوم الجائزة والفرح بعد إتمام صيام رمضان.", "Festival of Breaking the Fast following Ramadan."),
        OccasionDef("arafah", 12, 9, "يوم عرفة", "Day of Arafah", "أعظم أيام الدعاء، وصيامه لغير الحاج يكفر سنتين.", "Supreme day of supplication; fasting expiates two years."),
        OccasionDef("eid_adha", 12, 10, "عيد الأضحى المبارك", "Eid al-Adha", "يوم النحر الأكبر وموسم الحج والأضاحي.", "Festival of Sacrifice during the season of Hajj.")
    )

    fun gregorianToHijri(
        year: Int,
        month: Int, // 1..12
        day: Int,   // 1..31
        dayAdjustment: Int = 0
    ): HijriDateModel {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            add(Calendar.DAY_OF_MONTH, dayAdjustment)
        }
        val adjY = cal.get(Calendar.YEAR)
        val adjM = cal.get(Calendar.MONTH) + 1
        val adjD = cal.get(Calendar.DAY_OF_MONTH)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val localDate = LocalDate.of(adjY, adjM, adjD)
                val hijrah = HijrahDate.from(localDate)
                val hYear = hijrah.get(ChronoField.YEAR)
                val hMonth = hijrah.get(ChronoField.MONTH_OF_YEAR)
                val hDay = hijrah.get(ChronoField.DAY_OF_MONTH)
                HijriDateModel(
                    year = hYear,
                    month = hMonth,
                    day = hDay,
                    monthNameAr = hijriMonthsAr.getOrElse(hMonth - 1) { "" },
                    monthNameEn = hijriMonthsEn.getOrElse(hMonth - 1) { "" }
                )
            } catch (_: Exception) {
                kuwaitiFallback(adjY, adjM, adjD)
            }
        } else {
            kuwaitiFallback(adjY, adjM, adjD)
        }
    }

    private fun kuwaitiFallback(year: Int, month: Int, day: Int): HijriDateModel {
        val m = if (month <= 2) month + 12 else month
        val y = if (month <= 2) year - 1 else year
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        val jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5

        val l0 = jd - 1948439.5 + 10632.0
        val n = floor((l0 - 1.0) / 10631.0)
        val l1 = l0 - 10631.0 * n + 354.0
        val j = floor((10985.0 - l1) / 5316.0) * floor((50.0 * l1) / 17719.0) +
            floor(l1 / 5670.0) * floor((43.0 * l1) / 15238.0)
        val l2 = l1 - floor((30.0 - j) / 15.0) * floor((17719.0 * j) / 50.0) -
            floor(j / 16.0) * floor((15238.0 * j) / 43.0) + 29.0
        val hMonth = floor((24.0 * l2) / 709.0).toInt().coerceIn(1, 12)
        val hDay = (l2 - floor((709.0 * hMonth) / 24.0)).toInt().coerceIn(1, 30)
        val hYear = (30.0 * n + j - 30.0).toInt()

        return HijriDateModel(
            year = hYear,
            month = hMonth,
            day = hDay,
            monthNameAr = hijriMonthsAr.getOrElse(hMonth - 1) { "" },
            monthNameEn = hijriMonthsEn.getOrElse(hMonth - 1) { "" }
        )
    }

    fun buildMonthCells(
        gregorianYear: Int,
        gregorianMonth: Int, // 1..12
        dayAdjustment: Int
    ): List<CalendarDayCell> {
        val today = Calendar.getInstance()
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, gregorianYear)
            set(Calendar.MONTH, gregorianMonth - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val cells = ArrayList<CalendarDayCell>(daysInMonth)

        for (day in 1..daysInMonth) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dow = cal.get(Calendar.DAY_OF_WEEK)
            val hijri = gregorianToHijri(gregorianYear, gregorianMonth, day, dayAdjustment)
            val occ = occasions.firstOrNull { it.month == hijri.month && it.day == hijri.day }
            val isWhiteDay = hijri.day in 13..15
            val isMonOrThu = dow == Calendar.MONDAY || dow == Calendar.THURSDAY
            val isSpecialFast = (hijri.month == 1 && hijri.day in 9..10) || (hijri.month == 12 && hijri.day == 9)
            val isEid = (hijri.month == 10 && hijri.day == 1) || (hijri.month == 12 && hijri.day in 10..13)

            cells.add(
                CalendarDayCell(
                    gregorianYear = gregorianYear,
                    gregorianMonth = gregorianMonth,
                    gregorianDay = day,
                    dayOfWeek = dow,
                    hijri = hijri,
                    isToday = today.get(Calendar.YEAR) == gregorianYear &&
                        (today.get(Calendar.MONTH) + 1) == gregorianMonth &&
                        today.get(Calendar.DAY_OF_MONTH) == day,
                    isWhiteDay = isWhiteDay && !isEid,
                    isSunnahFastDay = (isWhiteDay || isMonOrThu || isSpecialFast) && !isEid && hijri.month != 9,
                    isMonthStart = hijri.day == 1,
                    occasionNameAr = occ?.nameAr,
                    occasionNameEn = occ?.nameEn
                )
            )
        }
        return cells
    }

    fun getUpcomingOccasions(dayAdjustment: Int): List<IslamicOccasion> {
        val now = Calendar.getInstance()
        val todayHijri = gregorianToHijri(
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH) + 1,
            now.get(Calendar.DAY_OF_MONTH),
            dayAdjustment
        )
        val todayDoy = (todayHijri.month - 1) * 29.53 + todayHijri.day

        return occasions.map { def ->
            val targetDoy = (def.month - 1) * 29.53 + def.day
            var diff = (targetDoy - todayDoy).toInt()
            if (diff < 0) diff += 354
            IslamicOccasion(
                id = def.id,
                nameAr = def.nameAr,
                nameEn = def.nameEn,
                descriptionAr = def.descAr,
                descriptionEn = def.descEn,
                hijriMonth = def.month,
                hijriDay = def.day,
                daysUntil = diff
            )
        }.sortedBy { it.daysUntil }
    }

    fun formatHijri(hijri: HijriDateModel, isArabic: Boolean): String =
        if (isArabic) "${hijri.day} ${hijri.monthNameAr} ${hijri.year} هـ"
        else "${hijri.day} ${hijri.monthNameEn} ${hijri.year} AH"

    fun formatGregorian(cal: Calendar, isArabic: Boolean): String {
        val dow = cal.get(Calendar.DAY_OF_WEEK) - 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        return if (isArabic) {
            "${weekDaysAr.getOrElse(dow) { "" }}، $day ${gregorianMonthsAr.getOrElse(month) { "" }} $year"
        } else {
            "${weekDaysEn.getOrElse(dow) { "" }}, ${gregorianMonthsEn.getOrElse(month) { "" }} $day, $year"
        }
    }
}
