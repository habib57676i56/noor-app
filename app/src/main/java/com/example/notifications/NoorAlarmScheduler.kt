package com.example.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.local.prefs.UserPreferences
import com.example.domain.model.AppLanguage
import com.example.domain.model.PrayerName
import com.example.domain.prayer.PrayerTimeCalculator
import java.util.Calendar
import java.util.TimeZone

object NoorAlarmScheduler {

    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_BODY = "extra_body"
    const val EXTRA_CHANNEL = "extra_channel"
    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

    private const val REQ_PRAYER_BASE = 2000
    private const val REQ_PRE_PRAYER_BASE = 2100
    private const val REQ_MORNING_ADHKAR = 2201
    private const val REQ_EVENING_ADHKAR = 2202
    private const val REQ_QURAN_REMINDER = 2203
    private const val REQ_TASBIH_REMINDER = 2204

    fun rescheduleAll(context: Context, prefs: UserPreferences) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val tz = TimeZone.getTimeZone(prefs.timezoneId)
        val now = System.currentTimeMillis()
        val isArabic = prefs.language == AppLanguage.ARABIC

        val todayCal = Calendar.getInstance(tz)
        val tomorrowCal = (todayCal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }

        val todaySchedule = PrayerTimeCalculator.calculateForCalendar(
            todayCal, prefs.latitude, prefs.longitude, prefs.calculationMethod, prefs.asrMadhhab, prefs.offsets
        )
        val tomorrowSchedule = PrayerTimeCalculator.calculateForCalendar(
            tomorrowCal, prefs.latitude, prefs.longitude, prefs.calculationMethod, prefs.asrMadhhab, prefs.offsets
        )

        // Schedule each Fard prayer & optional pre-prayer alert
        PrayerName.fardPrayers.forEachIndexed { idx, prayer ->
            val exactReq = REQ_PRAYER_BASE + idx
            val preReq = REQ_PRE_PRAYER_BASE + idx

            if (prefs.isPrayerNotificationEnabled(prayer)) {
                val targetTime = if (todaySchedule.timeFor(prayer) > now + 5_000L) {
                    todaySchedule.timeFor(prayer)
                } else {
                    tomorrowSchedule.timeFor(prayer)
                }
                val pNameAr = prayerNameAr(prayer)
                val pNameEn = prayerNameEn(prayer)

                scheduleSingleAlarm(
                    context = context,
                    alarmManager = alarmManager,
                    requestCode = exactReq,
                    triggerAtMillis = targetTime,
                    channelId = NoorNotificationHelper.CHANNEL_PRAYER,
                    title = if (isArabic) "حان الآن موعد صلاة $pNameAr" else "Time for $pNameEn Prayer",
                    body = if (isArabic) "حيّ على الصلاة — ${prefs.cityNameAr}" else "It is time for $pNameEn in ${prefs.cityNameEn}"
                )

                if (prefs.prePrayerAlertMinutes > 0) {
                    val preTime = targetTime - prefs.prePrayerAlertMinutes * 60_000L
                    if (preTime > now + 5_000L) {
                        scheduleSingleAlarm(
                            context = context,
                            alarmManager = alarmManager,
                            requestCode = preReq,
                            triggerAtMillis = preTime,
                            channelId = NoorNotificationHelper.CHANNEL_PRAYER,
                            title = if (isArabic) "تذكير قبل صلاة $pNameAr" else "Upcoming $pNameEn Prayer",
                            body = if (isArabic) {
                                "متبقي ${prefs.prePrayerAlertMinutes} دقيقة على دخول وقت صلاة $pNameAr"
                            } else {
                                "${prefs.prePrayerAlertMinutes} minutes remaining until $pNameEn"
                            }
                        )
                    } else {
                        cancelAlarm(context, alarmManager, preReq)
                    }
                } else {
                    cancelAlarm(context, alarmManager, preReq)
                }
            } else {
                cancelAlarm(context, alarmManager, exactReq)
                cancelAlarm(context, alarmManager, preReq)
            }
        }

        // Daily Adhkar, Quran, and Tasbih reminders
        scheduleDailyReminder(
            context, alarmManager, REQ_MORNING_ADHKAR,
            prefs.morningAdhkarReminderEnabled, prefs.morningAdhkarHour, prefs.morningAdhkarMinute,
            if (isArabic) "أذكار الصباح" else "Morning Adhkar",
            if (isArabic) "ابدأ يومك بحصن المسلم وذكر الله" else "Begin your morning with remembrance of Allah"
        )
        scheduleDailyReminder(
            context, alarmManager, REQ_EVENING_ADHKAR,
            prefs.eveningAdhkarReminderEnabled, prefs.eveningAdhkarHour, prefs.eveningAdhkarMinute,
            if (isArabic) "أذكار المساء" else "Evening Adhkar",
            if (isArabic) "حافظ على ورد أذكار المساء والسكينة" else "Keep your evening Adhkar routine"
        )
        scheduleDailyReminder(
            context, alarmManager, REQ_QURAN_REMINDER,
            prefs.quranReminderEnabled, prefs.quranReminderHour, prefs.quranReminderMinute,
            if (isArabic) "ورد القرآن اليومي" else "Daily Quran Reading",
            if (isArabic) "خصّص دقائق لتلاوة كتاب الله اليوم" else "Take a peaceful moment to read the Holy Quran"
        )
        scheduleDailyReminder(
            context, alarmManager, REQ_TASBIH_REMINDER,
            prefs.tasbihReminderEnabled, prefs.tasbihReminderHour, prefs.tasbihReminderMinute,
            if (isArabic) "تذكير التسبيح" else "Tasbih Reminder",
            if (isArabic) "سبحان الله والحمد لله والله أكبر" else "SubhanAllah, Alhamdulillah, Allahu Akbar"
        )
    }

    private fun scheduleDailyReminder(
        context: Context,
        alarmManager: AlarmManager,
        requestCode: Int,
        enabled: Boolean,
        hour: Int,
        minute: Int,
        title: String,
        body: String
    ) {
        if (!enabled) {
            cancelAlarm(context, alarmManager, requestCode)
            return
        }
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis() + 5_000L) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        scheduleSingleAlarm(
            context = context,
            alarmManager = alarmManager,
            requestCode = requestCode,
            triggerAtMillis = cal.timeInMillis,
            channelId = NoorNotificationHelper.CHANNEL_REMINDER,
            title = title,
            body = body
        )
    }

    private fun scheduleSingleAlarm(
        context: Context,
        alarmManager: AlarmManager,
        requestCode: Int,
        triggerAtMillis: Long,
        channelId: String,
        title: String,
        body: String
    ) {
        val intent = Intent(context, NoorAlarmReceiver::class.java).apply {
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_BODY, body)
            putExtra(EXTRA_CHANNEL, channelId)
            putExtra(EXTRA_NOTIFICATION_ID, requestCode)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val canUseExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        try {
            if (canUseExact) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    private fun cancelAlarm(context: Context, alarmManager: AlarmManager, requestCode: Int) {
        val intent = Intent(context, NoorAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun prayerNameAr(prayer: PrayerName): String = when (prayer) {
        PrayerName.FAJR -> "الفجر"
        PrayerName.SUNRISE -> "الشروق"
        PrayerName.DHUHR -> "الظهر"
        PrayerName.ASR -> "العصر"
        PrayerName.MAGHRIB -> "المغرب"
        PrayerName.ISHA -> "العشاء"
    }

    private fun prayerNameEn(prayer: PrayerName): String = when (prayer) {
        PrayerName.FAJR -> "Fajr"
        PrayerName.SUNRISE -> "Sunrise"
        PrayerName.DHUHR -> "Dhuhr"
        PrayerName.ASR -> "Asr"
        PrayerName.MAGHRIB -> "Maghrib"
        PrayerName.ISHA -> "Isha"
    }
}
