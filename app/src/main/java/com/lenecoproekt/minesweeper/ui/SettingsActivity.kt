package com.lenecoproekt.minesweeper.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import com.lenecoproekt.minesweeper.*
import com.lenecoproekt.minesweeper.databinding.ActivitySettingsBinding
import com.lenecoproekt.minesweeper.logic.FieldParams

class SettingsActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {
    private val ui : ActivitySettingsBinding by lazy { ActivitySettingsBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
        ui.heightBar.progress = 10
        ui.heightBar.max = 30
        ui.heigthNumber.text = ui.heightBar.progress.toString()
        ui.widthBar.progress = 10
        ui.widthBar.max = 30
        ui.widthNumber.text = ui.widthBar.progress.toString()
        ui.minesBar.progress = 10
        ui.minesBar.max = 30
        ui.minesProcentNumber.text = ui.minesBar.progress.toString()
        ui.heightBar.setOnSeekBarChangeListener(this)
        ui.widthBar.setOnSeekBarChangeListener(this)
        ui.minesBar.setOnSeekBarChangeListener(this)
        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            FieldParams.height = ui.heightBar.progress
            FieldParams.width = ui.widthBar.progress
            FieldParams.mines = (FieldParams.height* FieldParams.width*ui.minesBar.progress)/100
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
                FieldParams.height = ui.heightBar.progress
            }
            ui.widthBar -> {
                ui.widthNumber.text = seekBar.progress.toString()
                FieldParams.width = ui.widthBar.progress
            }
            ui.minesBar -> {
                ui.minesProcentNumber.text = seekBar.progress.toString()
                FieldParams.mines = FieldParams.height* FieldParams.width*ui.minesBar.progress/100
            }
        }
    }
}