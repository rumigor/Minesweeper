package com.lenecoproekt.minesweeper.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import com.lenecoproekt.minesweeper.*
import com.lenecoproekt.minesweeper.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    companion object {
        fun getStartIntent(context: Context) = Intent(context, SettingsActivity::class.java)
    }

    private val ui : ActivitySettingsBinding by lazy { ActivitySettingsBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
        ui.heightBar.progress = AppPreferences.height!!
        ui.heightBar.max = 50
        ui.heigthNumber.text = ui.heightBar.progress.toString()
        ui.widthBar.progress = AppPreferences.width!!
        ui.widthBar.max = 50
        ui.widthNumber.text = ui.widthBar.progress.toString()
        ui.minesBar.progress = AppPreferences.mines!!
        ui.minesBar.max = 30
        ui.minesProcentNumber.text = ui.minesBar.progress.toString()
        ui.heightBar.setOnSeekBarChangeListener(this)
        ui.widthBar.setOnSeekBarChangeListener(this)
        ui.minesBar.setOnSeekBarChangeListener(this)
        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            AppPreferences.height = ui.heightBar.progress
            AppPreferences.width = ui.widthBar.progress
            AppPreferences.mines = ui.minesBar.progress
            val intent = Intent(this@SettingsActivity, GameActivity::class.java)
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
                AppPreferences.height = ui.heightBar.progress
            }
            ui.widthBar -> {
                ui.widthNumber.text = seekBar.progress.toString()
                AppPreferences.width = ui.widthBar.progress
            }
            ui.minesBar -> {
                ui.minesProcentNumber.text = seekBar.progress.toString()
                AppPreferences.mines = AppPreferences.height!!* AppPreferences.width!!*ui.minesBar.progress/100
            }
        }
    }
}