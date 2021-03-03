package com.lenecoproekt.minesweeper.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
data class HighScore(
        @PrimaryKey (autoGenerate = true) val id: Long,
        @ColumnInfo(name = "name") val name: String?,
        @ColumnInfo(name = "fieldSize") val fieldSize: String,
        @ColumnInfo(name = "time") val time: String,
        @ColumnInfo(name = "score") val score: Int
)
