//package com.lenecoproekt.minesweeper.room
//
//import android.app.Application
//import androidx.room.Room
//
//class App : Application() {
//    private lateinit var db : HighScoreDatabase
//
//    companion object {
//        @Volatile
//        private var INSTANCE: App? = null
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        INSTANCE = this
//        db = Room.databaseBuilder(
//                applicationContext,
//                HighScoreDatabase::class.java,
//                "HighScores_database"
//        )
//                .build()
//    }
//    public fun getHighScoreDao(): HighScoreDao {
//        return db.highScoreDao
//    }
//
//}