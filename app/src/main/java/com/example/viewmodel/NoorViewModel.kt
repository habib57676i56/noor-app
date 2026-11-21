package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.NoorApplication
import com.example.audio.AudioPlaybackState
import com.example.audio.DhikrTtsHelper
import com.example.audio.QuranAudioPlayer
import com.example.data.local.catalog.AdhkarCatalog
import com.example.data.local.catalog.DailyAyahItem
import com.example.data.local.catalog.SurahCatalog
import com.example.data.local.db.AdhkarProgressEntity
import com.example.data.local.db.DailyActivityEntity
import com.example.data.local.db.QuranBookmarkEntity
import com.example.data.local.db.QuranVerseCacheEntity
import com.example.data.local.db.TasbihSessionEntity
import com.example.data.local.prefs.UserPreferences
import com.example.data.repository.NoorRepository
import com.example.data.repository.SurahLoadResult
import com.example.domain.calendar.HijriCalendarEngine
import com.example.domain.model.AchievementItem
import com.example.domain.model.AppLanguage
import com.example.domain.model.AsrMadhhab
import com.example.domain.model.CalculationMethod
import com.example.domain.model.CityLocation
import com.example.domain.model.DailyPrayerSchedule
import com.example.domain.model.DhikrItem
import com.example.domain.model.HijriDateModel
import com.example.domain.model.NextPrayerInfo
import com.example.domain.model.PrayerName
import com.example.domain.model.QuranReadingTheme
import com.example.domain.model.QuranReciter
import com.example.domain.model.QuranVerse
import com.example.domain.model.ThemeMode
import com.example.domain.prayer.PrayerTimeCalculator
import com.example.location.LocationHelper
import com.example.notifications.NoorAlarmScheduler
import com.example.notifications.NoorNotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

data class PrayerDashboardState(
    val todaySchedule: DailyPrayerSchedule,
    val weeklySchedules: List<DailyPrayerSchedule>,
    val monthlySchedules: List<DailyPrayerSchedule>,
    val nextPrayerInfo: NextPrayerInfo,
    val hijriToday: HijriDateModel,
    val gregorianCal: Calendar,
    val dailyDhikr: DhikrItem,
    val dailyAyah: DailyAyahItem
)

sealed class SurahReaderState {
    data object Loading : SurahReaderState()
    data class Ready(val surahNumber: Int, val verses: List<QuranVerse>, val isOfflineCached: Boolean) : SurahReaderState()
    data class Error(val surahNumber: Int, val messageAr: String, val messageEn: String) : SurahReaderState()
}

data class StatsSummary(
    val currentStreakDays: Int = 0,
    val allTimeTasbih: Int = 0,
    val totalTasbihSessions: Int = 0,
    val totalCompletedAdhkar: Int = 0,
    val totalQuranVersesRead: Int = 0,
    val totalPrayersLogged: Int = 0,
    val recentActivities: List<DailyActivityEntity> = emptyList(),
    val achievements: List<AchievementItem> = emptyList()
)

class NoorViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: NoorRepository = (application as NoorApplication).repository
    private val quranAudioPlayer = QuranAudioPlayer()
    private val dhikrTtsHelper = DhikrTtsHelper(application)

    val preferences: StateFlow<UserPreferences> = repo.userPreferences
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())

    private val _nowMillis = MutableStateFlow(System.currentTimeMillis())
    val nowMillis: StateFlow<Long> = _nowMillis.asStateFlow()

    val prayerDashboard: StateFlow<PrayerDashboardState> = combine(preferences, _nowMillis) { prefs, now ->
        val tz = TimeZone.getTimeZone(prefs.timezoneId)
        val todayCal = Calendar.getInstance(tz).apply { timeInMillis = now }
        val tomorrowCal = (todayCal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }

        val todaySchedule = PrayerTimeCalculator.calculateForCalendar(
            todayCal, prefs.latitude, prefs.longitude, prefs.calculationMethod, prefs.asrMadhhab, prefs.offsets
        )
        val tomorrowSchedule = PrayerTimeCalculator.calculateForCalendar(
            tomorrowCal, prefs.latitude, prefs.longitude, prefs.calculationMethod, prefs.asrMadhhab, prefs.offsets
        )
        val weekly = PrayerTimeCalculator.calculateRange(
            todayCal, 7, prefs.latitude, prefs.longitude, prefs.calculationMethod, prefs.asrMadhhab, prefs.offsets
        )
        val monthly = PrayerTimeCalculator.calculateRange(
            todayCal, 30, prefs.latitude, prefs.longitude, prefs.calculationMethod, prefs.asrMadhhab, prefs.offsets
        )
        val nextInfo = PrayerTimeCalculator.computeNextPrayer(now, todaySchedule, tomorrowSchedule)
        val hijri = HijriCalendarEngine.gregorianToHijri(
            todayCal.get(Calendar.YEAR),
            todayCal.get(Calendar.MONTH) + 1,
            todayCal.get(Calendar.DAY_OF_MONTH),
            prefs.hijriAdjustmentDays
        )
        val dayOfYear = todayCal.get(Calendar.DAY_OF_YEAR)

        PrayerDashboardState(
            todaySchedule = todaySchedule,
            weeklySchedules = weekly,
            monthlySchedules = monthly,
            nextPrayerInfo = nextInfo,
            hijriToday = hijri,
            gregorianCal = todayCal,
            dailyDhikr = AdhkarCatalog.dailyDhikr(dayOfYear),
            dailyAyah = AdhkarCatalog.dailyAyah(dayOfYear)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = buildInitialDashboard(UserPreferences())
    )

    val todayActivity: StateFlow<DailyActivityEntity?> = repo.observeDailyActivity()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val tasbihSessions: StateFlow<List<TasbihSessionEntity>> = repo.tasbihSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayTasbihTotal: StateFlow<Int> = repo.observeTodayTasbihTotal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val favoriteDhikrIds: StateFlow<Set<String>> = repo.favoriteDhikrIds
        .combine(MutableStateFlow(Unit)) { list, _ -> list.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val adhkarProgressMap: StateFlow<Map<String, AdhkarProgressEntity>> = repo.observeAdhkarProgress()
        .combine(MutableStateFlow(Unit)) { list, _ -> list.associateBy { it.dhikrId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val quranBookmarks: StateFlow<List<QuranBookmarkEntity>> = repo.quranBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val audioState: StateFlow<AudioPlaybackState> = quranAudioPlayer.state
    val speakingDhikrId: StateFlow<String?> = dhikrTtsHelper.speakingDhikrId

    private val _surahReaderState = MutableStateFlow<SurahReaderState>(SurahReaderState.Loading)
    val surahReaderState: StateFlow<SurahReaderState> = _surahReaderState.asStateFlow()

    private val _verseSearchResults = MutableStateFlow<List<QuranVerseCacheEntity>>(emptyList())
    val verseSearchResults: StateFlow<List<QuranVerseCacheEntity>> = _verseSearchResults.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _isDetectingLocation = MutableStateFlow(false)
    val isDetectingLocation: StateFlow<Boolean> = _isDetectingLocation.asStateFlow()

    private val _liveTasbihCount = MutableStateFlow(0)
    val liveTasbihCount: StateFlow<Int> = _liveTasbihCount.asStateFlow()
    private var tasbihHydrated = false

    val statsSummary: StateFlow<StatsSummary> = combine(
        repo.allTimeTasbihTotal,
        repo.tasbihSessionCount,
        repo.totalCompletedAdhkar,
        repo.recentDailyActivities
    ) { allTasbih, sessionCount, completedAdhkar, activities ->
        val totalVerses = activities.sumOf { it.quranVersesRead }
        val totalPrayers = activities.sumOf { it.prayersCompletedCount }
        val activeDays = activities.count {
            it.prayersCompletedCount > 0 || it.tasbihCount > 0 || it.adhkarCompleted > 0 || it.quranVersesRead > 0
        }
        val combinedTasbih = maxOf(allTasbih, activities.sumOf { it.tasbihCount })

        val achievements = listOf(
            AchievementItem(
                id = "streak_3",
                titleAr = "بداية الثبات",
                titleEn = "Steady Beginning",
                subtitleAr = "المحافظة على الورد اليومي لمدة ٣ أيام",
                subtitleEn = "Maintain your daily worship routine for 3 days",
                unlocked = activeDays >= 3,
                progress = (activeDays / 3f).coerceIn(0f, 1f)
            ),
            AchievementItem(
                id = "tasbih_300",
                titleAr = "ذاكر الله",
                titleEn = "Mindful Remembrance",
                subtitleAr = "إتمام ٣٠٠ تسبيحة في المسبحة الإلكترونية",
                subtitleEn = "Complete 300 Tasbih counts",
                unlocked = combinedTasbih >= 300,
                progress = (combinedTasbih / 300f).coerceIn(0f, 1f)
            ),
            AchievementItem(
                id = "adhkar_10",
                titleAr = "حصن المسلم",
                titleEn = "Fortress of Remembrance",
                subtitleAr = "إتمام ١٠ أذكار يومية كاملة",
                subtitleEn = "Complete 10 daily Adhkar items",
                unlocked = completedAdhkar >= 10,
                progress = (completedAdhkar / 10f).coerceIn(0f, 1f)
            ),
            AchievementItem(
                id = "prayers_5",
                titleAr = "محافظ على الصلوات",
                titleEn = "Guardian of Prayers",
                subtitleAr = "تسجيل إتمام ٥ صلوات مفروضة",
                subtitleEn = "Log completion of 5 obligatory prayers",
                unlocked = totalPrayers >= 5,
                progress = (totalPrayers / 5f).coerceIn(0f, 1f)
            ),
            AchievementItem(
                id = "quran_20",
                titleAr = "رفيق القرآن",
                titleEn = "Companion of the Quran",
                subtitleAr = "قراءة ٢٠ آية من المصحف الشريف",
                subtitleEn = "Read 20 verses from the Holy Quran",
                unlocked = totalVerses >= 20,
                progress = (totalVerses / 20f).coerceIn(0f, 1f)
            )
        )

        StatsSummary(
            currentStreakDays = activeDays,
            allTimeTasbih = combinedTasbih,
            totalTasbihSessions = sessionCount,
            totalCompletedAdhkar = completedAdhkar,
            totalQuranVersesRead = totalVerses,
            totalPrayersLogged = totalPrayers,
            recentActivities = activities,
            achievements = achievements
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsSummary())

    init {
        // Live 1-second clock tick for the Hero prayer countdown timer
        viewModelScope.launch {
            while (isActive) {
                _nowMillis.value = System.currentTimeMillis()
                delay(1000L)
            }
        }
        // Keep Android prayer/reminder alarms synced & hydrate live Tasbih counter
        viewModelScope.launch {
            preferences.collect { prefs ->
                if (!tasbihHydrated) {
                    _liveTasbihCount.value = prefs.activeTasbihCount
                    tasbihHydrated = true
                }
                NoorAlarmScheduler.rescheduleAll(getApplication(), prefs)
            }
        }
    }

    private fun buildInitialDashboard(prefs: UserPreferences): PrayerDashboardState {
        val now = System.currentTimeMillis()
        val tz = TimeZone.getTimeZone(prefs.timezoneId)
        val todayCal = Calendar.getInstance(tz).apply { timeInMillis = now }
        val tomorrowCal = (todayCal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }
        val today = PrayerTimeCalculator.calculateForCalendar(
            todayCal, prefs.latitude, prefs.longitude, prefs.calculationMethod, prefs.asrMadhhab, prefs.offsets
        )
        val tomorrow = PrayerTimeCalculator.calculateForCalendar(
            tomorrowCal, prefs.latitude, prefs.longitude, prefs.calculationMethod, prefs.asrMadhhab, prefs.offsets
        )
        val dayOfYear = todayCal.get(Calendar.DAY_OF_YEAR)
        return PrayerDashboardState(
            todaySchedule = today,
            weeklySchedules = listOf(today),
            monthlySchedules = listOf(today),
            nextPrayerInfo = PrayerTimeCalculator.computeNextPrayer(now, today, tomorrow),
            hijriToday = HijriCalendarEngine.gregorianToHijri(
                todayCal.get(Calendar.YEAR),
                todayCal.get(Calendar.MONTH) + 1,
                todayCal.get(Calendar.DAY_OF_MONTH),
                prefs.hijriAdjustmentDays
            ),
            gregorianCal = todayCal,
            dailyDhikr = AdhkarCatalog.dailyDhikr(dayOfYear),
            dailyAyah = AdhkarCatalog.dailyAyah(dayOfYear)
        )
    }

    fun clearStatusBanner() {
        _statusBanner.value = null
    }

    // --- Theme & Language ---
    fun setLanguage(lang: AppLanguage) = viewModelScope.launch {
        repo.prefs.setLanguage(lang)
    }

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch {
        repo.prefs.setThemeMode(mode)
    }

    // --- Location & Prayer Config ---
    fun selectCity(city: CityLocation) = viewModelScope.launch {
        repo.prefs.setCity(city, isAuto = false)
    }

    fun detectLocationAutomatically() = viewModelScope.launch {
        _isDetectingLocation.value = true
        val detected = LocationHelper.detectCurrentCity(getApplication())
        _isDetectingLocation.value = false
        val isAr = preferences.value.language == AppLanguage.ARABIC
        if (detected != null) {
            repo.prefs.setCity(detected, isAuto = true)
            _statusBanner.value = if (isAr) "تم تحديث الموقع: ${detected.nameAr}" else "Location updated: ${detected.nameEn}"
        } else {
            _statusBanner.value = if (isAr) {
                "تعذّر تحديد الموقع تلقائياً. يمكنك اختيار مدينتك يدوياً."
            } else {
                "Could not detect GPS location. You can choose your city manually."
            }
        }
    }

    fun setCalculationMethod(method: CalculationMethod) = viewModelScope.launch {
        repo.prefs.setCalculationMethod(method)
    }

    fun setAsrMadhhab(madhhab: AsrMadhhab) = viewModelScope.launch {
        repo.prefs.setAsrMadhhab(madhhab)
    }

    fun setPrayerOffset(prayer: PrayerName, offset: Int) = viewModelScope.launch {
        repo.prefs.setPrayerOffset(prayer, offset)
    }

    fun setHijriAdjustment(days: Int) = viewModelScope.launch {
        repo.prefs.setHijriAdjustment(days)
    }

    fun togglePrayerLogged(prayer: PrayerName) = viewModelScope.launch {
        repo.togglePrayerLogged(prayer)
    }

    // --- Notifications ---
    fun setPrayerNotification(prayer: PrayerName, enabled: Boolean) = viewModelScope.launch {
        repo.prefs.setPrayerNotification(prayer, enabled)
    }

    fun setPrePrayerAlertMinutes(minutes: Int) = viewModelScope.launch {
        repo.prefs.setPrePrayerAlertMinutes(minutes)
    }

    fun setReminder(type: String, enabled: Boolean, hour: Int? = null, minute: Int? = null) = viewModelScope.launch {
        repo.prefs.setReminder(type, enabled, hour, minute)
    }

    fun sendTestNotification(): Boolean {
        val isAr = preferences.value.language == AppLanguage.ARABIC
        val sent = NoorNotificationHelper.showNotification(
            context = getApplication(),
            notificationId = 9999,
            channelId = NoorNotificationHelper.CHANNEL_PRAYER,
            title = if (isAr) "تطبيق نور — تنبيه تجريبي" else "Noor — Test Notification",
            body = if (isAr) {
                "نظام تنبيهات الصلاة والأذكار يعمل بنجاح ودقة."
            } else {
                "Prayer & Adhkar notifications are active and working properly."
            }
        )
        _statusBanner.value = if (sent) {
            if (isAr) "تم إرسال إشعار تجريبي بنجاح" else "Test notification sent"
        } else {
            if (isAr) "يرجى منح إذن الإشعارات أولاً" else "Please grant notification permission first"
        }
        return sent
    }

    // --- Adhkar ---
    fun toggleFavoriteDhikr(dhikrId: String) = viewModelScope.launch {
        val isFav = favoriteDhikrIds.value.contains(dhikrId)
        repo.toggleFavoriteDhikr(dhikrId, isFav)
    }

    fun incrementDhikr(dhikr: DhikrItem) = viewModelScope.launch {
        val current = adhkarProgressMap.value[dhikr.id]?.currentCount ?: 0
        if (current < dhikr.targetCount) {
            triggerHaptic(light = true)
            repo.incrementDhikrProgress(dhikr.id, current, dhikr.targetCount)
        }
    }

    fun resetTodayAdhkar() = viewModelScope.launch {
        repo.resetDhikrProgressForToday()
    }

    fun toggleSpeakDhikr(dhikr: DhikrItem) {
        dhikrTtsHelper.speakOrStop(dhikr.id, dhikr.textAr)
    }

    fun stopSpeakingDhikr() {
        dhikrTtsHelper.stop()
    }

    // --- Electronic Tasbih ---
    fun incrementTasbih() {
        tasbihHydrated = true
        val prefs = preferences.value
        val nextCount = _liveTasbihCount.value + 1
        _liveTasbihCount.value = nextCount
        val target = prefs.activeTasbihTarget.coerceAtLeast(1)

        if (prefs.tasbihVibrationEnabled) {
            triggerHaptic(light = nextCount % target != 0)
        }
        if (prefs.tasbihSoundEnabled) {
            playClickTone(isMilestone = nextCount % target == 0)
        }

        viewModelScope.launch {
            repo.recordTasbihTap(prefs.activeTasbihPresetId, nextCount, target)

            if (nextCount == target) {
                val preset = AdhkarCatalog.tasbihPresets.firstOrNull { it.id == prefs.activeTasbihPresetId }
                repo.saveCompletedTasbihSession(
                    presetId = prefs.activeTasbihPresetId,
                    labelAr = preset?.textAr ?: prefs.customTasbihText,
                    labelEn = preset?.textEn ?: prefs.customTasbihText,
                    count = nextCount,
                    target = target
                )
            }
        }
    }

    fun selectTasbihPreset(presetId: String, target: Int, customText: String? = null) {
        tasbihHydrated = true
        val prefs = preferences.value
        val prevCount = _liveTasbihCount.value
        _liveTasbihCount.value = 0
        viewModelScope.launch {
            if (prevCount > 0 && prevCount != prefs.activeTasbihTarget) {
                val prevPreset = AdhkarCatalog.tasbihPresets.firstOrNull { it.id == prefs.activeTasbihPresetId }
                repo.saveCompletedTasbihSession(
                    presetId = prefs.activeTasbihPresetId,
                    labelAr = prevPreset?.textAr ?: prefs.customTasbihText,
                    labelEn = prevPreset?.textEn ?: prefs.customTasbihText,
                    count = prevCount,
                    target = prefs.activeTasbihTarget
                )
            }
            repo.prefs.saveActiveTasbih(presetId, 0, target.coerceAtLeast(1), customText)
        }
    }

    fun updateTasbihTarget(target: Int) = viewModelScope.launch {
        val prefs = preferences.value
        repo.prefs.saveActiveTasbih(prefs.activeTasbihPresetId, _liveTasbihCount.value, target.coerceIn(1, 10000))
    }

    fun resetTasbihCounter() {
        tasbihHydrated = true
        val prefs = preferences.value
        val prevCount = _liveTasbihCount.value
        _liveTasbihCount.value = 0
        viewModelScope.launch {
            if (prevCount > 0 && prevCount != prefs.activeTasbihTarget) {
                val preset = AdhkarCatalog.tasbihPresets.firstOrNull { it.id == prefs.activeTasbihPresetId }
                repo.saveCompletedTasbihSession(
                    presetId = prefs.activeTasbihPresetId,
                    labelAr = preset?.textAr ?: prefs.customTasbihText,
                    labelEn = preset?.textEn ?: prefs.customTasbihText,
                    count = prevCount,
                    target = prefs.activeTasbihTarget
                )
            }
            repo.prefs.saveActiveTasbih(prefs.activeTasbihPresetId, 0, prefs.activeTasbihTarget)
        }
    }

    fun setTasbihVibration(enabled: Boolean) = viewModelScope.launch {
        repo.prefs.setTasbihVibration(enabled)
    }

    fun setTasbihSound(enabled: Boolean) = viewModelScope.launch {
        repo.prefs.setTasbihSound(enabled)
    }

    // --- Holy Quran ---
    fun loadSurah(surahNumber: Int) = viewModelScope.launch {
        _surahReaderState.value = SurahReaderState.Loading
        val reciter = preferences.value.quranReciter
        when (val res = repo.loadSurahVerses(surahNumber, reciter)) {
            is SurahLoadResult.Success -> {
                _surahReaderState.value = SurahReaderState.Ready(surahNumber, res.verses, res.isOfflineCached)
                repo.recordQuranReadingProgress(surahNumber, 1, versesDelta = 1)
            }
            is SurahLoadResult.Error -> {
                _surahReaderState.value = SurahReaderState.Error(surahNumber, res.messageAr, res.messageEn)
            }
        }
    }

    fun searchQuranVerses(query: String) = viewModelScope.launch {
        _verseSearchResults.value = repo.searchVersesOffline(query)
    }

    fun saveLastRead(surahNumber: Int, ayahNumber: Int) = viewModelScope.launch {
        repo.recordQuranReadingProgress(surahNumber, ayahNumber, versesDelta = 1)
    }

    fun toggleBookmark(surahNumber: Int, ayahNumber: Int, ayahText: String) = viewModelScope.launch {
        val surah = SurahCatalog.findSurah(surahNumber)
        val exists = quranBookmarks.value.any { it.surahNumber == surahNumber && it.ayahNumber == ayahNumber }
        repo.toggleBookmark(
            surahNumber = surahNumber,
            ayahNumber = ayahNumber,
            surahNameAr = surah.nameAr,
            surahNameEn = surah.nameEn,
            ayahText = ayahText,
            currentlyBookmarked = exists
        )
    }

    fun setQuranFontSize(sizeSp: Int) = viewModelScope.launch {
        repo.prefs.setQuranFontSize(sizeSp)
    }

    fun setQuranReadingTheme(theme: QuranReadingTheme) = viewModelScope.launch {
        repo.prefs.setQuranReadingTheme(theme)
    }

    fun setQuranReciter(reciter: QuranReciter) = viewModelScope.launch {
        repo.prefs.setQuranReciter(reciter)
        val currentState = _surahReaderState.value
        if (currentState is SurahReaderState.Ready) {
            val updatedVerses = currentState.verses.map { v ->
                v.copy(audioUrl = SurahCatalog.audioUrlForGlobalAyah(v.globalNumber, reciter))
            }
            _surahReaderState.value = currentState.copy(verses = updatedVerses)
        }
    }

    fun playQuranVerse(verse: QuranVerse, allVerses: List<QuranVerse>) {
        quranAudioPlayer.playAyah(
            surahNumber = verse.surahNumber,
            ayahNumber = verse.numberInSurah,
            audioUrl = verse.audioUrl,
            onCompleted = {
                val nextIndex = allVerses.indexOfFirst { it.numberInSurah == verse.numberInSurah } + 1
                if (nextIndex in allVerses.indices) {
                    playQuranVerse(allVerses[nextIndex], allVerses)
                }
            }
        )
    }

    fun toggleAudioPauseResume() = quranAudioPlayer.togglePauseResume()
    fun stopQuranAudio() = quranAudioPlayer.stop()

    // --- Backup, Restore & Reset ---
    suspend fun buildBackupJson(): String = repo.exportBackupJson()

    fun restoreBackupJson(json: String) = viewModelScope.launch {
        val ok = repo.importBackupJson(json)
        val isAr = preferences.value.language == AppLanguage.ARABIC
        _statusBanner.value = if (ok) {
            if (isAr) "تمت استعادة النسخة الاحتياطية بنجاح" else "Backup restored successfully"
        } else {
            if (isAr) "ملف النسخة الاحتياطية غير صالح" else "Invalid backup file format"
        }
    }

    fun resetApplication() = viewModelScope.launch {
        _liveTasbihCount.value = 0
        quranAudioPlayer.stop()
        dhikrTtsHelper.stop()
        repo.resetAllAppData()
        val isAr = preferences.value.language == AppLanguage.ARABIC
        _statusBanner.value = if (isAr) "تمت إعادة ضبط التطبيق بالكامل" else "Application reset completed"
    }

    private fun triggerHaptic(light: Boolean) {
        try {
            val ctx = getApplication<Application>()
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            } ?: return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val duration = if (light) 22L else 70L
                val amplitude = if (light) 90 else 200
                vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(if (light) 22L else 70L)
            }
        } catch (_: Exception) {
        }
    }

    private fun playClickTone(isMilestone: Boolean) {
        try {
            val tone = ToneGenerator(AudioManager.STREAM_SYSTEM, 40)
            tone.startTone(
                if (isMilestone) ToneGenerator.TONE_PROP_ACK else ToneGenerator.TONE_PROP_BEEP,
                if (isMilestone) 95 else 30
            )
        } catch (_: Exception) {
        }
    }

    override fun onCleared() {
        super.onCleared()
        quranAudioPlayer.stop()
        dhikrTtsHelper.shutdown()
    }
}
