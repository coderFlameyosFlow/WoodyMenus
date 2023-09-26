package me.flame.menus.menu.iterator;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.IterationDirection;
import me.flame.menus.menu.Slot;
import me.flame.menus.menu.Page;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 *
 * @author FlameyosFlow
 * @since 1.4.0
 *
 * MenuIterator alternative to iterate through Page instead
 */
@SuppressWarnings("unused")
public final class PageIterator implements Iterator<MenuItem> {
    @NotNull
    private final Page page;

    @NotNull
    private final IterationDirection direction;

    @NotNull
    private Slot position;

    private Slot next;

    private boolean isFirst = true;

    private final int rows;

    private static final String NOTHING_MORE_NEXT =
            "Used PageIterator#next() but nothing more" +
            "\nFix: Use hasNext() beforehand to avoid this error.";

    private static final String NOTHING_MORE_NEXT_OPTIONAL =
            "Used PageIterator#nextOptional() but nothing more" +
            "\nFix: Use hasNext() beforehand to avoid this error.";

    private static final String NOTHING_MORE_NEXT_NOT_NULL =
            "Used PageIterator#nextNotNull() but nothing more";

    private static final String GREATER_THAN_ONE_ONLY =
            "Starting row and column must be 1 or greater only." +
            "\nFix: If you're using an algorithm for rows/cols, you might wanna check it";

    public PageIterator(int startingRow, int startingCol,
                        @NotNull IterationDirection direction,
                        @NotNull Page page) {
        Slot prepos = new Slot(startingRow, startingCol);
        if (!prepos.isSlot()) throw new IllegalArgumentException(GREATER_THAN_ONE_ONLY);
        this.page = page;
        this.rows = page.rows();
        this.position = prepos;
        this.direction = direction;
    }

    public PageIterator(@NotNull IterationDirection direction, @NotNull Page page) {
        this.page = page;
        this.rows = page.rows();
        this.position = Slot.FIRST.copy();
        this.direction = direction;
    }

    /**
     * Retrieves the next slot in the menu.
     *
     * @param emptyOnly  a boolean indicating whether to retrieve only empty slots
     * @return           the next empty slot in the menu, or null if no empty slot is found
     */
    public @Nullable Slot nextSlot(boolean emptyOnly) {
        if (!emptyOnly) return nextSlot();

        while (true) {
            MenuItem item = page.getItem(position);
            if (item == null) break;

            position = direction.shift(position, this.rows);
            if (!position.isSlot()) return null;
        }

        // when it becomes empty
        return position;
    }

    /**
     * Retrieves the next slot in the menu.
     * @return           the next empty slot in the menu, or null if no empty slot is found
     */
    public @Nullable Slot nextSlot() {
        if (!isFirst) {
            position = next;
            next = null;
            return position;
        }
        isFirst = false;
        return position;
    }

    @Override
    public boolean hasNext() {
        if (next != null) return true;
        next = direction.shift(position, this.rows);
        return next.isSlot();
    }

    @Override
    public MenuItem next() {
        Slot slot = nextSlot();
        if (slot != null) return page.getItem(slot.getSlot());
        throw new NoSuchElementException(NOTHING_MORE_NEXT);
    }

    /**
     * Retrieves the next non-null MenuItem in the menu.
     *
     * @return         	the next non-null MenuItem in the menu
     */
    public MenuItem nextNotNull() {
        while (hasNext()) {
            Slot slot = nextSlot();
            if (slot == null)
                throw new NoSuchElementException(
                    NOTHING_MORE_NEXT_NOT_NULL +
                    "\nFix: Everything after slot " + position.getSlot() + " is empty/null."
                );

            MenuItem item = page.getItem(slot.getSlot());
            if (item != null) return item;
        }

        throw new NoSuchElementException(
            NOTHING_MORE_NEXT_NOT_NULL +
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
        if (slot != null) return page.get(slot);
        throw new NoSuchElementException(NOTHING_MORE_NEXT_OPTIONAL);
    }
}
