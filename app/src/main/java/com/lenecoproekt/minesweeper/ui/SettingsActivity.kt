package com.lenecoproekt.minesweeper.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import com.lenecoproekt.minesweeper.*
import com.lenecoproekt.minesweeper.databinding.ActivitySettingsBinding
import android.view.View

import android.widget.RadioButton


class SettingsActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    companion object {
        fun getStartIntent(context: Context) = Intent(context, SettingsActivity::class.java)
    }

    private val ui: ActivitySettingsBinding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
        ui.minesBar.progress = AppPreferences.mines!!
        ui.minesBar.max = 30
        ui.minesProcentNumber.text = ui.minesBar.progress.toString()
        ui.minesBar.setOnSeekBarChangeListener(this)
        val startButton = findViewById<Button>(R.id.startButton)

        when (AppPreferences.height!! * AppPreferences.width!!) {
            100 -> ui.radioButton.isChecked = true
            144 -> ui.radioButton2.isChecked = true
            225 -> ui.radioButton3.isChecked = true
            200 -> ui.radioButton4.isChecked = true
            300 -> ui.radioButton5.isChecked = true
            400 -> ui.radioButton6.isChecked = true
        }

        ui.radioButton.setOnClickListener(radioButtonClickListener)
        ui.radioButton2.setOnClickListener(radioButtonClickListener)
        ui.radioButton3.setOnClickListener(radioButtonClickListener)
        ui.radioButton4.setOnClickListener(radioButtonClickListener)
        ui.radioButton5.setOnClickListener(radioButtonClickListener)
        ui.radioButton6.setOnClickListener(radioButtonClickListener)




        startButton.setOnClickListener {
            AppPreferences.mines = ui.minesBar.progress
            val intent = Intent(this@SettingsActivity, GameActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        when (seekBar) {
            ui.minesBar -> {
                ui.minesProcentNumber.text = seekBar.progress.toString()
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {
        when (seekBar) {
            ui.minesBar -> {
                ui.minesProcentNumber.text = seekBar.progress.toString()
                AppPreferences.mines =
                    AppPreferences.height!! * AppPreferences.width!! * ui.minesBar.progress / 100
            }
        }
    }

    var radioButtonClickListener: View.OnClickListener = View.OnClickListener { v ->
        val rb = v as RadioButton
        when (rb.id) {
            R.id.radioButton -> {
                AppPreferences.width = 10
                AppPreferences.height = 10
            }
            R.id.radioButton2 -> {
                AppPreferences.width = 12
                AppPreferences.height = 12
            }
            R.id.radioButton3 -> {
                AppPreferences.width = 15
                AppPreferences.height = 15
            }
            R.id.radioButton4 -> {
                AppPreferences.width = 10
                AppPreferences.height = 20
            }
            R.id.radioButton5 -> {
                AppPreferences.width = 15
                AppPreferences.height = 20
            }
            R.id.radioButton6 -> {
                AppPreferences.width = 20
                AppPreferences.height = 20
            }

            else -> {}
        }
    }


}