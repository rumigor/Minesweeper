package com.lenecoproekt.minesweeper.logic

import java.util.ArrayList

class Logic(val height: Int, val width: Int, val minesNumber: Int) {
    private val gameObjects: Array<Array<GameObject?>> = Array(height) { arrayOfNulls(width) }

    init {
        fillGameField()
        loadMines()
    }

    private fun fillGameField() {
        loadMines()
        loadNumbers()
    }

    private fun loadMines() {
        var countMines = 0
        for (i in 0 until height) {
            for (j in 0 until width) {
                if (gameObjects[i][j] == null) {
                    var isMine: Boolean
                    if (countMines == minesNumber) {

                        return
                    } else {
                        isMine = (Math.random() * (width * height / minesNumber)) < 1
                        if (isMine) {
                            countMines++
                            gameObjects[i][j] = GameObject(i, j, isMine)
                        }
                    }
                }
            }
        }
        if (countMines < minesNumber) loadMines()
    }

    private fun loadNumbers() {
        for (i in 0 until height) {
            for (j in 0 until width) {
                gameObjects[i][j]?.let{
                    if ()
                }
                if (!gameObjects[i][j]!!.isMine) {
                    var n = 0
                    val result = getNeighbors(gameObjects[i][j])
                    for (k in result.indices) {
                        if (result[k]!!.isMine) n++
                    }
                    gameObjects[i][j]!!.countMineNeighbors = n
                }
            }
        }
    }

    fun openTile() {

    }

    private fun getNeighbors(gameObject: GameObject?): List<GameObject?> {
        val result: MutableList<GameObject?> = ArrayList()
        for (y in gameObject!!.y - 1..gameObject.y + 1) {
            for (x in gameObject.x - 1..gameObject.x + 1) {
                if (y < 0 || y >= width) {
                    continue
                }
                if (x < 0 || x >= height) {
                    continue
                }
                if (gameObjects[x][y] === gameObject) {
                    continue
                }
                result.add(gameObjects[x][y])
            }
        }
        return result
    }


}