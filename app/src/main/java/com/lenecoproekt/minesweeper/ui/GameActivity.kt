package com.lenecoproekt.minesweeper.ui

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.lenecoproekt.minesweeper.*
import com.lenecoproekt.minesweeper.databinding.ActivityGameBinding
import com.lenecoproekt.minesweeper.logic.GameObject
import com.lenecoproekt.minesweeper.room.HighScore
import com.lenecoproekt.minesweeper.room.HighScoreDatabase
import com.lenecoproekt.minesweeper.viewmodel.GameViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

open class GameActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Main + Job()
    }

    private lateinit var dataJob: Job
    private lateinit var errorJob: Job
    private var height = 0
    private var width = 0
    private var mines = 0
    private val ui: ActivityGameBinding by lazy { ActivityGameBinding.inflate(layoutInflater) }
    private val viewModel: GameViewModel by lazy { ViewModelProvider(this).get(GameViewModel::class.java) }
    private lateinit var cells: Array<Array<TextView?>>
    private var isGameStopped = false
    private var scoreN = 0
    private var win = false


    override fun onStart() {
        super.onStart()
        dataJob = launch {
            viewModel.getViewState().consumeEach {
                renderData(it)
            }
        }

        errorJob = launch {
            viewModel.getErrorChannel().consumeEach {
                renderError(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
        startGame()
    }

    private fun startGame() {
        height = AppPreferences.height!!
        width = AppPreferences.width!!
        mines = (AppPreferences.height!!* AppPreferences.width!!*AppPreferences.mines!!)/100
        ui.mineIco.text = "\uD83D\uDCA9"
        ui.flagIco.text = "\uD83D\uDC31"
        ui.scoreIco.text = "\uD83D\uDC53"
        ui.score.text = 0.toString()
        ui.timeIco.text = "\uD83D\uDD57"
        ui.gameFieldSize.text = "$height X $width"
        ui.gameField.isStretchAllColumns = true
        ui.gameField.isShrinkAllColumns = true
        cells = Array(height) { arrayOfNulls(width) }
        ui.restart.setOnClickListener {
            startActivity(SettingsActivity.getStartIntent(this))
            finishActivity(1)
        }
        ui.smile.setOnClickListener {
            viewModel.reload()
            recreate()
        }
        ui.chronometer2.base = SystemClock.elapsedRealtime()
        ui.chronometer2.start()
        ui.minesNumber.text = mines.toString()
        ui.saveRecord.setOnClickListener {
            if (viewModel.isWin()) {
                val recordDialogFragment = RecordDialogFragment()
                recordDialogFragment.show(supportFragmentManager, "recordDialog")
            }
        }
        for (i in 0 until height) {
            val tableRow = TableRow(this)
            tableRow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
            for (j in 0 until width) {
                val cell = TextView(this)
                cell.textSize = 24 / ((width + height).toFloat() / 20)
                cell.textAlignment = View.TEXT_ALIGNMENT_CENTER
                cells[i][j] = cell
                tableRow.addView(cell, j)
                cell.setOnClickListener {
                    if (!isGameStopped) {
                        viewModel.openTile(i, j)
                    }
                }
                cell.setOnLongClickListener {
                    if (!isGameStopped)
                        viewModel.flagOn(i, j)
                    true
                }
            }
            ui.gameField.addView(tableRow, i)
        }
        ui.homeButton.setOnClickListener{
            startActivity(MainActivity.getStartIntent(this))
            finishActivity(1)
        }
    }




    private fun setNumberColor(cell: TextView?, number: Int) {
        when (number) {
            2 -> cell?.setTextColor(Color.GREEN)
            3 -> cell?.setTextColor(Color.RED)
            4 -> cell?.setTextColor(Color.MAGENTA)
            1 -> cell?.setTextColor(Color.BLUE)
            5-> cell?.setTextColor(Color.CYAN)
            6-> cell?.setTextColor(Color.YELLOW)
            else -> cell!!.setTextColor(Color.DKGRAY)
        }
    }


    private fun renderError(error: Throwable) {
        error.message?.let { showError(it) }

    }

    private fun renderData(data: Array<Array<GameObject?>>) {
        for (i in 0 until height) {
            for (j in 0 until width) {
                data[i][j]?.let {
                    if (it.isOpen) {
                        if (it.isMine) {
                            cells[i][j]?.run {
                                cells[i][j]?.setBackgroundResource(R.drawable.cellbomb)
                                text = "\uD83D\uDCA9"
                            }
                        } else cells[i][j]?.run {
                            cells[i][j]?.setBackgroundResource(R.drawable.cellopen)
                            if (it.countMineNeighbors > 0) {
                                text = it.countMineNeighbors.toString()
                                setNumberColor(cells[i][j], it.countMineNeighbors)
                            } else text = "\uD83D\uDC3E"
                        }
                    } else {
                        cells[i][j]?.text = ""
                        cells[i][j]?.setBackgroundResource(R.drawable.closedcell)
                    }
                    if (it.isFlag) {
                        cells[i][j]?.run {
                            cells[i][j]?.setBackgroundResource(R.drawable.cellflag)
                            text = "\uD83D\uDC31"
                        }
                    }
                }
            }
        }
        ui.flagsNumber.text = viewModel.getFlags().toString()
        ui.score.text = viewModel.getScore().toString()
        if (viewModel.isWin()) {
            Toast.makeText(this, "You win!", Toast.LENGTH_LONG).show()
            isGameStopped = true
            ui.chronometer2.stop()
            for (cell in cells){
                for (c in cell){
                    if (c?.text == ""){
                        c.setBackgroundResource(R.drawable.cellflag)
                        c.text = "\uD83D\uDC31"
                    }
                }
            }
            ui.smile.setImageResource(R.drawable.smiling_cat)

            win = true
        }
        if (viewModel.isDefeat()) {
            Toast.makeText(this, "You loose!", Toast.LENGTH_LONG).show()
            isGameStopped = true
            ui.chronometer2.stop()
            ui.smile.setImageResource(R.drawable.crying_cat_face)
        }
    }


    private fun showError(error: String) {
        Snackbar.make(ui.root, error, Snackbar.LENGTH_INDEFINITE).apply {
            setAction(R.string.ok_bth_title) { dismiss() }
            show()
        }
    }

    override fun onStop() {
        super.onStop()
        dataJob.cancel()
        errorJob.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

    fun onDialogResult(resultDialog: String) {
        launch {
            val database = HighScoreDatabase.getInstance(application)
            val highScoreDao = database.highScoreDao
            val score = viewModel.getScore() + mines*10 - ui.chronometer2.drawingTime/100
            var highScore = HighScore(resultDialog, "$height X $width", ui.minesNumber.text.toString().toInt(),
                    ui.chronometer2.text.toString(), score.toInt())
            highScoreDao.insertAll(highScore)
        }
    }
}