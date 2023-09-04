package me.flame.menus.menu.iterator;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.BaseMenu;
import me.flame.menus.menu.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 *
 * @author Mqzen, FlameyosFlow (Mostly Mqzen)
 * @date 28/8/2023
 *
 * A special class made to iterate over complex menus.
 */
@SuppressWarnings("unused")
public final class MenuIterator implements Iterator<MenuItem> {

    @NotNull
    private Slot currentPosition;

    @NotNull
    private final IterationDirection direction;

    @NotNull
    private final BaseMenu<?> menu;

    private final int menuRows;

    public MenuIterator(int startingRow, int startingCol,
                        @NotNull IterationDirection direction,
                        @NotNull BaseMenu<?> menu) {
        if (startingRow < 1 || startingCol < 1)
            throw new IllegalArgumentException(
                    "Starting row and column must be greater than 1 or equal. not under 0" +
                    "\nFix: If you're using an algorithm for rows and/or cols, you might wanna check it."
            );
        this.menu = menu;
        this.currentPosition = new Slot(startingRow, startingCol);
        this.direction = direction;
        this.menuRows = menu.getRows();
    }

    public @Nullable Slot nextSlot(boolean emptyOnly) {
        if (!emptyOnly) {
            Slot newPos = direction.shift(currentPosition, this.menuRows);
            currentPosition = newPos;
            if (!currentPosition.isSlot()) {
                return null;
            }
            return newPos;
        }

        while (menu.hasItem(currentPosition)) {
            Slot shiftedPos = direction.shift(currentPosition, this.menuRows);

            if (!shiftedPos.isSlot()) {
                return null;
            }

            currentPosition = shiftedPos;
        }

        // when it becomes empty
        return currentPosition;
    }

    public @Nullable Slot nextSlot() {
        Slot newPos = direction.shift(currentPosition, this.menuRows);
        currentPosition = newPos;
        return !newPos.isSlot() ? null : newPos;
    }

    @Override
    public boolean hasNext() {
        return currentPosition.isSlot();
    }

    @Override
    public @Nullable MenuItem next() {
        Slot slot = nextSlot();
        if (slot == null)
            throw new NoSuchElementException("Used MenuIterator#next() but no more items to iterate over");
        return menu.getItem(slot);
    }

    public @NotNull MenuItem nextNotNull() {
        // don't be fooled, this is only 1 loop through the menu, not 1 AND recursion, only one happens.
        Slot slot = nextSlot();
        if (slot == null) {
            while (hasNext()) {
                slot = nextSlot();
                if (slot == null) continue;
                MenuItem item = menu.getItem(slot);
                if (item != null) {
                    return item;
                }
            }

            throw new NoSuchElementException("Used MenuIterator#next() but no more items to iterate over");
        }

        MenuItem item = menu.getItem(slot);
        if (item != null) {
            return item;
        }
        return nextNotNull();
    }

    public Optional<MenuItem> nextOptional() {
        Slot slot = nextSlot();
        if (slot == null)
            throw new NoSuchElementException("Used MenuIterator#next() but no more items to iterate over");
        return menu.get(slot);
    }

    public enum IterationDirection {
        HORIZONTAL() {
            @Override
            Slot shift(Slot oldPos, int maxRows) {

                int oldCol = oldPos.getColumn();
                int oldRow = oldPos.getRow();

                if (oldCol == 9 && oldRow < maxRows) {
                    oldCol = 1;
                    oldRow++;
                } else {
                    oldCol++;
                }

                return new Slot(oldRow, oldCol);
            }
        },

        VERTICAL() {
            @Override
            Slot shift(Slot oldPos, int maxRows) {
                int oldCol = oldPos.getColumn();
                int oldRow = oldPos.getRow();

                if (oldCol < 9 && oldRow == maxRows) {
                    oldCol++;
                    oldRow = 1;
                } else {
                    oldRow++;
                }

                return new Slot(oldRow, oldCol);
            }
        },

        UPWARDS_ONLY {

            @Override
            Slot shift(Slot oldPos, int maxRows) {
                int oldRow = oldPos.getRow();
                int row = oldRow - 1;
                if (row < 1) {
                    row = oldRow;
                }
                return new Slot(row, oldPos.getColumn());
            }
        },

        DOWNWARDS_ONLY {
            @Override
            Slot shift(Slot oldPos, int maxRows) {
                int oldRow = oldPos.getRow();
                int row = oldRow + 1;
                if (row > maxRows) {
                    row = oldRow;
                }
                return new Slot(row, oldPos.getColumn());
            }
        },

        RIGHT_ONLY {
            @Override
            Slot shift(Slot oldPos, int maxRows) {
                int oldCol = oldPos.getColumn() + 1;
                int col = oldCol > 9 ? oldCol - 1 : oldCol;
                return new Slot(oldPos.getRow(), col);
            }
        },


        LEFT_ONLY {
            @Override
            Slot shift(Slot oldPos, int maxRows) {
                int oldCol = oldPos.getColumn() - 1;
                int col = oldCol < 1 ? oldCol + 1 : oldCol;
                return new Slot(oldPos.getRow(), col);
            }
        },


        RIGHT_UPWARDS_ONLY {
            @Override
            Slot shift(Slot oldPos, int maxRows) {
                Slot upwardSlot = UPWARDS_ONLY.shift(oldPos, maxRows);
                int row = upwardSlot.getRow();

                Slot rightSlot = RIGHT_ONLY.shift(oldPos, maxRows);
                int col = rightSlot.getColumn();

                return new Slot(row, col);
            }
        },


        RIGHT_DOWNWARDS_ONLY {
            @Override
            Slot shift(Slot oldPos, int maxRows) {
                Slot downwardSlot = DOWNWARDS_ONLY.shift(oldPos, maxRows);
                int row = downwardSlot.getRow();

                Slot rightSlot = RIGHT_ONLY.shift(oldPos, maxRows);
                int col = rightSlot.getColumn();

                return new Slot(row, col);
            }
        },

        LEFT_UPWARDS {
            @Override
            Slot shift(Slot oldPos, int maxRows) {
                Slot upwardSlot = UPWARDS_ONLY.shift(oldPos, maxRows);
                int row = upwardSlot.getRow();

                Slot leftSlot = LEFT_ONLY.shift(oldPos, maxRows);
                int col = leftSlot.getColumn();

                return new Slot(row, col);
            }
        },

        LEFT_DOWNWARDS {
            @Override
            Slot shift(Slot oldPos, int maxRows) {
                Slot downwardSlot = DOWNWARDS_ONLY.shift(oldPos, maxRows);
                int row = downwardSlot.getRow();

                Slot leftSlot = LEFT_ONLY.shift(oldPos, maxRows);
                int col = leftSlot.getColumn();

                return new Slot(row, col);
            }
        };

        abstract Slot shift(Slot oldPos, int maxRows);
    }
}
