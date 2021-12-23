package com.lenecoproekt.minesweeper

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

object AppPreferences {
    private var sharedPreferences: SharedPreferences? = null

    fun setup(context: Context) {
        sharedPreferences = context.getSharedPreferences("MinesweeperApp.sharedprefs", MODE_PRIVATE)
    }

    var width: Int?
        get() = Key.WIDTH.getInt()
        set(value) = Key.WIDTH.setInt(value)

    var height: Int?
        get() = Key.HEIGHT.getInt()
        set(value) = Key.HEIGHT.setInt(value)

    var mines: Int?
        get() = Key.MINES.getInt()
        set(value) = Key.MINES.setInt(value)



    private enum class Key {
        WIDTH, HEIGHT, MINES;

        fun getInt(): Int? = if (sharedPreferences!!.contains(name)) sharedPreferences!!.getInt(name, 0) else null

        fun setInt(value: Int?) = value?.let { sharedPreferences!!.edit { putInt(name, value) } } ?: remove()

        fun remove() = sharedPreferences!!.edit { remove(name) }
    }
}