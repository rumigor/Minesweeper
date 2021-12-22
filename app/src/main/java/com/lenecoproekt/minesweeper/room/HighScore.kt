package com.lenecoproekt.minesweeper.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HighScore(
        @ColumnInfo(name = "name") val name: String?,
        @ColumnInfo(name = "fieldSize") val fieldSize: String,
        @ColumnInfo(name = "mines") val mines: Int,
        @ColumnInfo(name = "time") val time: String,
        @ColumnInfo(name = "score") val score: Int
) {
    @PrimaryKey (autoGenerate = true) var id: Int = 0
}
