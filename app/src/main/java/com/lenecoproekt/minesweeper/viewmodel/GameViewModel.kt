package com.lenecoproekt.minesweeper.viewmodel


import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.lenecoproekt.minesweeper.APP_PREF
import com.lenecoproekt.minesweeper.HEIGHT
import com.lenecoproekt.minesweeper.MINES
import com.lenecoproekt.minesweeper.WIDTH
import com.lenecoproekt.minesweeper.logic.GameObject
import com.lenecoproekt.minesweeper.logic.Logic
import com.lenecoproekt.minesweeper.logic.Result
import com.lenecoproekt.minesweeper.ui.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext


class GameViewModel() : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy{
        Dispatchers.Default + Job()
    }

    private val gameFieldChannel by lazy { runBlocking { logic.getGameField() }}

    private val logic: Logic

    init {
        val height = 10
        val width = 10
        val mines = 10
        logic = Logic(height, width, mines)
        launch {
            gameFieldChannel.consumeEach { result ->
                when (result) {
                    is Result.Success<*> -> {
                        setData(result.data as Array<Array<GameObject?>>)
                    }
                    is Result.Error -> setError(result.error)
                }
            }
        }
    }

    fun openTile(i: Int, j: Int) {
        logic.openTile(i, j)
    }

    fun flagOn(i: Int, j: Int){
        logic.flagOn(i, j)
    }

    private val viewStateChannel = BroadcastChannel<Array<Array<GameObject?>>>(Channel.CONFLATED)
    private val errorChannel = Channel<Throwable>()

    fun getViewState(): ReceiveChannel<Array<Array<GameObject?>>> = viewStateChannel.openSubscription()
    fun getErrorChannel(): ReceiveChannel<Throwable> = errorChannel

    private fun setError(e: Throwable){
        launch {
            errorChannel.send(e)
        }
    }

    private fun setData(data: Array<Array<GameObject?>>){
        launch {
            viewStateChannel.send(data)
        }
    }

    override fun onCleared() {
        viewStateChannel.close()
        errorChannel.close()
        coroutineContext.cancel()
        super.onCleared()
    }

}