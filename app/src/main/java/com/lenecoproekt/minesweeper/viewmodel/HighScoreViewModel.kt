//package com.lenecoproekt.minesweeper.viewmodel
//
//import androidx.lifecycle.ViewModel
//import com.lenecoproekt.minesweeper.room.HighScore
//import com.lenecoproekt.minesweeper.room.HighScoreDatabase
//import kotlinx.coroutines.*
//import kotlinx.coroutines.channels.BroadcastChannel
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.channels.ReceiveChannel
//import kotlin.coroutines.CoroutineContext
//
//class HighScoreViewModel : ViewModel(), CoroutineScope {
//    override val coroutineContext: CoroutineContext by lazy {
//        Dispatchers.Default + Job()
//    }
//
//    init{
//        launch{
//            val database = HighScoreDatabase.getInstance()
//            val highScoreDao = database.highScoreDao
//        }
//    }
//
//    private val viewStateChannel = BroadcastChannel<List<HighScore>>(Channel.CONFLATED)
//    private val errorChannel = Channel<Throwable>()
//
//    fun getViewState(): ReceiveChannel<List<HighScore>> = viewStateChannel.openSubscription()
//    fun getErrorChannel(): ReceiveChannel<Throwable> = errorChannel
//
//    private fun setError(e: Throwable) {
//        launch {
//            errorChannel.send(e)
//        }
//    }
//
//    private fun setData(data: List<HighScore>) {
//        launch {
//            viewStateChannel.send(data)
//        }
//    }
//
//    override fun onCleared() {
//        viewStateChannel.close()
//        errorChannel.close()
//        coroutineContext.cancel()
//        super.onCleared()
//    }
//}