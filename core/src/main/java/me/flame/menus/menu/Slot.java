package me.flame.menus.menu;

import com.google.errorprone.annotations.CompileTimeConstant;

import lombok.Getter;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Slot wrapper of a (row, column) pair to represent a slot in a menu.
 * <p>
 * Fail-fast if row or column is out of bounds with {@link #isValid()}.
 * @author FlameyosFlow
 */
@Getter
@SuppressWarnings({ "unused", "JavadocDeclaration" })
public final class Slot {
    /**
     *  Get the row of the slot.
     *
     * @return the row
     */
    int row, /**
     *  Get the slot.
     *
     * @return the slot
     */
            slot, /**
     *  Get the column of the slot.
     *
     * @return the column
     */
            column;

    /**
     * First as in row 1, col 1.
     * Slot = 0 (always)
     */
    @CompileTimeConstant
    public static final Slot FIRST = getFirst();

    /**
     * Not A Slot; a purposeful invalid slot to save performance and memory.
     */
    @CompileTimeConstant
    public static final Slot NaS = getNaS();

    public Slot(final int row, final int column) {
        this.row = row;
        this.column = column;

        this.slot = (column + (row - 1) * 9) - 1;
        this.slot = this.slot >= 54 ? -1 : this.slot;
    }

    public Slot(final int slot) {
        this.slot = slot >= 54 ? -1 : slot;
        this.row = (slot / 9) + 1;
        this.column = (slot % 9) + 1;
    }

    // fast copy constructor
    private Slot(final int row, final int column, final boolean dummy) {
        this.row = row;
        this.column = column;
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
        if (row == this.row && col == this.column) return this;

        this.row = row;
        this.column = col;
        this.slot = (column + (row - 1) * 9) - 1;
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
        this.column = slot.column;
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
        this.column = (slot % 9) + 1;
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
        final Slot slot = new Slot(row, column, true);
        slot.slot = this.slot;
        return slot;
    }

    /**
     * Check if the slot is a valid slot.
     * @apiNote If the slot is equal to -1, it's invalid; fast check.
     * @return true if the slot does not equal -1.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.1.0")
    public boolean isSlot() {
        return slot != -1;
    }

    /**
     * Check if the slot is a valid slot.
     * @apiNote If the slot is equal to -1, it's invalid; fast check.
     * @return true if the slot does not equal -1.
     */
    public boolean isValid() {
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
    public static Slot getFirst() {
        Slot slot = new Slot(1, 1, true);
        slot.slot = 0;
        return slot;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Slot)) return false;
        Slot slot = (Slot) o;
        return (slot == this) || (slot.slot == this.slot && slot.row == row && slot.column == column);
    }

    public boolean equals(int slot) {
        return slot == this.slot && slot % 9 == column && slot / 9 == row;
    }
}
