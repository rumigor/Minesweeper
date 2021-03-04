//package com.lenecoproekt.minesweeper.room
//
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.launch
//import kotlin.coroutines.CoroutineContext
//
//class HighScoreSource (highScoreDao: HighScoreDao) : CoroutineScope {
//    override val coroutineContext: CoroutineContext by lazy {
//        Dispatchers.Default + Job()
//    }
//    private var records: List<HighScore>? = null
//    private var scoreDao = highScoreDao
//
//    public suspend fun getRecords(): List<HighScore>? {
//        if (records == null) {
//            loadRecords();
//        }
//        return records
//    }
//
//    private suspend fun loadRecords() {
//        records = scoreDao.getAll()
//    }
//}