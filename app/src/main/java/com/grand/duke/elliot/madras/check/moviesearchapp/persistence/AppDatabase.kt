package com.grand.duke.elliot.madras.check.moviesearchapp.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

const val DATABASE_NAME = "com.grand.duke.elliot.madras.check.moviesearchapp.persistence" +
        ".database_name:debug:1.0.0"

@Database (entities = [RecentSearch::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    )
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}