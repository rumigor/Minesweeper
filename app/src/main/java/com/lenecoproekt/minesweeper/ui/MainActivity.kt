package com.lenecoproekt.minesweeper.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.lenecoproekt.minesweeper.*
import com.lenecoproekt.minesweeper.databinding.ActivityMainBinding
import com.lenecoproekt.minesweeper.logic.FieldParams

class MainActivity : AppCompatActivity(), OnSeekBarChangeListener {
    private val ui : ActivityMainBinding by lazy {ActivityMainBinding.inflate(layoutInflater)}
    private lateinit var sharedPreferences : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
        ui.heightBar.progress = 10
        ui.heightBar.max = 50
        ui.heigthNumber.text = ui.heightBar.progress.toString()
        ui.widthBar.progress = 10
        ui.widthBar.max = 50
        ui.widthNumber.text = ui.widthBar.progress.toString()
        ui.minesBar.progress = 10
        ui.minesBar.max = 30
        ui.minesProcentNumber.text = ui.minesBar.progress.toString()
        ui.heightBar.setOnSeekBarChangeListener(this)
        ui.widthBar.setOnSeekBarChangeListener(this)
        ui.minesBar.setOnSeekBarChangeListener(this)
        sharedPreferences = getSharedPreferences(APP_PREF, MODE_PRIVATE)
        sharedPreferences.edit().putInt(HEIGHT, ui.heightBar.progress).apply()
        sharedPreferences.edit().putInt(WIDTH, ui.widthBar.progress).apply()
        sharedPreferences.edit().putInt(MINES, ui.minesBar.progress).apply()
        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            FieldParams.height = ui.heightBar.progress
            FieldParams.width = ui.widthBar.progress
            FieldParams.mines = (FieldParams.height*FieldParams.width*ui.minesBar.progress)/100
            val intent = Intent(this@MainActivity, GameActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        when (seekBar) {
            ui.heightBar -> {
                ui.heigthNumber.text = seekBar.progress.toString()
            }
            ui.widthBar -> {
                ui.widthNumber.text = seekBar.progress.toString()
            }
            ui.minesBar -> {
                ui.minesProcentNumber.text = seekBar.progress.toString()
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {
        when (seekBar) {
            ui.heightBar -> {
                ui.heigthNumber.text = seekBar.progress.toString()
                sharedPreferences.edit().putInt(HEIGHT, ui.heightBar.progress).apply()
                FieldParams.height = ui.heightBar.progress
            }
            ui.widthBar -> {
                ui.widthNumber.text = seekBar.progress.toString()
                FieldParams.width = ui.widthBar.progress
                sharedPreferences.edit().putInt(WIDTH, ui.widthBar.progress).apply()
            }
            ui.minesBar -> {
                ui.minesProcentNumber.text = seekBar.progress.toString()
                sharedPreferences.edit().putInt(MINES, ui.minesBar.progress).apply()
                FieldParams.mines = FieldParams.height*FieldParams.width*ui.minesBar.progress/100
            }
        }
    }
}