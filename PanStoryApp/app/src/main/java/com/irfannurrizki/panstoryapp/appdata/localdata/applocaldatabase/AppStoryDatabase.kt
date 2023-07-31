package com.irfannurrizki.panstoryapp.appdata.localdata.applocaldatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.ListStoryItem

@Database(
    entities = [ListStoryItem::class, AppRemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class AppStoryDatabase: RoomDatabase() {
    abstract fun appStoryDao(): AppStoryDao
    abstract fun appRemoteKeysDao(): AppRemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: AppStoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AppStoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppStoryDatabase::class.java, "story_database"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
