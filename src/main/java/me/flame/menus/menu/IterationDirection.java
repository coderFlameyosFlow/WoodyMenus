package me.flame.menus.menu;

/**
 * Iteration direction of a {@link Menu}
 * @author Mqzn (Mqzen), FlameyosFlow (Mostly Mqzen)
 */
@SuppressWarnings("unused")
public enum IterationDirection {
    HORIZONTAL {
        @Override
        public Slot shift(Slot oldPos, int maxRows) {
            int oldCol = oldPos.column;
            int oldRow = oldPos.row;

            return oldCol == 9 && oldRow < maxRows
                    ? oldPos.setSlot(oldRow + 1, 1)
                    : oldPos.setSlot(oldRow, oldCol + 1);
        }
    },

    VERTICAL {
        @Override
        public Slot shift(Slot oldPos, int maxRows) {
            int oldCol = oldPos.column;
            int oldRow = oldPos.row;

            return (oldCol < 9 && oldRow == maxRows)
                    ? oldPos.setSlot(1, oldCol + 1)
                    : oldPos.setSlot(oldRow + 1, oldCol);
        }
    },

    UPWARDS_ONLY {
        @Override
        public Slot shift(Slot oldPos, int maxRows) {
            return new Slot(oldPos.row - 1, oldPos.column);
        }
    },

    DOWNWARDS_ONLY {
        @Override
        public Slot shift(Slot oldPos, int maxRows) {
            return new Slot(oldPos.row + 1, oldPos.column);
        }
    },

    RIGHT_ONLY {
        @Override
        public Slot shift(Slot oldPos, int maxRows) {
            int col = oldPos.column + 1;
            return col > 9 ? Slot.NaS : oldPos.setSlot(oldPos.row, col);
        }
    },

    LEFT_ONLY {
        @Override
        public Slot shift(Slot oldPos, int maxRows) {
            int col = oldPos.column - 1;
            return col < 0 ? Slot.NaS : oldPos.setSlot(oldPos.row, col);
        }
    },

    RIGHT_UPWARDS_ONLY {
        @Override
        public Slot shift(Slot oldPos, int maxRows) {
            Slot upwardSlot = UPWARDS_ONLY.shift(oldPos, maxRows);
            Slot rightSlot = RIGHT_ONLY.shift(oldPos, maxRows);
            return oldPos.setSlot(upwardSlot.row, rightSlot.column);
        }
    },

    RIGHT_DOWNWARDS_ONLY {
        @Override
        public Slot shift(Slot oldPos, int maxRows) {
            Slot downwardSlot = DOWNWARDS_ONLY.shift(oldPos, maxRows);
            Slot rightSlot = RIGHT_ONLY.shift(oldPos, maxRows);
            return oldPos.setSlot(downwardSlot.row, rightSlot.column);
        }
    },

    LEFT_UPWARDS {
        @Override
        public Slot shift(Slot oldPos, int maxRows) {
            Slot upwardSlot = UPWARDS_ONLY.shift(oldPos, maxRows);
            Slot leftSlot = LEFT_ONLY.shift(oldPos, maxRows);
            return oldPos.setSlot(upwardSlot.row, leftSlot.column);
        }
    },

    LEFT_DOWNWARDS {
        @Override
        public Slot shift(Slot oldPos, int maxRows) {
            Slot downwardSlot = DOWNWARDS_ONLY.shift(oldPos, maxRows);
            Slot leftSlot = LEFT_ONLY.shift(oldPos, maxRows);
            return oldPos.setSlot(downwardSlot.row, leftSlot.column);
        }
    },

    BACKWARDS_HORIZONTAL {
        @Override
        public Slot shift(Slot oldPos, int maxRows) {
            int oldCol = oldPos.column;
            int oldRow = oldPos.row;

            if (oldCol == 1 && oldRow >= 1) {
                oldCol = 9;
                oldRow--;
            } else {
                oldCol--;
            }

            return oldPos.setSlot(oldRow, oldCol);
        }
    },

    BACKWARDS_VERTICAL {
        @Override
        public Slot shift(Slot oldPos, int maxRows) {
            int oldCol = oldPos.column;
            int oldRow = oldPos.row;

            if (oldCol > 1 && oldRow < 6) {
                oldCol--;
                oldRow = 6;
            } else {
                oldRow--;
            }

            return oldPos.setSlot(oldRow, oldCol);
        }
    };

    /**
     * Shifting the slot "oldPos" by "maxRows" to the next slot.
     *
     * @param  oldPos   the old position to shift FROM
     * @param  maxRows  the maximum amount of rows the menu may have
     * @return          the shifted position that can AND will depend on the {@link IterationDirection} direction
     *                  <p>How different directions work:</p>
     *                  <p></p>
     *                  HORIZONTAL: 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, etc.
     *                  <p></p>
     *                  VERTICAL: 0, 9, 18, 27, 36, 45, 1, 10, etc.
     *                  <p></p>
     *                  UPWARDS_ONLY: 45, 36, 27, 18, 9, 0.
     *                  <p></p>
     *                  DOWNWARDS_ONLY: 0, 9, 18, 27, 36, 45.
     *                  <p></p>
     *                  RIGHT_ONLY: 0, 1, 2, 3, 4, 5, 6, 7, 8, 9.
     *                  <p></p>
     *                  LEFT_ONLY: 9, 8, 7, 6, 5, 4, 3, 2, 1, 0
     *                  <p></p>
     *                  RIGHT_UPWARDS_ONLY: 45, 37, 29, 21, 13, 5, 0.
     *                  <p></p>
     *                  RIGHT_DOWNWARDS_ONLY: 0, 10, 20, 30, 40, 50.
     *                  <p></p>
     *                  LEFT_UPWARDS: 53, 44, 35, 26, 17, 8, 0.
     *                  <p></p>
     *                  LEFT_DOWNWARDS: 7, 16, 25, 34, 40, 48.
     *                  <p></p>
     *                  BACKWARDS_HORIZONTAL: 53, 52, 51, 50, 49, etc.
     *                  <p></p>
     *                  BACKWARDS_VERTICAL: 53, 44, 35, 26, 17, 17, 8, 52, etc.
     *
     */
    public abstract Slot shift(Slot oldPos, int maxRows);
}
