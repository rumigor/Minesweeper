package com.lenecoproekt.minesweeper.viewmodel


import androidx.lifecycle.ViewModel
import com.lenecoproekt.minesweeper.logic.FieldParams
import com.lenecoproekt.minesweeper.logic.GameObject
import com.lenecoproekt.minesweeper.logic.Logic
import com.lenecoproekt.minesweeper.logic.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext


class GameViewModel : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Default + Job()
    }

    private val gameFieldChannel by lazy { runBlocking { logic.getGameField() } }

    private val logic: Logic = Logic()

    init {
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
        setData(logic.openTile(i, j))
    }

    fun flagOn(i: Int, j: Int) {
        setData(logic.flagOn(i, j))
    }

    private val viewStateChannel = BroadcastChannel<Array<Array<GameObject?>>>(Channel.CONFLATED)
    private val errorChannel = Channel<Throwable>()

    fun getViewState(): ReceiveChannel<Array<Array<GameObject?>>> = viewStateChannel.openSubscription()
    fun getErrorChannel(): ReceiveChannel<Throwable> = errorChannel

    private fun setError(e: Throwable) {
        launch {
            errorChannel.send(e)
        }
    }

    private fun setData(data: Array<Array<GameObject?>>) {
        launch {
            viewStateChannel.send(data)
        }
    }

    fun getScore(): Int = logic.score

    fun getFlags(): Int = logic.flagCounter

    fun isWin(): Boolean = logic.win
    fun isDefeat(): Boolean = logic.gameOver

    fun reload(){
        setData(logic.reload())
    }

    override fun onCleared() {
        viewStateChannel.close()
        errorChannel.close()
        coroutineContext.cancel()
        super.onCleared()
    }
}