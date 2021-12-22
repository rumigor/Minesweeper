package com.lenecoproekt.minesweeper.logic

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class GameObject(var x: Int, var y: Int, var isMine: Boolean) : Parcelable {
    var countMineNeighbors = 0
    var isOpen = false
    var isFlag = false
}