package com.lenecoproekt.minesweeper.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lenecoproekt.minesweeper.R
import com.lenecoproekt.minesweeper.databinding.ActivityHighScoreBinding
import com.lenecoproekt.minesweeper.room.HighScoreDatabase
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class HighScoreActivity : AppCompatActivity(), CoroutineScope {
    private val ui: ActivityHighScoreBinding by lazy { ActivityHighScoreBinding.inflate(layoutInflater) }
    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Main + Job()
    }
    private lateinit var adapter: HighScoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
        initRecycleView()
    }

    private fun initRecycleView() {
        val database = HighScoreDatabase.getInstance(application)
        val highScoreDao = database.highScoreDao
        adapter = HighScoreAdapter(highScoreDao)
        launch { adapter.records = highScoreDao.getAll() }
        ui.recordsRecycleView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }

}