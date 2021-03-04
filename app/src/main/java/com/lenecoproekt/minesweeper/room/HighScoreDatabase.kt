package com.lenecoproekt.minesweeper.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(HighScore::class), version = 3)
abstract class HighScoreDatabase : RoomDatabase() {
    abstract val highScoreDao: HighScoreDao

    companion object {

        @Volatile
        private var INSTANCE: HighScoreDatabase? = null

        fun getInstance(context: Context): HighScoreDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            HighScoreDatabase::class.java,
                            "HighScores_database"
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