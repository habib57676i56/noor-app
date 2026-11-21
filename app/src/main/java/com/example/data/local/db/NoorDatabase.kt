package com.example.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        TasbihSessionEntity::class,
        AdhkarFavoriteEntity::class,
        AdhkarProgressEntity::class,
        QuranBookmarkEntity::class,
        QuranVerseCacheEntity::class,
        DailyActivityEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NoorDatabase : RoomDatabase() {
    abstract fun noorDao(): NoorDao

    companion object {
        @Volatile
        private var INSTANCE: NoorDatabase? = null

        fun getInstance(context: Context): NoorDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NoorDatabase::class.java,
                    "noor_islamic_db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
