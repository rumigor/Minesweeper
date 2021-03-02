package com.lenecoproekt.minesweeper.ui

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.lenecoproekt.minesweeper.*
import com.lenecoproekt.minesweeper.databinding.ActivityGameBinding
import com.lenecoproekt.minesweeper.logic.GameObject
import com.lenecoproekt.minesweeper.viewmodel.GameViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import java.util.*
import kotlin.coroutines.CoroutineContext

class GameActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Main + Job()
    }

    private lateinit var dataJob: Job
    private lateinit var errorJob: Job

    public var height = 0
    public var width = 0
    public var mines = 0
    private val ui : ActivityGameBinding by lazy{ActivityGameBinding.inflate(layoutInflater)}
    private val viewModel : GameViewModel by lazy{ViewModelProvider(this).get(GameViewModel::class.java)}
    private lateinit var cells: Array<Array<TextView?>>
    private var isGameStopped = false
    private var scoreN = 0
    private var win = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
        startGame()
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

    private fun startGame() {
        val sharedPref = getSharedPreferences(APP_PREF, MODE_PRIVATE)
        sharedPref?.let {
            height = it.getInt(HEIGHT, 10)
            width = it.getInt(WIDTH, 10)
        }
        ui.gameField.isStretchAllColumns = true
        ui.gameField.isShrinkAllColumns = true
        ui.gameFieldSize.text = "$height X $width"
        cells = Array(height) { arrayOfNulls(width) }
        ui.restart.setOnClickListener { finish() }
        ui.smile.setOnClickListener { recreate() }
        ui.chronometer2.base = SystemClock.elapsedRealtime()
        ui.chronometer2.start()
        for (i in 0 until height) {
            val tableRow = TableRow(this)
            tableRow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    1)
            for (j in 0 until width) {
                val cell = TextView(this)
                cell.textSize = 24 / ((width + height).toFloat() / 20)
                cell.textAlignment = View.TEXT_ALIGNMENT_CENTER
                cell.setBackgroundColor(R.drawable.blue50)
                cells[i][j] = cell
                tableRow.addView(cell, j)
                cell.setOnClickListener {
                    if (!isGameStopped) {
                        viewModel.openTile(i, j)
                    }
                }
                cell.setOnLongClickListener {
                    viewModel.flagOn(i, j)
                    true
                }
            }
            ui.gameField.addView(tableRow, i)
        }
    }



//    private fun setBackgroundForCells(cell: TextView?, size1: Int, size2: Int, size3: Int, size4: Int, size5: Int, size6: Int) {
//
//
//        if (height <= 11) {
//            cell!!.background = set.getDrawable(this, size1)
//        } else if (height < 15) {
//            cell!!.background = ContextCompat.getDrawable(this, size2)
//        } else if (height < 20) {
//            cell!!.background = ContextCompat.getDrawable(this, size3)
//        } else if (height < 30) {
//            cell!!.background = ContextCompat.getDrawable(this, size4)
//        } else if (height < 40) {
//            cell!!.background = ContextCompat.getDrawable(this, size5)
//        } else cell!!.background = ContextCompat.getDrawable(this, size6)
//    }

    private fun setNumberColor(cell: TextView?, number: Int) {
        when (number) {
            2 -> cell?.setTextColor(Color.GREEN)
            3 -> cell?.setTextColor(Color.RED)
            4 -> cell?.setTextColor(Color.MAGENTA)
            1 -> cell?.setTextColor(Color.DKGRAY)
            else -> cell!!.setTextColor(Color.BLUE)
        }
    }




    private fun renderError(error: Throwable) {
        error.message?.let { showError(it) }

    }

    private fun renderData(data: Array<Array<GameObject?>>) {
        for (i in 0 until height){
            for (j in 0 until width){
                data[i][j]?.let {
                    if (it.isOpen){
                        if (it.isMine){
                            cells[i][j]?.run{
                                setBackgroundColor(R.drawable.red50)
                                text = "\uD83D\uDCA9"
                            }
                        } else cells[i][j]?.run{
                            setBackgroundColor(R.drawable.open50)
                            if (it.countMineNeighbors > 0){
                                text = it.countMineNeighbors.toString()
                                setNumberColor(cells[i][j], it.countMineNeighbors)
                            } else text = "\uD83D\uDC3E"
                        }
                    }
                    if (it.isFlag){
                        cells[i][j]?.run{
                            setBackgroundColor(R.drawable.green50)
                            text = "\uD83D\uDC31"
                        }
                    }
                }
            }
        }
    }


    protected fun showError(error: String) {
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
}