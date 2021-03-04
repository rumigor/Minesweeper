package com.lenecoproekt.minesweeper.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lenecoproekt.minesweeper.*
import com.lenecoproekt.minesweeper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val ui: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
        ui.startGameButton.setOnClickListener {
            val intent = Intent(this@MainActivity, GameActivity::class.java)
            startActivity(intent)
        }
        ui.openSettingsButton.setOnClickListener{
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }
        ui.highScoreButton.setOnClickListener{
            startActivity(Intent(this@MainActivity, HighScoreActivity::class.java))
        }
    }
}