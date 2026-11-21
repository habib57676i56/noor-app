package com.example.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.AppLanguage
import com.example.domain.model.AsrMadhhab
import com.example.domain.model.CalculationMethod
import com.example.domain.model.CityLocation
import com.example.domain.model.PrayerMinuteOffsets
import com.example.domain.model.PrayerName
import com.example.domain.model.QuranReadingTheme
import com.example.domain.model.QuranReciter
import com.example.domain.model.ThemeMode
import com.example.domain.prayer.CityCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.noorDataStore: DataStore<Preferences> by preferencesDataStore(name = "noor_settings")

data class UserPreferences(
    val language: AppLanguage = AppLanguage.ARABIC,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val cityId: String = CityCatalog.defaultCity.id,
    val cityNameAr: String = CityCatalog.defaultCity.nameAr,
    val cityNameEn: String = CityCatalog.defaultCity.nameEn,
    val countryAr: String = CityCatalog.defaultCity.countryAr,
    val countryEn: String = CityCatalog.defaultCity.countryEn,
    val latitude: Double = CityCatalog.defaultCity.latitude,
    val longitude: Double = CityCatalog.defaultCity.longitude,
    val timezoneId: String = CityCatalog.defaultCity.timezoneId,
    val isAutoLocation: Boolean = false,
    val calculationMethod: CalculationMethod = CalculationMethod.UMM_AL_QURA,
    val asrMadhhab: AsrMadhhab = AsrMadhhab.SHAFI,
    val offsets: PrayerMinuteOffsets = PrayerMinuteOffsets(),
    val hijriAdjustmentDays: Int = 0,
    // Notifications
    val notifyFajr: Boolean = true,
    val notifyDhuhr: Boolean = true,
    val notifyAsr: Boolean = true,
    val notifyMaghrib: Boolean = true,
    val notifyIsha: Boolean = true,
    val prePrayerAlertMinutes: Int = 10,
    val morningAdhkarReminderEnabled: Boolean = true,
    val morningAdhkarHour: Int = 6,
    val morningAdhkarMinute: Int = 30,
    val eveningAdhkarReminderEnabled: Boolean = true,
    val eveningAdhkarHour: Int = 17,
    val eveningAdhkarMinute: Int = 30,
    val quranReminderEnabled: Boolean = true,
    val quranReminderHour: Int = 21,
    val quranReminderMinute: Int = 0,
    val tasbihReminderEnabled: Boolean = false,
    val tasbihReminderHour: Int = 15,
    val tasbihReminderMinute: Int = 0,
    // Audio & Haptics
    val tasbihVibrationEnabled: Boolean = true,
    val tasbihSoundEnabled: Boolean = true,
    val activeTasbihPresetId: String = "subhanallah",
    val activeTasbihCount: Int = 0,
    val activeTasbihTarget: Int = 33,
    val customTasbihText: String = "سبحان الله وبحمده",
    // Quran Reader
    val lastReadSurah: Int = 1,
    val lastReadAyah: Int = 1,
    val quranFontSizeSp: Int = 24,
    val quranReadingTheme: QuranReadingTheme = QuranReadingTheme.AUTO,
    val quranReciter: QuranReciter = QuranReciter.ALAFASY
) {
    fun isPrayerNotificationEnabled(prayer: PrayerName): Boolean = when (prayer) {
        PrayerName.FAJR -> notifyFajr
        PrayerName.SUNRISE -> false
        PrayerName.DHUHR -> notifyDhuhr
        PrayerName.ASR -> notifyAsr
        PrayerName.MAGHRIB -> notifyMaghrib
        PrayerName.ISHA -> notifyIsha
    }
}

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val CITY_ID = stringPreferencesKey("city_id")
        val CITY_NAME_AR = stringPreferencesKey("city_name_ar")
        val CITY_NAME_EN = stringPreferencesKey("city_name_en")
        val COUNTRY_AR = stringPreferencesKey("country_ar")
        val COUNTRY_EN = stringPreferencesKey("country_en")
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
        val TIMEZONE_ID = stringPreferencesKey("timezone_id")
        val IS_AUTO_LOCATION = booleanPreferencesKey("is_auto_location")
        val CALC_METHOD = stringPreferencesKey("calc_method")
        val ASR_MADHHAB = stringPreferencesKey("asr_madhhab")
        val OFFSET_FAJR = intPreferencesKey("offset_fajr")
        val OFFSET_SUNRISE = intPreferencesKey("offset_sunrise")
        val OFFSET_DHUHR = intPreferencesKey("offset_dhuhr")
        val OFFSET_ASR = intPreferencesKey("offset_asr")
        val OFFSET_MAGHRIB = intPreferencesKey("offset_maghrib")
        val OFFSET_ISHA = intPreferencesKey("offset_isha")
        val HIJRI_ADJUST = intPreferencesKey("hijri_adjust")

        val NOTIFY_FAJR = booleanPreferencesKey("notify_fajr")
        val NOTIFY_DHUHR = booleanPreferencesKey("notify_dhuhr")
        val NOTIFY_ASR = booleanPreferencesKey("notify_asr")
        val NOTIFY_MAGHRIB = booleanPreferencesKey("notify_maghrib")
        val NOTIFY_ISHA = booleanPreferencesKey("notify_isha")
        val PRE_PRAYER_MIN = intPreferencesKey("pre_prayer_min")
        val REMIND_MORNING = booleanPreferencesKey("remind_morning")
        val REMIND_MORNING_H = intPreferencesKey("remind_morning_h")
        val REMIND_MORNING_M = intPreferencesKey("remind_morning_m")
        val REMIND_EVENING = booleanPreferencesKey("remind_evening")
        val REMIND_EVENING_H = intPreferencesKey("remind_evening_h")
        val REMIND_EVENING_M = intPreferencesKey("remind_evening_m")
        val REMIND_QURAN = booleanPreferencesKey("remind_quran")
        val REMIND_QURAN_H = intPreferencesKey("remind_quran_h")
        val REMIND_QURAN_M = intPreferencesKey("remind_quran_m")
        val REMIND_TASBIH = booleanPreferencesKey("remind_tasbih")
        val REMIND_TASBIH_H = intPreferencesKey("remind_tasbih_h")
        val REMIND_TASBIH_M = intPreferencesKey("remind_tasbih_m")

        val TASBIH_VIBRATION = booleanPreferencesKey("tasbih_vibration")
        val TASBIH_SOUND = booleanPreferencesKey("tasbih_sound")
        val ACTIVE_TASBIH_PRESET = stringPreferencesKey("active_tasbih_preset")
        val ACTIVE_TASBIH_COUNT = intPreferencesKey("active_tasbih_count")
        val ACTIVE_TASBIH_TARGET = intPreferencesKey("active_tasbih_target")
        val CUSTOM_TASBIH_TEXT = stringPreferencesKey("custom_tasbih_text")

        val LAST_READ_SURAH = intPreferencesKey("last_read_surah")
        val LAST_READ_AYAH = intPreferencesKey("last_read_ayah")
        val QURAN_FONT_SIZE = intPreferencesKey("quran_font_size")
        val QURAN_READING_THEME = stringPreferencesKey("quran_reading_theme")
        val QURAN_RECITER = stringPreferencesKey("quran_reciter")
    }

    val preferencesFlow: Flow<UserPreferences> = context.noorDataStore.data.map { p ->
        UserPreferences(
            language = AppLanguage.fromCode(p[Keys.LANGUAGE]),
            themeMode = ThemeMode.fromKey(p[Keys.THEME_MODE]),
            cityId = p[Keys.CITY_ID] ?: CityCatalog.defaultCity.id,
            cityNameAr = p[Keys.CITY_NAME_AR] ?: CityCatalog.defaultCity.nameAr,
            cityNameEn = p[Keys.CITY_NAME_EN] ?: CityCatalog.defaultCity.nameEn,
            countryAr = p[Keys.COUNTRY_AR] ?: CityCatalog.defaultCity.countryAr,
            countryEn = p[Keys.COUNTRY_EN] ?: CityCatalog.defaultCity.countryEn,
            latitude = p[Keys.LATITUDE] ?: CityCatalog.defaultCity.latitude,
            longitude = p[Keys.LONGITUDE] ?: CityCatalog.defaultCity.longitude,
            timezoneId = p[Keys.TIMEZONE_ID] ?: CityCatalog.defaultCity.timezoneId,
            isAutoLocation = p[Keys.IS_AUTO_LOCATION] ?: false,
            calculationMethod = CalculationMethod.fromKey(p[Keys.CALC_METHOD]),
            asrMadhhab = AsrMadhhab.fromKey(p[Keys.ASR_MADHHAB]),
            offsets = PrayerMinuteOffsets(
                fajr = p[Keys.OFFSET_FAJR] ?: 0,
                sunrise = p[Keys.OFFSET_SUNRISE] ?: 0,
                dhuhr = p[Keys.OFFSET_DHUHR] ?: 0,
                asr = p[Keys.OFFSET_ASR] ?: 0,
                maghrib = p[Keys.OFFSET_MAGHRIB] ?: 0,
                isha = p[Keys.OFFSET_ISHA] ?: 0
            ),
            hijriAdjustmentDays = p[Keys.HIJRI_ADJUST] ?: 0,
            notifyFajr = p[Keys.NOTIFY_FAJR] ?: true,
            notifyDhuhr = p[Keys.NOTIFY_DHUHR] ?: true,
            notifyAsr = p[Keys.NOTIFY_ASR] ?: true,
            notifyMaghrib = p[Keys.NOTIFY_MAGHRIB] ?: true,
            notifyIsha = p[Keys.NOTIFY_ISHA] ?: true,
            prePrayerAlertMinutes = p[Keys.PRE_PRAYER_MIN] ?: 10,
            morningAdhkarReminderEnabled = p[Keys.REMIND_MORNING] ?: true,
            morningAdhkarHour = p[Keys.REMIND_MORNING_H] ?: 6,
            morningAdhkarMinute = p[Keys.REMIND_MORNING_M] ?: 30,
            eveningAdhkarReminderEnabled = p[Keys.REMIND_EVENING] ?: true,
            eveningAdhkarHour = p[Keys.REMIND_EVENING_H] ?: 17,
            eveningAdhkarMinute = p[Keys.REMIND_EVENING_M] ?: 30,
            quranReminderEnabled = p[Keys.REMIND_QURAN] ?: true,
            quranReminderHour = p[Keys.REMIND_QURAN_H] ?: 21,
            quranReminderMinute = p[Keys.REMIND_QURAN_M] ?: 0,
            tasbihReminderEnabled = p[Keys.REMIND_TASBIH] ?: false,
            tasbihReminderHour = p[Keys.REMIND_TASBIH_H] ?: 15,
            tasbihReminderMinute = p[Keys.REMIND_TASBIH_M] ?: 0,
            tasbihVibrationEnabled = p[Keys.TASBIH_VIBRATION] ?: true,
            tasbihSoundEnabled = p[Keys.TASBIH_SOUND] ?: true,
            activeTasbihPresetId = p[Keys.ACTIVE_TASBIH_PRESET] ?: "subhanallah",
            activeTasbihCount = p[Keys.ACTIVE_TASBIH_COUNT] ?: 0,
            activeTasbihTarget = p[Keys.ACTIVE_TASBIH_TARGET] ?: 33,
            customTasbihText = p[Keys.CUSTOM_TASBIH_TEXT] ?: "سبحان الله وبحمده",
            lastReadSurah = p[Keys.LAST_READ_SURAH] ?: 1,
            lastReadAyah = p[Keys.LAST_READ_AYAH] ?: 1,
            quranFontSizeSp = p[Keys.QURAN_FONT_SIZE] ?: 24,
            quranReadingTheme = QuranReadingTheme.fromKey(p[Keys.QURAN_READING_THEME]),
            quranReciter = QuranReciter.fromId(p[Keys.QURAN_RECITER])
        )
    }

    suspend fun getPreferencesOnce(): UserPreferences = preferencesFlow.first()

    suspend fun setLanguage(language: AppLanguage) {
        context.noorDataStore.edit { it[Keys.LANGUAGE] = language.code }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.noorDataStore.edit { it[Keys.THEME_MODE] = mode.key }
    }

    suspend fun setCity(city: CityLocation, isAuto: Boolean = false) {
        context.noorDataStore.edit {
            it[Keys.CITY_ID] = city.id
            it[Keys.CITY_NAME_AR] = city.nameAr
            it[Keys.CITY_NAME_EN] = city.nameEn
            it[Keys.COUNTRY_AR] = city.countryAr
            it[Keys.COUNTRY_EN] = city.countryEn
            it[Keys.LATITUDE] = city.latitude
            it[Keys.LONGITUDE] = city.longitude
            it[Keys.TIMEZONE_ID] = city.timezoneId
            it[Keys.IS_AUTO_LOCATION] = isAuto
        }
    }

    suspend fun setCalculationMethod(method: CalculationMethod) {
        context.noorDataStore.edit { it[Keys.CALC_METHOD] = method.key }
    }

    suspend fun setAsrMadhhab(madhhab: AsrMadhhab) {
        context.noorDataStore.edit { it[Keys.ASR_MADHHAB] = madhhab.key }
    }

    suspend fun setPrayerOffset(prayer: PrayerName, offsetMinutes: Int) {
        val clamped = offsetMinutes.coerceIn(-30, 30)
        context.noorDataStore.edit {
            when (prayer) {
                PrayerName.FAJR -> it[Keys.OFFSET_FAJR] = clamped
                PrayerName.SUNRISE -> it[Keys.OFFSET_SUNRISE] = clamped
                PrayerName.DHUHR -> it[Keys.OFFSET_DHUHR] = clamped
                PrayerName.ASR -> it[Keys.OFFSET_ASR] = clamped
                PrayerName.MAGHRIB -> it[Keys.OFFSET_MAGHRIB] = clamped
                PrayerName.ISHA -> it[Keys.OFFSET_ISHA] = clamped
            }
        }
    }

    suspend fun setHijriAdjustment(days: Int) {
        context.noorDataStore.edit { it[Keys.HIJRI_ADJUST] = days.coerceIn(-2, 2) }
    }

    suspend fun setPrayerNotification(prayer: PrayerName, enabled: Boolean) {
        context.noorDataStore.edit {
            when (prayer) {
                PrayerName.FAJR -> it[Keys.NOTIFY_FAJR] = enabled
                PrayerName.DHUHR -> it[Keys.NOTIFY_DHUHR] = enabled
                PrayerName.ASR -> it[Keys.NOTIFY_ASR] = enabled
                PrayerName.MAGHRIB -> it[Keys.NOTIFY_MAGHRIB] = enabled
                PrayerName.ISHA -> it[Keys.NOTIFY_ISHA] = enabled
                PrayerName.SUNRISE -> Unit
            }
        }
    }

    suspend fun setPrePrayerAlertMinutes(minutes: Int) {
        context.noorDataStore.edit { it[Keys.PRE_PRAYER_MIN] = minutes.coerceIn(0, 60) }
    }

    suspend fun setReminder(
        type: String,
        enabled: Boolean,
        hour: Int? = null,
        minute: Int? = null
    ) {
        context.noorDataStore.edit {
            when (type) {
                "morning" -> {
                    it[Keys.REMIND_MORNING] = enabled
                    if (hour != null) it[Keys.REMIND_MORNING_H] = hour
                    if (minute != null) it[Keys.REMIND_MORNING_M] = minute
                }
                "evening" -> {
                    it[Keys.REMIND_EVENING] = enabled
                    if (hour != null) it[Keys.REMIND_EVENING_H] = hour
                    if (minute != null) it[Keys.REMIND_EVENING_M] = minute
                }
                "quran" -> {
                    it[Keys.REMIND_QURAN] = enabled
                    if (hour != null) it[Keys.REMIND_QURAN_H] = hour
                    if (minute != null) it[Keys.REMIND_QURAN_M] = minute
                }
                "tasbih" -> {
                    it[Keys.REMIND_TASBIH] = enabled
                    if (hour != null) it[Keys.REMIND_TASBIH_H] = hour
                    if (minute != null) it[Keys.REMIND_TASBIH_M] = minute
                }
            }
        }
    }

    suspend fun setTasbihVibration(enabled: Boolean) {
        context.noorDataStore.edit { it[Keys.TASBIH_VIBRATION] = enabled }
    }

    suspend fun setTasbihSound(enabled: Boolean) {
        context.noorDataStore.edit { it[Keys.TASBIH_SOUND] = enabled }
    }

    suspend fun saveActiveTasbih(presetId: String, count: Int, target: Int, customText: String? = null) {
        context.noorDataStore.edit {
            it[Keys.ACTIVE_TASBIH_PRESET] = presetId
            it[Keys.ACTIVE_TASBIH_COUNT] = count
            it[Keys.ACTIVE_TASBIH_TARGET] = target
            if (customText != null) it[Keys.CUSTOM_TASBIH_TEXT] = customText
        }
    }

    suspend fun saveLastRead(surahNumber: Int, ayahNumber: Int) {
        context.noorDataStore.edit {
            it[Keys.LAST_READ_SURAH] = surahNumber
            it[Keys.LAST_READ_AYAH] = ayahNumber
        }
    }

    suspend fun setQuranFontSize(sizeSp: Int) {
        context.noorDataStore.edit { it[Keys.QURAN_FONT_SIZE] = sizeSp.coerceIn(18, 38) }
    }

    suspend fun setQuranReadingTheme(theme: QuranReadingTheme) {
        context.noorDataStore.edit { it[Keys.QURAN_READING_THEME] = theme.key }
    }

    suspend fun setQuranReciter(reciter: QuranReciter) {
        context.noorDataStore.edit { it[Keys.QURAN_RECITER] = reciter.id }
    }

    suspend fun restoreFromPreferences(prefs: UserPreferences) {
        context.noorDataStore.edit {
            it[Keys.LANGUAGE] = prefs.language.code
            it[Keys.THEME_MODE] = prefs.themeMode.key
            it[Keys.CITY_ID] = prefs.cityId
            it[Keys.CITY_NAME_AR] = prefs.cityNameAr
            it[Keys.CITY_NAME_EN] = prefs.cityNameEn
            it[Keys.COUNTRY_AR] = prefs.countryAr
            it[Keys.COUNTRY_EN] = prefs.countryEn
            it[Keys.LATITUDE] = prefs.latitude
            it[Keys.LONGITUDE] = prefs.longitude
            it[Keys.TIMEZONE_ID] = prefs.timezoneId
            it[Keys.IS_AUTO_LOCATION] = prefs.isAutoLocation
            it[Keys.CALC_METHOD] = prefs.calculationMethod.key
            it[Keys.ASR_MADHHAB] = prefs.asrMadhhab.key
            it[Keys.OFFSET_FAJR] = prefs.offsets.fajr
            it[Keys.OFFSET_SUNRISE] = prefs.offsets.sunrise
            it[Keys.OFFSET_DHUHR] = prefs.offsets.dhuhr
            it[Keys.OFFSET_ASR] = prefs.offsets.asr
            it[Keys.OFFSET_MAGHRIB] = prefs.offsets.maghrib
            it[Keys.OFFSET_ISHA] = prefs.offsets.isha
            it[Keys.HIJRI_ADJUST] = prefs.hijriAdjustmentDays
            it[Keys.NOTIFY_FAJR] = prefs.notifyFajr
            it[Keys.NOTIFY_DHUHR] = prefs.notifyDhuhr
            it[Keys.NOTIFY_ASR] = prefs.notifyAsr
            it[Keys.NOTIFY_MAGHRIB] = prefs.notifyMaghrib
            it[Keys.NOTIFY_ISHA] = prefs.notifyIsha
            it[Keys.PRE_PRAYER_MIN] = prefs.prePrayerAlertMinutes
            it[Keys.REMIND_MORNING] = prefs.morningAdhkarReminderEnabled
            it[Keys.REMIND_EVENING] = prefs.eveningAdhkarReminderEnabled
            it[Keys.REMIND_QURAN] = prefs.quranReminderEnabled
            it[Keys.REMIND_TASBIH] = prefs.tasbihReminderEnabled
            it[Keys.TASBIH_VIBRATION] = prefs.tasbihVibrationEnabled
            it[Keys.TASBIH_SOUND] = prefs.tasbihSoundEnabled
            it[Keys.LAST_READ_SURAH] = prefs.lastReadSurah
            it[Keys.LAST_READ_AYAH] = prefs.lastReadAyah
            it[Keys.QURAN_FONT_SIZE] = prefs.quranFontSizeSp
            it[Keys.QURAN_READING_THEME] = prefs.quranReadingTheme.key
            it[Keys.QURAN_RECITER] = prefs.quranReciter.id
        }
    }

    suspend fun clearAll() {
        context.noorDataStore.edit { it.clear() }
    }
}
