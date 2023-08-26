package me.flame.menus.menu;

import lombok.Getter;

import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Slot {
    private static final int MAX_INVENTORY_SLOTS = 54;
    private final @Getter int row;
    private final int col;
    private final @Getter int slot;

    public Slot(int row, int col) {
        this.row = row;
        this.col = col;
        this.slot = Math.min(((col + (row - 1) * 9) - 1), MAX_INVENTORY_SLOTS);
    }

    public int getColumn() {
        return col;
    }
}
