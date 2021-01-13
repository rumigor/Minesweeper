package com.lenecoproekt.minesweeper

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), OnSeekBarChangeListener {
    private lateinit var heightBar : SeekBar
    private lateinit var widthBar : SeekBar
    private lateinit var minesBar : SeekBar
    private lateinit var height : TextView
    private lateinit var width : TextView
    private lateinit var mines : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        heightBar = findViewById(R.id.heightChooser)
        widthBar = findViewById(R.id.widthChooser)
        minesBar = findViewById(R.id.minesProcent)
        height = findViewById(R.id.heigthNumber)
        width = findViewById(R.id.widthNumber)
        mines = findViewById(R.id.minesProcentNumber)
        heightBar.progress = 10
        heightBar.max = 50
        height.text = heightBar.progress.toString()
        widthBar.progress = 10
        widthBar.max = 50
        width.text = widthBar.progress.toString()
        minesBar.progress = 10
        minesBar.max = 30
        mines.text = minesBar.progress.toString()
        heightBar.setOnSeekBarChangeListener(this)
        widthBar.setOnSeekBarChangeListener(this)
        minesBar.setOnSeekBarChangeListener(this)
        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            val intent = Intent(this@MainActivity, GameActivity::class.java)
            intent.putExtra("HEIGHT", heightBar.progress).putExtra("WIDTH", widthBar.progress).putExtra("MINES", minesBar.progress)
            startActivity(intent)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        if (seekBar === heightBar) {
            height!!.text = seekBar.progress.toString()
        } else if (seekBar === widthBar) {
            width!!.text = seekBar.progress.toString()
        } else if (seekBar === minesBar) {
            mines!!.text = seekBar.progress.toString()
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {
        if (seekBar === heightBar) {
            height!!.text = seekBar.progress.toString()
        } else if (seekBar === widthBar) {
            width!!.text = seekBar.progress.toString()
        } else if (seekBar === minesBar) {
            mines!!.text = seekBar.progress.toString()
        }
    }
}