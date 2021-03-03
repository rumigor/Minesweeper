package com.lenecoproekt.minesweeper.logic

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import java.util.*

class Logic {
    private val height = FieldParams.height
    private val width = FieldParams.width
    private val minesNumber = FieldParams.mines
    private val gameField: Array<Array<GameObject?>> = Array(height) { arrayOfNulls(width) }
    private var closedTiles = height * width
    var score = 0
    var gameOver = false
    var win = false
    var flagCounter = minesNumber

    init {
        fillGameField()
    }

    fun getGameField(): ReceiveChannel<Result> =
            Channel<Result>(Channel.CONFLATED).apply {
                try {
                    offer(Result.Success(gameField))
                } catch (e: Throwable) {
                    offer(Result.Error(e))
                }
            }

    private fun fillGameField() {
        loadMines()
        loadNumbers()
    }

    private fun loadMines() {
        var countMines = 0
        while (countMines != minesNumber) {
            val i = Random().nextInt(height)
            val j = Random().nextInt(width)
            if (gameField[i][j] == null) {
                gameField[i][j] = GameObject(i, j, true)
                countMines++
            }
        }
        for (i in 0 until height) {
            for (j in 0 until width) {
                if (gameField[i][j] == null) {
                    gameField[i][j] = GameObject(i, j, false)
                }
            }
        }
    }

    private fun loadNumbers() {
        for (i in 0 until height) {
            for (j in 0 until width) {
                gameField[i][j]?.let {
                    if (!it.isMine) {
                        var n = 0
                        val result = getNeighbors(it)
                        for (k in result.indices) {
                            if (result[k].isMine) n++
                        }
                        it.countMineNeighbors = n
                    }
                }
            }
        }
    }

    fun openTile(i: Int, j: Int): Array<Array<GameObject?>> {
        gameField[i][j]?.let {
            if (it.isMine) {
                gameOver = true
                gameLost()
                return gameField
            } else {
                closedTiles--
                score += 5
            }
            if (it.isFlag) {
                it.isFlag = false
                flagCounter++
            }
            if (it.countMineNeighbors == 0) {
                it.isOpen = true
                val neighbors = getNeighbors(it)
                for (n in neighbors.indices) {
                    if (!neighbors[n].isOpen) {
                        neighbors[n].isOpen = true
                        val indexI = neighbors[n].x
                        val indexJ = neighbors[n].y
                        openTile(indexI, indexJ)
                    }
                }
            } else {
                it.isOpen = true
            }
        }
        if (closedTiles == minesNumber) win = true
        getGameField()
        return gameField
    }

    private fun gameLost() {
        for (i in 0 until height) {
            for (j in 0 until width) {
                gameField[i][j]?.isOpen = true
            }
        }
    }

    private fun getNeighbors(gameObject: GameObject): List<GameObject> {
        val result: MutableList<GameObject> = ArrayList()
        for (y in gameObject.y - 1..gameObject.y + 1) {
            for (x in gameObject.x - 1..gameObject.x + 1) {
                if (y < 0 || y >= width) {
                    continue
                }
                if (x < 0 || x >= height) {
                    continue
                }
                if (gameField[x][y] === gameObject) {
                    continue
                }
                gameField[x][y]?.let {
                    result.add(it)
                }

            }
        }
        return result
    }

    fun flagOn(i: Int, j: Int): Array<Array<GameObject?>> {
        gameField[i][j]?.let {
            if (!it.isOpen) {
                if (!it.isFlag) {
                    if (flagCounter > 0) {
                        it.isFlag = true
                        flagCounter--
                    }
                } else {
                    it.isFlag = false
                    flagCounter++
                }
            }
        }
        getGameField()
        return gameField
    }
    fun reload(): Array<Array<GameObject?>> {
        for (i in 0 until height) {
            for (j in 0 until width) {
                gameField[i][j] = null
            }
        }
        closedTiles = height * width
        score = 0
        gameOver = false
        win = false
        flagCounter = minesNumber
        fillGameField()
        return gameField
    }
}