package me.flame.menus.menu;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class Slot {
    int row, slot, col;

    /**
     * First as in row 1, col 1.
     * Slot = 1 (always)
     */
    public static final Slot FIRST = getFirst();

    /**
     * Not A Slot; a purposeful invalid slot to save performance and memory.
     */
    public static final Slot NaS = getNaS();

    public Slot(final int row, final int col) {
        this.row = row;
        this.col = col;
        this.slot = ((row * 9) + col) - 10;
        this.slot = slot >= 54 ? -1 : slot;
    }

    public Slot(final int slot) {
        this.row = (slot / 9) + 1;
        this.col = (slot % 9) + 1;
        this.slot = slot >= 54 ? -1 : slot;
    }

    // fast copy constructor
    private Slot(final int row, final int col, final boolean dummy) {
        this.row = row;
        this.col = col;
    }

    /**
     * Sets the slot at the specified row and column.
     *
     * @param row the row index of the slot
     * @param col the column index of the slot
     * @return the updated Slot object
     */
    @Contract("_, _ -> this")
    public Slot setSlot(final int row, final int col) {
        if (row == this.row && col == this.col) return this;

        this.row = row;
        this.col = col;
        this.slot = ((row * 9) + col) - 10;
        this.slot = slot >= 54 ? -1 : slot;
        return this;
    }

    /**
     * Sets the slot at the specified row and column.
     * @param slot the slot
     * @return the updated Slot object
     */
    @Contract("_ -> this")
    @SuppressWarnings("UnusedReturnValue")
    public Slot setSlot(@NotNull final Slot slot) {
        this.row = slot.row;
        this.col = slot.col;

        int preSlot = slot.slot;
        this.slot = preSlot >= 54 ? -1 : preSlot;
        return this;
    }

    /**
     * Sets the slot at the specified row and column.
     * @param slot the slot
     * @return the updated Slot object
     */
    @Contract("_ -> this")
    @SuppressWarnings("UnusedReturnValue")
    public Slot setSlot(final int slot) {
        this.row = (slot / 9) + 1;
        this.col = (slot % 9) + 1;
        this.slot = slot >= 54 ? -1 : slot;
        return this;
    }

    /**
     * Creates a copy of the Slot object.
     *
     * @return	A new Slot object that is an exact copy of the original Slot.
     */
    @NotNull
    public Slot copy() {
        final Slot slot = new Slot(row, col, true);
        slot.slot = this.slot;
        return slot;
    }

    /**
     * Get the column of the slot.
     * @return the column
     */
    public int getColumn() {
        return col;
    }

    /**
     * Get the row of the slot.
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * Get the slot.
     * @return the slot
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Check if the slot is a valid slot.
     * @apiNote If the slot is equal to -1, it's invalid; fast check.
     * @return true if the slot does not equal -1.
     */
    public boolean isSlot() {
        return slot != -1;
    }

    // faster NaS (Not A Slot) alternative to creating a Slot like new Slot(8, 1);
    @NotNull
    private static Slot getNaS() {
        Slot slot = new Slot(-1, -1, true);
        slot.slot = -1;
        return slot;
    }

    // faster FIRST slot compared to doing "new Slot(1, 1)"
    @NotNull
    private static Slot getFirst() {
        Slot slot = new Slot(1, 1, true);
        slot.slot = 1;
        return slot;
    }
}
