package com.lenecoproekt.minesweeper.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lenecoproekt.minesweeper.R
import com.lenecoproekt.minesweeper.databinding.ItemBinding
import com.lenecoproekt.minesweeper.room.HighScore
import com.lenecoproekt.minesweeper.room.HighScoreDao
import kotlinx.coroutines.runBlocking

class HighScoreAdapter (highScoreDao: HighScoreDao) : RecyclerView.Adapter<HighScoreAdapter.HighScoreViewHolder>() {
    var records = runBlocking {highScoreDao.getAll().sortedByDescending {
        it.score
    }}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighScoreAdapter.HighScoreViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item, parent, false)
        return HighScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: HighScoreAdapter.HighScoreViewHolder, position: Int) {
        holder.bind(records[position])
    }

    override fun getItemCount(): Int = records.size

    inner class HighScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ui: ItemBinding = ItemBinding.bind(itemView)

        fun bind(highScore: HighScore) {
            with(highScore) {
                ui.winnerName.text = this.name
                ui.fSize.text = this.fieldSize
                ui.minesDisabled.text = this.mines.toString()
                ui.gameTime.text = this.time
                ui.getScore.text = this.score.toString()
            }
        }
    }

}