package com.example.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasbih_sessions")
data class TasbihSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val presetId: String,
    val labelAr: String,
    val labelEn: String,
    val count: Int,
    val target: Int,
    val dateKey: String,
    val timestampMillis: Long
)

@Entity(tableName = "adhkar_favorites")
data class AdhkarFavoriteEntity(
    @PrimaryKey val dhikrId: String,
    val savedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "adhkar_progress", primaryKeys = ["dhikrId", "dateKey"])
data class AdhkarProgressEntity(
    val dhikrId: String,
    val dateKey: String,
    val currentCount: Int,
    val targetCount: Int,
    val isCompleted: Boolean
)

@Entity(tableName = "quran_bookmarks", primaryKeys = ["surahNumber", "ayahNumber"])
data class QuranBookmarkEntity(
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahNameAr: String,
    val surahNameEn: String,
    val ayahPreview: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "quran_verses_cache", primaryKeys = ["surahNumber", "numberInSurah"])
data class QuranVerseCacheEntity(
    val surahNumber: Int,
    val numberInSurah: Int,
    val globalNumber: Int,
    val textUthmani: String,
    val tafsirAr: String
)

@Entity(tableName = "daily_activity_log")
data class DailyActivityEntity(
    @PrimaryKey val dateKey: String,
    val fajrPrayed: Boolean = false,
    val dhuhrPrayed: Boolean = false,
    val asrPrayed: Boolean = false,
    val maghribPrayed: Boolean = false,
    val ishaPrayed: Boolean = false,
    val tasbihCount: Int = 0,
    val adhkarCompleted: Int = 0,
    val quranVersesRead: Int = 0
) {
    val prayersCompletedCount: Int
        get() = listOf(fajrPrayed, dhuhrPrayed, asrPrayed, maghribPrayed, ishaPrayed).count { it }
}
