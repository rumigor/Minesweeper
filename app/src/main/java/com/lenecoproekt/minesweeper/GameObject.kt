package com.lenecoproekt.minesweeper

import java.io.Serializable

class GameObject(var x: Int, var y: Int, var isMine: Boolean) : Serializable {
    var countMineNeighbors = 0
    var isOpen = false
    var isFlag = false
}