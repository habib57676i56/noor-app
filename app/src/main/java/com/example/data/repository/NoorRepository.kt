package com.example.data.repository

import com.example.data.local.catalog.SurahCatalog
import com.example.data.local.db.AdhkarFavoriteEntity
import com.example.data.local.db.AdhkarProgressEntity
import com.example.data.local.db.DailyActivityEntity
import com.example.data.local.db.NoorDao
import com.example.data.local.db.QuranBookmarkEntity
import com.example.data.local.db.QuranVerseCacheEntity
import com.example.data.local.db.TasbihSessionEntity
import com.example.data.local.prefs.UserPreferences
import com.example.data.local.prefs.UserPreferencesRepository
import com.example.data.remote.QuranApiService
import com.example.domain.model.AppLanguage
import com.example.domain.model.AsrMadhhab
import com.example.domain.model.CalculationMethod
import com.example.domain.model.PrayerMinuteOffsets
import com.example.domain.model.PrayerName
import com.example.domain.model.QuranReadingTheme
import com.example.domain.model.QuranReciter
import com.example.domain.model.QuranVerse
import com.example.domain.model.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class SurahLoadResult {
    data class Success(val verses: List<QuranVerse>, val isOfflineCached: Boolean) : SurahLoadResult()
    data class Error(val messageAr: String, val messageEn: String) : SurahLoadResult()
}

class NoorRepository(
    val dao: NoorDao,
    val prefs: UserPreferencesRepository,
    private val quranApi: QuranApiService
) {

    fun todayDateKey(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    // Reactive Streams
    val userPreferences: Flow<UserPreferences> = prefs.preferencesFlow
    val tasbihSessions: Flow<List<TasbihSessionEntity>> = dao.observeTasbihSessions()
    val allTimeTasbihTotal: Flow<Int> = dao.observeAllTimeTasbihTotal()
    val tasbihSessionCount: Flow<Int> = dao.observeTasbihSessionCount()
    val favoriteDhikrIds: Flow<List<String>> = dao.observeFavoriteDhikrIds()
    val totalCompletedAdhkar: Flow<Int> = dao.observeTotalCompletedAdhkar()
    val quranBookmarks: Flow<List<QuranBookmarkEntity>> = dao.observeQuranBookmarks()
    val recentDailyActivities: Flow<List<DailyActivityEntity>> = dao.observeRecentDailyActivities()

    fun observeTodayTasbihTotal(dateKey: String = todayDateKey()): Flow<Int> =
        dao.observeTodayTasbihTotal(dateKey)

    fun observeAdhkarProgress(dateKey: String = todayDateKey()): Flow<List<AdhkarProgressEntity>> =
        dao.observeAdhkarProgressForDate(dateKey)

    fun observeDailyActivity(dateKey: String = todayDateKey()): Flow<DailyActivityEntity?> =
        dao.observeDailyActivity(dateKey)

    // --- Prayer Completion Tracking ---
    suspend fun togglePrayerLogged(prayer: PrayerName, dateKey: String = todayDateKey()) {
        if (!prayer.isFard) return
        val current = dao.getDailyActivityOnce(dateKey) ?: DailyActivityEntity(dateKey = dateKey)
        val updated = when (prayer) {
            PrayerName.FAJR -> current.copy(fajrPrayed = !current.fajrPrayed)
            PrayerName.DHUHR -> current.copy(dhuhrPrayed = !current.dhuhrPrayed)
            PrayerName.ASR -> current.copy(asrPrayed = !current.asrPrayed)
            PrayerName.MAGHRIB -> current.copy(maghribPrayed = !current.maghribPrayed)
            PrayerName.ISHA -> current.copy(ishaPrayed = !current.ishaPrayed)
            PrayerName.SUNRISE -> current
        }
        dao.upsertDailyActivity(updated)
    }

    // --- Tasbih Persistence ---
    suspend fun recordTasbihTap(
        presetId: String,
        newCount: Int,
        target: Int,
        customText: String? = null
    ) {
        prefs.saveActiveTasbih(presetId, newCount, target, customText)
        val dateKey = todayDateKey()
        val current = dao.getDailyActivityOnce(dateKey) ?: DailyActivityEntity(dateKey = dateKey)
        dao.upsertDailyActivity(current.copy(tasbihCount = current.tasbihCount + 1))
    }

    suspend fun saveCompletedTasbihSession(
        presetId: String,
        labelAr: String,
        labelEn: String,
        count: Int,
        target: Int
    ) {
        if (count <= 0) return
        dao.insertTasbihSession(
            TasbihSessionEntity(
                presetId = presetId,
                labelAr = labelAr,
                labelEn = labelEn,
                count = count,
                target = target,
                dateKey = todayDateKey(),
                timestampMillis = System.currentTimeMillis()
            )
        )
    }

    // --- Adhkar Progress & Favorites ---
    suspend fun toggleFavoriteDhikr(dhikrId: String, currentlyFavorite: Boolean) {
        if (currentlyFavorite) {
            dao.removeFavoriteDhikr(dhikrId)
        } else {
            dao.addFavoriteDhikr(AdhkarFavoriteEntity(dhikrId = dhikrId))
        }
    }

    suspend fun incrementDhikrProgress(
        dhikrId: String,
        currentCount: Int,
        targetCount: Int,
        dateKey: String = todayDateKey()
    ) {
        val nextCount = (currentCount + 1).coerceAtMost(targetCount)
        val newlyCompleted = currentCount < targetCount && nextCount >= targetCount
        dao.upsertAdhkarProgress(
            AdhkarProgressEntity(
                dhikrId = dhikrId,
                dateKey = dateKey,
                currentCount = nextCount,
                targetCount = targetCount,
                isCompleted = nextCount >= targetCount
            )
        )
        if (newlyCompleted) {
            val act = dao.getDailyActivityOnce(dateKey) ?: DailyActivityEntity(dateKey = dateKey)
            dao.upsertDailyActivity(act.copy(adhkarCompleted = act.adhkarCompleted + 1))
        }
    }

    suspend fun resetDhikrProgressForToday(dateKey: String = todayDateKey()) {
        dao.resetAdhkarForDate(dateKey)
    }

    // --- Quran Loading (Offline-first: Bundled -> Room Cache -> AlQuran Cloud API + Cache) ---
    suspend fun loadSurahVerses(
        surahNumber: Int,
        reciter: QuranReciter
    ): SurahLoadResult = withContext(Dispatchers.IO) {
        // 1. Check Room cache first
        val cached = dao.getCachedSurahVerses(surahNumber)
        if (cached.isNotEmpty()) {
            return@withContext SurahLoadResult.Success(
                verses = cached.map { c ->
                    QuranVerse(
                        surahNumber = c.surahNumber,
                        numberInSurah = c.numberInSurah,
                        globalNumber = c.globalNumber,
                        textUthmani = c.textUthmani,
                        tafsirAr = c.tafsirAr,
                        audioUrl = SurahCatalog.audioUrlForGlobalAyah(c.globalNumber, reciter)
                    )
                },
                isOfflineCached = true
            )
        }

        // 2. Check verified bundled Surahs (instant offline)
        val bundled = SurahCatalog.getBundledSurahVerses(surahNumber, reciter)
        if (bundled != null) {
            dao.insertQuranVerses(
                bundled.map { v ->
                    QuranVerseCacheEntity(
                        surahNumber = v.surahNumber,
                        numberInSurah = v.numberInSurah,
                        globalNumber = v.globalNumber,
                        textUthmani = v.textUthmani,
                        tafsirAr = v.tafsirAr
                    )
                }
            )
            return@withContext SurahLoadResult.Success(bundled, isOfflineCached = true)
        }

        // 3. Fetch authoritative Uthmani + Tafsir Al-Muyassar from AlQuran Cloud API and cache in Room
        try {
            val response = quranApi.fetchSurahWithTafsir(surahNumber)
            val editions = response.data
            if (editions != null && editions.size >= 2) {
                val uthmaniAyahs = editions[0].ayahs
                val tafsirAyahs = editions[1].ayahs
                val tafsirMap = tafsirAyahs.associateBy { it.numberInSurah }

                val verses = uthmaniAyahs.map { ayah ->
                    QuranVerse(
                        surahNumber = surahNumber,
                        numberInSurah = ayah.numberInSurah,
                        globalNumber = ayah.number,
                        textUthmani = ayah.text,
                        tafsirAr = tafsirMap[ayah.numberInSurah]?.text ?: "",
                        audioUrl = SurahCatalog.audioUrlForGlobalAyah(ayah.number, reciter)
                    )
                }
                dao.insertQuranVerses(
                    verses.map { v ->
                        QuranVerseCacheEntity(
                            surahNumber = v.surahNumber,
                            numberInSurah = v.numberInSurah,
                            globalNumber = v.globalNumber,
                            textUthmani = v.textUthmani,
                            tafsirAr = v.tafsirAr
                        )
                    }
                )
                SurahLoadResult.Success(verses, isOfflineCached = false)
            } else {
                SurahLoadResult.Error(
                    messageAr = "تعذّر تحميل نص السورة من المصدر الموثوق. يرجى التحقق من الاتصال بالإنترنت.",
                    messageEn = "Unable to load Surah text from the verified source. Please check your internet connection."
                )
            }
        } catch (_: Exception) {
            SurahLoadResult.Error(
                messageAr = "لا يتوفر اتصال بالإنترنت لتحميل هذه السورة لأول مرة. السور المحفوظة متاحة دائماً دون إنترنت.",
                messageEn = "No internet connection to download this Surah for the first time. Previously opened & bundled Surahs work fully offline."
            )
        }
    }

    suspend fun searchVersesOffline(query: String): List<QuranVerseCacheEntity> =
        withContext(Dispatchers.IO) {
            if (query.trim().length < 2) emptyList() else dao.searchCachedVerses(query.trim())
        }

    suspend fun toggleBookmark(
        surahNumber: Int,
        ayahNumber: Int,
        surahNameAr: String,
        surahNameEn: String,
        ayahText: String,
        currentlyBookmarked: Boolean
    ) {
        if (currentlyBookmarked) {
            dao.deleteQuranBookmark(surahNumber, ayahNumber)
        } else {
            dao.insertQuranBookmark(
                QuranBookmarkEntity(
                    surahNumber = surahNumber,
                    ayahNumber = ayahNumber,
                    surahNameAr = surahNameAr,
                    surahNameEn = surahNameEn,
                    ayahPreview = ayahText.take(90)
                )
            )
        }
    }

    suspend fun recordQuranReadingProgress(surahNumber: Int, ayahNumber: Int, versesDelta: Int = 1) {
        prefs.saveLastRead(surahNumber, ayahNumber)
        if (versesDelta > 0) {
            val dateKey = todayDateKey()
            val act = dao.getDailyActivityOnce(dateKey) ?: DailyActivityEntity(dateKey = dateKey)
            dao.upsertDailyActivity(act.copy(quranVersesRead = act.quranVersesRead + versesDelta))
        }
    }

    // --- Local Backup & Restore (JSON via SAF - zero cloud leak, zero storage permission) ---
    suspend fun exportBackupJson(): String = withContext(Dispatchers.IO) {
        val p = prefs.getPreferencesOnce()
        val root = JSONObject()
        root.put("app", "NOOR")
        root.put("schemaVersion", 1)
        root.put("exportedAt", System.currentTimeMillis())

        val prefsJson = JSONObject().apply {
            put("language", p.language.code)
            put("themeMode", p.themeMode.key)
            put("cityId", p.cityId)
            put("cityNameAr", p.cityNameAr)
            put("cityNameEn", p.cityNameEn)
            put("countryAr", p.countryAr)
            put("countryEn", p.countryEn)
            put("latitude", p.latitude)
            put("longitude", p.longitude)
            put("timezoneId", p.timezoneId)
            put("calculationMethod", p.calculationMethod.key)
            put("asrMadhhab", p.asrMadhhab.key)
            put("hijriAdjustmentDays", p.hijriAdjustmentDays)
            put("offsetFajr", p.offsets.fajr)
            put("offsetSunrise", p.offsets.sunrise)
            put("offsetDhuhr", p.offsets.dhuhr)
            put("offsetAsr", p.offsets.asr)
            put("offsetMaghrib", p.offsets.maghrib)
            put("offsetIsha", p.offsets.isha)
            put("lastReadSurah", p.lastReadSurah)
            put("lastReadAyah", p.lastReadAyah)
            put("quranFontSizeSp", p.quranFontSizeSp)
            put("quranReadingTheme", p.quranReadingTheme.key)
            put("quranReciter", p.quranReciter.id)
        }
        root.put("preferences", prefsJson)

        val bookmarksArr = JSONArray()
        dao.getAllBookmarksOnce().forEach { b ->
            bookmarksArr.put(JSONObject().apply {
                put("surahNumber", b.surahNumber)
                put("ayahNumber", b.ayahNumber)
                put("surahNameAr", b.surahNameAr)
                put("surahNameEn", b.surahNameEn)
                put("ayahPreview", b.ayahPreview)
                put("createdAtMillis", b.createdAtMillis)
            })
        }
        root.put("bookmarks", bookmarksArr)

        val favsArr = JSONArray()
        dao.getAllFavoritesOnce().forEach { favsArr.put(it.dhikrId) }
        root.put("favoriteAdhkar", favsArr)

        val sessionsArr = JSONArray()
        dao.getAllTasbihSessionsOnce().forEach { s ->
            sessionsArr.put(JSONObject().apply {
                put("presetId", s.presetId)
                put("labelAr", s.labelAr)
                put("labelEn", s.labelEn)
                put("count", s.count)
                put("target", s.target)
                put("dateKey", s.dateKey)
                put("timestampMillis", s.timestampMillis)
            })
        }
        root.put("tasbihSessions", sessionsArr)

        val logsArr = JSONArray()
        dao.getAllDailyActivitiesOnce().forEach { d ->
            logsArr.put(JSONObject().apply {
                put("dateKey", d.dateKey)
                put("fajrPrayed", d.fajrPrayed)
                put("dhuhrPrayed", d.dhuhrPrayed)
                put("asrPrayed", d.asrPrayed)
                put("maghribPrayed", d.maghribPrayed)
                put("ishaPrayed", d.ishaPrayed)
                put("tasbihCount", d.tasbihCount)
                put("adhkarCompleted", d.adhkarCompleted)
                put("quranVersesRead", d.quranVersesRead)
            })
        }
        root.put("dailyActivities", logsArr)
        root.toString(2)
    }

    suspend fun importBackupJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (root.optString("app") != "NOOR") return@withContext false

            root.optJSONObject("preferences")?.let { pj ->
                val current = prefs.getPreferencesOnce()
                val restored = current.copy(
                    language = AppLanguage.fromCode(pj.optString("language", current.language.code)),
                    themeMode = ThemeMode.fromKey(pj.optString("themeMode", current.themeMode.key)),
                    cityId = pj.optString("cityId", current.cityId),
                    cityNameAr = pj.optString("cityNameAr", current.cityNameAr),
                    cityNameEn = pj.optString("cityNameEn", current.cityNameEn),
                    countryAr = pj.optString("countryAr", current.countryAr),
                    countryEn = pj.optString("countryEn", current.countryEn),
                    latitude = pj.optDouble("latitude", current.latitude),
                    longitude = pj.optDouble("longitude", current.longitude),
                    timezoneId = pj.optString("timezoneId", current.timezoneId),
                    calculationMethod = CalculationMethod.fromKey(pj.optString("calculationMethod", current.calculationMethod.key)),
                    asrMadhhab = AsrMadhhab.fromKey(pj.optString("asrMadhhab", current.asrMadhhab.key)),
                    hijriAdjustmentDays = pj.optInt("hijriAdjustmentDays", current.hijriAdjustmentDays),
                    offsets = PrayerMinuteOffsets(
                        fajr = pj.optInt("offsetFajr", 0),
                        sunrise = pj.optInt("offsetSunrise", 0),
                        dhuhr = pj.optInt("offsetDhuhr", 0),
                        asr = pj.optInt("offsetAsr", 0),
                        maghrib = pj.optInt("offsetMaghrib", 0),
                        isha = pj.optInt("offsetIsha", 0)
                    ),
                    lastReadSurah = pj.optInt("lastReadSurah", 1),
                    lastReadAyah = pj.optInt("lastReadAyah", 1),
                    quranFontSizeSp = pj.optInt("quranFontSizeSp", 24),
                    quranReadingTheme = QuranReadingTheme.fromKey(pj.optString("quranReadingTheme", "auto")),
                    quranReciter = QuranReciter.fromId(pj.optString("quranReciter", "alafasy"))
                )
                prefs.restoreFromPreferences(restored)
            }

            root.optJSONArray("bookmarks")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    dao.insertQuranBookmark(
                        QuranBookmarkEntity(
                            surahNumber = o.getInt("surahNumber"),
                            ayahNumber = o.getInt("ayahNumber"),
                            surahNameAr = o.optString("surahNameAr"),
                            surahNameEn = o.optString("surahNameEn"),
                            ayahPreview = o.optString("ayahPreview"),
                            createdAtMillis = o.optLong("createdAtMillis", System.currentTimeMillis())
                        )
                    )
                }
            }

            root.optJSONArray("favoriteAdhkar")?.let { arr ->
                for (i in 0 until arr.length()) {
                    dao.addFavoriteDhikr(AdhkarFavoriteEntity(arr.getString(i)))
                }
            }

            root.optJSONArray("tasbihSessions")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    dao.insertTasbihSession(
                        TasbihSessionEntity(
                            presetId = o.optString("presetId"),
                            labelAr = o.optString("labelAr"),
                            labelEn = o.optString("labelEn"),
                            count = o.optInt("count"),
                            target = o.optInt("target"),
                            dateKey = o.optString("dateKey"),
                            timestampMillis = o.optLong("timestampMillis", System.currentTimeMillis())
                        )
                    )
                }
            }

            root.optJSONArray("dailyActivities")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    dao.upsertDailyActivity(
                        DailyActivityEntity(
                            dateKey = o.getString("dateKey"),
                            fajrPrayed = o.optBoolean("fajrPrayed"),
                            dhuhrPrayed = o.optBoolean("dhuhrPrayed"),
                            asrPrayed = o.optBoolean("asrPrayed"),
                            maghribPrayed = o.optBoolean("maghribPrayed"),
                            ishaPrayed = o.optBoolean("ishaPrayed"),
                            tasbihCount = o.optInt("tasbihCount"),
                            adhkarCompleted = o.optInt("adhkarCompleted"),
                            quranVersesRead = o.optInt("quranVersesRead")
                        )
                    )
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun resetAllAppData() = withContext(Dispatchers.IO) {
        dao.clearTasbihSessions()
        dao.clearAdhkarFavorites()
        dao.clearAdhkarProgress()
        dao.clearQuranBookmarks()
        dao.clearDailyActivities()
        prefs.clearAll()
    }
}
