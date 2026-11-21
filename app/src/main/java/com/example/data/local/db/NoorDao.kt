package com.example.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoorDao {

    // --- Tasbih Sessions ---
    @Query("SELECT * FROM tasbih_sessions ORDER BY timestampMillis DESC LIMIT 60")
    fun observeTasbihSessions(): Flow<List<TasbihSessionEntity>>

    @Query("SELECT * FROM tasbih_sessions ORDER BY timestampMillis DESC")
    suspend fun getAllTasbihSessionsOnce(): List<TasbihSessionEntity>

    @Query("SELECT COALESCE(SUM(count), 0) FROM tasbih_sessions WHERE dateKey = :dateKey")
    fun observeTodayTasbihTotal(dateKey: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(count), 0) FROM tasbih_sessions")
    fun observeAllTimeTasbihTotal(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasbih_sessions")
    fun observeTasbihSessionCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasbihSession(session: TasbihSessionEntity)

    @Query("DELETE FROM tasbih_sessions")
    suspend fun clearTasbihSessions()

    // --- Adhkar Favorites & Progress ---
    @Query("SELECT dhikrId FROM adhkar_favorites")
    fun observeFavoriteDhikrIds(): Flow<List<String>>

    @Query("SELECT * FROM adhkar_favorites")
    suspend fun getAllFavoritesOnce(): List<AdhkarFavoriteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteDhikr(entity: AdhkarFavoriteEntity)

    @Query("DELETE FROM adhkar_favorites WHERE dhikrId = :dhikrId")
    suspend fun removeFavoriteDhikr(dhikrId: String)

    @Query("SELECT * FROM adhkar_progress WHERE dateKey = :dateKey")
    fun observeAdhkarProgressForDate(dateKey: String): Flow<List<AdhkarProgressEntity>>

    @Query("SELECT COUNT(*) FROM adhkar_progress WHERE isCompleted = 1")
    fun observeTotalCompletedAdhkar(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAdhkarProgress(progress: AdhkarProgressEntity)

    @Query("DELETE FROM adhkar_progress WHERE dateKey = :dateKey")
    suspend fun resetAdhkarForDate(dateKey: String)

    @Query("DELETE FROM adhkar_favorites")
    suspend fun clearAdhkarFavorites()

    @Query("DELETE FROM adhkar_progress")
    suspend fun clearAdhkarProgress()

    // --- Quran Bookmarks & Offline Cache ---
    @Query("SELECT * FROM quran_bookmarks ORDER BY createdAtMillis DESC")
    fun observeQuranBookmarks(): Flow<List<QuranBookmarkEntity>>

    @Query("SELECT * FROM quran_bookmarks ORDER BY createdAtMillis DESC")
    suspend fun getAllBookmarksOnce(): List<QuranBookmarkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuranBookmark(bookmark: QuranBookmarkEntity)

    @Query("DELETE FROM quran_bookmarks WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber")
    suspend fun deleteQuranBookmark(surahNumber: Int, ayahNumber: Int)

    @Query("DELETE FROM quran_bookmarks")
    suspend fun clearQuranBookmarks()

    @Query("SELECT * FROM quran_verses_cache WHERE surahNumber = :surahNumber ORDER BY numberInSurah ASC")
    suspend fun getCachedSurahVerses(surahNumber: Int): List<QuranVerseCacheEntity>

    @Query("SELECT * FROM quran_verses_cache WHERE textUthmani LIKE '%' || :query || '%' LIMIT 40")
    suspend fun searchCachedVerses(query: String): List<QuranVerseCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuranVerses(verses: List<QuranVerseCacheEntity>)

    // --- Daily Activity Logs (Stats & Streaks) ---
    @Query("SELECT * FROM daily_activity_log WHERE dateKey = :dateKey")
    fun observeDailyActivity(dateKey: String): Flow<DailyActivityEntity?>

    @Query("SELECT * FROM daily_activity_log WHERE dateKey = :dateKey")
    suspend fun getDailyActivityOnce(dateKey: String): DailyActivityEntity?

    @Query("SELECT * FROM daily_activity_log ORDER BY dateKey DESC LIMIT 35")
    fun observeRecentDailyActivities(): Flow<List<DailyActivityEntity>>

    @Query("SELECT * FROM daily_activity_log ORDER BY dateKey DESC")
    suspend fun getAllDailyActivitiesOnce(): List<DailyActivityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyActivity(activity: DailyActivityEntity)

    @Query("DELETE FROM daily_activity_log")
    suspend fun clearDailyActivities()
}
