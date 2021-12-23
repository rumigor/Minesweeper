package com.lenecoproekt.minesweeper.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lenecoproekt.minesweeper.*
import com.lenecoproekt.minesweeper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        fun getStartIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    private val ui: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppPreferences.setup(applicationContext)
        if (AppPreferences.height == null || AppPreferences.width == null || AppPreferences.mines == null){
            AppPreferences.height = 10
            AppPreferences.width = 10
            AppPreferences.mines = 10
        }
        setContentView(ui.root)
        ui.startGameButton.setOnClickListener {
            val intent = Intent(this@MainActivity, GameActivity::class.java)
            startActivity(intent)
        }
        ui.openSettingsButton.setOnClickListener{
            startActivity(SettingsActivity.getStartIntent(this))
        }
        ui.highScoreButton.setOnClickListener{
            startActivity(Intent(this@MainActivity, HighScoreActivity::class.java))
        }
    }
}