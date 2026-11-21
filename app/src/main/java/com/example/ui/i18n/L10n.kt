package com.example.ui.i18n

import androidx.compose.runtime.compositionLocalOf
import com.example.domain.model.AppLanguage
import com.example.domain.model.PrayerName

val LocalAppLanguage = compositionLocalOf { AppLanguage.ARABIC }

object L10n {
    fun pick(lang: AppLanguage, ar: String, en: String): String =
        if (lang == AppLanguage.ARABIC) ar else en

    fun prayerName(prayer: PrayerName, lang: AppLanguage): String = when (prayer) {
        PrayerName.FAJR -> pick(lang, "الفجر", "Fajr")
        PrayerName.SUNRISE -> pick(lang, "الشروق", "Sunrise")
        PrayerName.DHUHR -> pick(lang, "الظهر", "Dhuhr")
        PrayerName.ASR -> pick(lang, "العصر", "Asr")
        PrayerName.MAGHRIB -> pick(lang, "المغرب", "Maghrib")
        PrayerName.ISHA -> pick(lang, "العشاء", "Isha")
    }

    fun formatCountdown(remainingMillis: Long, lang: AppLanguage): String {
        val totalSeconds = (remainingMillis / 1000L).coerceAtLeast(0L)
        val hours = totalSeconds / 3600L
        val minutes = (totalSeconds % 3600L) / 60L
        val seconds = totalSeconds % 60L
        return String.format(java.util.Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun formatTime12h(millis: Long, timeZoneId: String, lang: AppLanguage): String {
        val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone(timeZoneId)).apply {
            timeInMillis = millis
        }
        val hour24 = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = cal.get(java.util.Calendar.MINUTE)
        val isPm = hour24 >= 12
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }
        val period = if (lang == AppLanguage.ARABIC) {
            if (isPm) "م" else "ص"
        } else {
            if (isPm) "PM" else "AM"
        }
        return String.format(java.util.Locale.US, "%d:%02d %s", hour12, minute, period)
    }
}
