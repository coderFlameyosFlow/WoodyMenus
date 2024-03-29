package me.flame.menus.menu.iterator;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.IMenu;
import me.flame.menus.menu.IterationDirection;
import me.flame.menus.menu.Slot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 *
 * @author Mqzen, FlameyosFlow (Mostly Mqzen)
 * @since 1.2.0, 28/8/2023
 *
 * A special class made to iterate over complex menus.
 */
@SuppressWarnings("unused")
public final class MenuIterator implements Iterator<MenuItem> {
    @NotNull
    private Slot position;

    @NotNull
    private final IterationDirection direction;

    @NotNull
    private final IMenu menu;

    private Slot next;

    private final int rows;

    private static final String NOTHING_MORE_NEXT =
            "Used MenuIterator#next() but nothing more" +
            "\nFix: Use hasNext() beforehand to avoid this error.";

    private static final String NOTHING_MORE_NEXT_OPTIONAL =
            "Used MenuIterator#nextOptional() but nothing more" +
            "\nFix: Use hasNext() beforehand to avoid this error.";

    private static final String NOTHING_MORE_NEXT_NOT_NULL =
            "Used MenuIterator#nextNotNull() but no non-null value was found";

    private static final String GREATER_THAN_ONE_ONLY =
            "Starting row and column must be 1 or greater only." +
            "\nFix: If you're using an algorithm for rows/cols, you might wanna check it";

    public MenuIterator(int startingRow, int startingCol,
                        @NotNull IterationDirection direction,
                        @NotNull IMenu menu) {
        Slot prepos = new Slot(startingRow, startingCol);
        if (!prepos.isValid()) throw new IllegalArgumentException(GREATER_THAN_ONE_ONLY);
        this.menu = menu;
        this.next = prepos;
        this.position = prepos;
        this.direction = direction;

        this.rows = menu.rows();
    }

    public MenuIterator(@NotNull IterationDirection direction, @NotNull IMenu menu) {
        this.menu = menu;

        Slot first = Slot.getFirst();
        this.next = first;
        this.position = first;
        this.direction = direction;

        this.rows = menu.rows();
    }

    /**
     * Retrieves the next slot in the menu.
     *
     * @param emptyOnly  a boolean indicating whether to retrieve only empty slots
     * @return           the next empty slot in the menu, or null if no empty slot is found
     */
    public @Nullable Slot nextSlot(boolean emptyOnly) {
        if (!emptyOnly) return nextSlot();

        while (menu.hasItem(position)) {
            position = direction.shift(position, this.rows);
            if (!position.isValid()) return null;
        }

        // when it becomes empty
        return position;
    }

    /**
     * Retrieves the next slot in the menu.
     * @return           the next empty slot in the menu, or null if no empty slot is found
     */
    public @Nullable Slot nextSlot() {
        position = next;
        next = null;
        return position;
    }

    @Override
    public boolean hasNext() {
        if (next != null) return true;
        next = direction.shift(position, this.rows);
        return next.isValid();
    }

    @Override
    public MenuItem next() {
        Slot slot = nextSlot();
        if (slot != null) return menu.getItem(slot);
        throw new NoSuchElementException(NOTHING_MORE_NEXT);
    }

    /**
     * Retrieves the next non-null MenuItem in the menu.
     *
     * @return         	the next non-null MenuItem in the menu
     */
    public @NotNull MenuItem nextNotNull() {
        while (hasNext()) {
            Slot slot = nextSlot();
            if (slot == null)
                throw new NoSuchElementException(NOTHING_MORE_NEXT_NOT_NULL +
                    "\nFix: Everything after slot " + position.getSlot() + " is empty/null."
                );

            MenuItem item = menu.getItem(slot);
            if (item != null) return item;
        }

        throw new NoSuchElementException(NOTHING_MORE_NEXT_NOT_NULL +
            "\nFix: Everything after slot " + position.getSlot() + " is empty/null."
        );
    }


    /**
     * Retrieves the next optional menu item.
     *
     * @return         	an Optional object containing the next MenuItem, if available.
     *                  Returns an empty Optional if the slot in the menu is empty.
     *                  Returns an empty Optional if there are no more items.
     * @throws NoSuchElementException if there are no more items in the menu.
     */
    public Optional<MenuItem> nextOptional() {
        Slot slot = nextSlot();
        if (slot != null) return menu.get(slot);
        throw new NoSuchElementException(NOTHING_MORE_NEXT_OPTIONAL);
    }
}
