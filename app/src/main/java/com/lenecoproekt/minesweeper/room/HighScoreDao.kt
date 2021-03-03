package com.lenecoproekt.minesweeper.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HighScoreDao {
    @Query("SELECT * FROM highscore")
    suspend fun getAll(): List<HighScore>

    @Query("SELECT * FROM highscore WHERE uid IN (:highScoreIds)")
    suspend fun loadAllByIds(highScoreIds: IntArray): List<HighScore>

    @Query("SELECT * FROM highscore WHERE name LIKE :name")
    suspend fun findByName(name: String): HighScore

    @Insert
    suspend fun insertAll(vararg highScore: HighScore)

    @Delete
    suspend fun delete(highScore: HighScore)
}