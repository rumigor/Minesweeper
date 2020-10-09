package com.lenecoproekt.minesweeper;

import java.io.Serializable;

public class GameObject implements Serializable {
    public int x, y, countMineNeighbors;
    public boolean isMine, isOpen, isFlag;
    public GameObject (int x, int y, boolean isMine) {
        this.x = x;
        this.y = y;
        this.isMine = isMine;
    }
}
