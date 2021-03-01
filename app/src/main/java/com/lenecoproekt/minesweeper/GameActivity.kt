package com.lenecoproekt.minesweeper

import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lenecoproekt.minesweeper.logic.GameObject
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*

class GameActivity : AppCompatActivity() {
    var height = 0
    var width = 0
    private lateinit var gameObjects: Array<Array<GameObject?>>
    private var minesNumber = 0
    private var flagCounter = 0
    private var closedTiles = 0
    private var countMines = 0
    private lateinit var cells: Array<Array<TextView?>>
    private var isGameStopped = false
    private lateinit var flags: TextView
    private lateinit var score: TextView
    private var scoreN = 0
    private lateinit var mChronometer: Chronometer
    private lateinit var smile: ImageView
    private var win = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        height = intent.extras!!.getInt("HEIGHT")
        width = intent.extras!!.getInt("WIDTH")
        minesNumber = height * width * intent.extras!!.getInt("MINES") / 100
        closedTiles = height * width
        gameObjects = Array(height) { arrayOfNulls(width) }
        val gameField = findViewById<TableLayout>(R.id.gameField)
        gameField.isStretchAllColumns = true
        gameField.isShrinkAllColumns = true
        val mines = findViewById<TextView>(R.id.minesNumber)
        val minesIco = findViewById<TextView>(R.id.mineIco)
        minesIco.text = "\uD83D\uDCA9"
        flags = findViewById(R.id.flagsNumber)
        val flagsIco = findViewById<TextView>(R.id.flagIco)
        flagsIco.text = "\uD83D\uDC31"
        score = findViewById(R.id.score)
        val scoreIco = findViewById<TextView>(R.id.scoreIco)
        scoreIco.text = "\uD83D\uDC53"
        score.text = 0.toString()
        val timeIco = findViewById<TextView>(R.id.timeIco)
        timeIco.text = "\uD83D\uDD57"
        val field = findViewById<TextView>(R.id.textView)
        field.text = "$height X $width"
        cells = Array(height) { arrayOfNulls(width) }
        smile = findViewById(R.id.smile)
        loadMines()
        loadNumbers()
        mines.text = countMines.toString()
        flagCounter = countMines
        flags.text = flagCounter.toString()
        val restart = findViewById<Button>(R.id.restart)
        restart.setOnClickListener { finish() }
        smile.setOnClickListener(View.OnClickListener { recreate() })
        mChronometer = findViewById(R.id.chronometer2)
        mChronometer.base = SystemClock.elapsedRealtime()
        mChronometer.start()
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val widthD = size.x
        val heightD = size.y
        for (i in 0 until height) {
            val tableRow = TableRow(this)
            tableRow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    1)
            for (j in 0 until width) {
                val cell = TextView(this)
                cell.maxHeight = (heightD / height * 0.8).toInt()
                cell.textSize = 24 / ((width + height).toFloat() / 20)
                cell.textAlignment = View.TEXT_ALIGNMENT_CENTER
                setBackgroundForCells(cell, R.drawable.blue50, R.drawable.blue40, R.drawable.blue30, R.drawable.blue20, R.drawable.blue15, R.drawable.blue10)
                cells[i][j] = cell
                tableRow.addView(cell, j)
                cell.setOnClickListener {
                    if (!isGameStopped) {
                        openTile(cell, i, j)
                    }
                }
                cell.setOnLongClickListener {
                    flagOn(cell, i, j)
                    true
                }
            }
            gameField.addView(tableRow, i)
        }
    }

    private fun flagOn(cell: TextView, i: Int, j: Int) {
        if (!isGameStopped) {
            if (!gameObjects[i][j]!!.isOpen) {
                if (!gameObjects[i][j]!!.isFlag) {
                    if (flagCounter > 0) {
                        setBackgroundForCells(cell, R.drawable.green50, R.drawable.green40, R.drawable.green30, R.drawable.green20, R.drawable.green15, R.drawable.green10)
                        cell.text = "\uD83D\uDC31"
                        gameObjects[i][j]!!.isFlag = true
                        flagCounter--
                        flags.text = flagCounter.toString()
                    }
                } else {
                    setBackgroundForCells(cell, R.drawable.blue50, R.drawable.blue40, R.drawable.blue30, R.drawable.blue20, R.drawable.blue15, R.drawable.blue10)
                    cell.text = ""
                    flagCounter++
                    flags.text = flagCounter.toString()
                }
            }
        }
    }

    private fun openTile(cell: TextView?, finalI: Int, finalJ: Int) {
        if (gameObjects[finalI][finalJ]!!.isMine) {
            setBackgroundForCells(cell, R.drawable.red50, R.drawable.bomb40, R.drawable.red30, R.drawable.bomb20, R.drawable.red15, R.drawable.bomb10)
            if (cell != null) {
                cell.text = "\uD83D\uDCA9"
            }
            gameOver()
        } else {
            closedTiles--
            scoreN += 5
            score.text = scoreN.toString()
            if (gameObjects[finalI][finalJ]!!.isFlag) {
                gameObjects[finalI][finalJ]!!.isFlag = false
                flagCounter++
                flags.text = flagCounter.toString()
            }
            if (gameObjects[finalI][finalJ]!!.countMineNeighbors == 0) {
                setBackgroundForCells(cell, R.drawable.open50, R.drawable.open40, R.drawable.open30, R.drawable.open20, R.drawable.open15, R.drawable.open10)
                cell!!.text = "\uD83D\uDC3E"
                gameObjects[finalI][finalJ]!!.isOpen = true
                val neighbors = getNeighbors(gameObjects[finalI][finalJ])
                for (i in neighbors.indices) {
                    if (!neighbors[i]!!.isOpen) {
                        gameObjects[finalI][finalJ]!!.isOpen = true
                        val indexI = neighbors[i]!!.x
                        val indexJ = neighbors[i]!!.y
                        openTile(cells[indexI][indexJ], indexI, indexJ)
                    }
                }
            } else {
                gameObjects[finalI][finalJ]!!.isOpen = true
                setBackgroundForCells(cell, R.drawable.open50, R.drawable.open40, R.drawable.open30, R.drawable.open20, R.drawable.open15, R.drawable.open10)
                cell!!.text = gameObjects[finalI][finalJ]!!.countMineNeighbors.toString()
                setNumberColor(cell, gameObjects[finalI][finalJ]!!.countMineNeighbors)
            }
            if (closedTiles == countMines) win()
        }
    }

    private fun win() {
        for (i in 0 until height) {
            for (j in 0 until width) {
                setBackgroundForCells(cells[i][j], R.drawable.open50, R.drawable.open40, R.drawable.open30, R.drawable.open20, R.drawable.open15, R.drawable.open10)
                if (gameObjects[i][j]!!.isMine) cells[i][j]!!.text = "\uD83D\uDC31"
            }
        }
        isGameStopped = true
        mChronometer.stop()
        smile.setImageResource(R.drawable.smiling_cat)
        win = true
        val saveRecord = findViewById<Button>(R.id.saveRecord)
        saveRecord.visibility = View.VISIBLE
        saveRecord.setOnClickListener {
            try {
                // отрываем поток для записи
                val bw = BufferedWriter(OutputStreamWriter(
                        openFileOutput("records.txt", MODE_APPEND)))
                // пишем данные
                bw.write("Содержимое файла")
                // закрываем поток
                bw.close()
                Toast.makeText(this@GameActivity, "файл сохранен", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@GameActivity, "ERROR", Toast.LENGTH_SHORT).show()
            }
        }
        Toast.makeText(this, R.string.success, Toast.LENGTH_LONG).show()
    }

    private fun gameOver() {
        for (i in 0 until height) {
            for (j in 0 until width) {
                setBackgroundForCells(cells[i][j], R.drawable.open50, R.drawable.open40, R.drawable.open30, R.drawable.open20, R.drawable.open15, R.drawable.open10)
                if (gameObjects[i][j]!!.isMine) {
                    cells[i][j]!!.text = "\uD83D\uDCA9"
                    setBackgroundForCells(cells[i][j], R.drawable.red50, R.drawable.bomb40, R.drawable.red30, R.drawable.bomb20, R.drawable.red15, R.drawable.bomb10)
                } else {
                    if (gameObjects[i][j]!!.countMineNeighbors == 0) cells[i][j]!!.text = "\uD83D\uDC3E" else {
                        setNumberColor(cells[i][j], gameObjects[i][j]!!.countMineNeighbors)
                        cells[i][j]!!.text = gameObjects[i][j]!!.countMineNeighbors.toString()
                    }
                }
                gameObjects[i][j]!!.isOpen = true
            }
        }
        isGameStopped = true
        mChronometer!!.stop()
        smile.setImageResource(R.drawable.crying_cat_face)
        Toast.makeText(this, R.string.gameOver, Toast.LENGTH_LONG).show()
    }

    private fun loadNumbers() {
        for (i in 0 until height) {
            for (j in 0 until width) {
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

    private fun loadMines() {
        for (i in 0 until height) {
            for (j in 0 until width) {
                if (gameObjects[i][j] == null) {
                    var isMine: Boolean
                    if (countMines == minesNumber) {
                        isMine = false
                    } else {
                        isMine = (Math.random() * (width * height / minesNumber)) < 1
                        if (isMine) countMines++
                    }
                    gameObjects[i][j] = GameObject(i, j, isMine)
                } else {
                    if (countMines < minesNumber) {
                        if (!gameObjects[i][j]!!.isMine) {
                            gameObjects[i][j]!!.isMine = Math.random() * 10 < 1
                            if (gameObjects[i][j]!!.isMine) countMines++
                        }
                    } else return
                }
            }
        }
        if (countMines < minesNumber) loadMines()
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

    private fun setBackgroundForCells(cell: TextView?, size1: Int, size2: Int, size3: Int, size4: Int, size5: Int, size6: Int) {
        if (height <= 11) {
            cell!!.background = ContextCompat.getDrawable(this, size1)
        } else if (height < 15) {
            cell!!.background = ContextCompat.getDrawable(this, size2)
        } else if (height < 20) {
            cell!!.background = ContextCompat.getDrawable(this, size3)
        } else if (height < 30) {
            cell!!.background = ContextCompat.getDrawable(this, size4)
        } else if (height < 40) {
            cell!!.background = ContextCompat.getDrawable(this, size5)
        } else cell!!.background = ContextCompat.getDrawable(this, size6)
    }

    private fun setNumberColor(cell: TextView?, number: Int) {
        when (number) {
            2 -> cell!!.setTextColor(Color.GREEN)
            3 -> cell!!.setTextColor(Color.RED)
            4 -> cell!!.setTextColor(Color.BLACK)
            else -> cell!!.setTextColor(Color.BLUE)
        }
    }
}