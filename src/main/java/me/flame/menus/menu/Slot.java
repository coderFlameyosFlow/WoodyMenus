package me.flame.menus.menu;

import lombok.Getter;

public final class Slot {
    private final @Getter int row;
    private final int col;
    private @Getter int slot;

    public Slot(int row, int col) {
        this.row = row;
        this.col = col;
        this.slot = ((row - 1) * 9) + (col - 1);
        slot = slot >= 54 ? -1 : slot;
    }

    public int getColumn() {
        return col;
    }

    public boolean isSlot() {
        return slot >= 0;
    }
}
