package me.flame.menus.menu.iterator;

import me.flame.menus.menu.BaseMenu;
import me.flame.menus.menu.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Mqzen
 * @date 28/8/2023
 *
 * A special class made to iterate over complex menus.
 */
public final class MenuIterator {

	@NotNull
	private Slot currentPosition;

	@NotNull
	private final IterationDirection direction;

	@NotNull
	private final BaseMenu<?> menu;

	public MenuIterator(int startingRow, int startingCol,
											@NotNull IterationDirection direction,
											@NotNull BaseMenu<?> menu) {
		this.menu = menu;
		this.currentPosition = new Slot(startingRow, startingCol);
		this.direction = direction;
	}

	public @Nullable Slot nextSlot(boolean emptyOnly) {

		if(!emptyOnly) {
			Slot newPos = direction.shift(currentPosition, menu.getRows());
			if(newPos.getRow() >= menu.getRows() || newPos.getRow() < 0
							|| newPos.getColumn() > 8 || newPos.getColumn() < 0) {
				return null;
			}
			currentPosition = newPos;
			return newPos;
		}

		while(menu.getOptionalItem(currentPosition).isPresent()) {
			Slot shiftedPos = direction.shift(currentPosition, menu.getRows());

			if(shiftedPos.getRow() >= menu.getRows() || shiftedPos.getRow() < 0
							|| shiftedPos.getColumn() > 8 || shiftedPos.getColumn() < 0) {
				return null;
			}

			currentPosition = shiftedPos;
		}

		//when it becomes empty
		return currentPosition;
	}

	public @Nullable Slot nextSlot() {
		return nextSlot(false);
	}

	public enum IterationDirection {


		HORIZONTAL() {
			@Override
			Slot shift(Slot oldPos, int maxRows) {

				int oldCol = oldPos.getColumn();
				int oldRow = oldPos.getRow();

				if(oldCol == 8 && oldRow < maxRows-1) {
					oldRow++;
					oldCol = 0;
				}else {
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

				if(oldCol < 8 && oldRow == maxRows-1) {
					oldCol++;
					oldRow = 0;
				}else {
					oldRow++;
				}

				return new Slot(oldRow, oldCol);
			}
		},

		UPWARDS_ONLY {

			@Override
			Slot shift(Slot oldPos, int maxRows) {
				int row = oldPos.getRow()-1;
				if(row < 0) {
					row = oldPos.getRow();
				}
				return new Slot(row, oldPos.getColumn());
			}
		},

		DOWNWARDS_ONLY {
			@Override
			Slot shift(Slot oldPos, int maxRows) {
				int row = oldPos.getRow()+1;
				if(row > maxRows) {
					row = oldPos.getRow();
				}
				return new Slot(row, oldPos.getColumn());
			}

		},

		RIGHT_ONLY {
			@Override
			Slot shift(Slot oldPos, int maxRows) {
				int col = oldPos.getColumn()+1;
				if(col > 8) {
					col = oldPos.getColumn();
				}
				return new Slot(oldPos.getRow(), col);
			}
		},


		LEFT_ONLY{
			@Override
			Slot shift(Slot oldPos, int maxRows) {
				int col = oldPos.getColumn()-1;
				if(col < 0) {
					col = oldPos.getColumn();
				}
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
