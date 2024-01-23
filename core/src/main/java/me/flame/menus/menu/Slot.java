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

    /**
     * Creates a new slot with the specified row and column.
     * @param row the row
     * @param column the col
     * @deprecated Use {@link #of(int, int)}, constructor will be private in 2.1.0
     */
    @Deprecated
    public Slot(final int row, final int column) {
        this(row, column, false);
        checkRowColumnSafety(slot, row, column);

        this.slot = (column + (row - 1) * 9) - 1;
        this.slot = this.slot >= 54 ? -1 : this.slot;
    }

    private Slot(final int row, final int column, final int slot) {
        this(row, column, false);
        this.slot = slot >= 54 ? -1 : slot;
        checkRowColumnSafety(slot, row, column);
    }

    /**
     * Creates a new slot with the specified row and column.
     * @param slot the slot
     * @deprecated Use {@link #of(int)}, constructor will be private in 2.1.0
     */
    @Deprecated
    public Slot(final int slot) {
        this(slot / 9 + 1, slot % 9 + 1, false);
        checkRowColumnSafety(slot, row, column);

        this.slot = slot >= 54 ? -1 : slot;
    }

    private static void checkRowColumnSafety(int slot, int row, int column) {
        if (row > 6) {
            throw new IllegalArgumentException(
                    "Row expected to be between 1 and 6, got " + row +
                    "\nFix: slot = (column + (row - 1) * 9) - 1, slot = " + slot
            );
        }
        if (column > 9) {
            throw new IllegalArgumentException(
                    "Column expected to be between 1 and 9, got " + column +
                    "\nFix: slot = (column + (row - 1) * 9) - 1, slot = " + slot
            );
        }
    }

    @Contract("_ -> new")
    public static @NotNull Slot of(int slot) {
        return new Slot(slot / 9 + 1, slot % 9 + 1, slot);
    }

    @Contract("_, _ -> new")
    public static @NotNull Slot of(int row, int column) {
        return new Slot(row, column, (column + (row - 1) * 9) - 1);
    }

    public static @NotNull Slot ofUnsafe(int slot) {
        Slot position = new Slot((slot / 9) + 1, (slot % 9) + 1, true);

        int positionSlot = (position.column + (position.row - 1) * 9) - 1;
        position.slot = positionSlot >= 54 ? -1 : positionSlot;
        return position;
    }

    public static Slot ofUnsafe(int row, int column) {
        if (column > 9 || row > 6) return Slot.NaS;
        Slot position = new Slot(row, column, true);
        int positionSlot = (column + (row - 1) * 9) - 1;
        position.slot = positionSlot >= 54 ? -1 : positionSlot;
        return position;
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
        return new Slot(row, column, slot);
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
     * Check if the slot is valid.
     * @apiNote If the slot is equal to -1, it's invalid; fast check.
     * @return true if the slot does not equal -1.
     */
    public boolean isValid() {
        return slot != -1;
    }

    // faster NaS (Not A Slot) alternative to creating a Slot like new Slot(8, 1);
    @NotNull
    private static Slot getNaS() {
        return new Slot(-1, -1, -1);
    }

    // faster FIRST slot compared to doing "new Slot(1, 1)"
    @NotNull
    public static Slot getFirst() {
        return new Slot(1, 1, 0);
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
