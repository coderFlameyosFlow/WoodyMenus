package me.flame.menus.menu;

import lombok.NonNull;
import me.flame.menus.adventure.TextHolder;
import me.flame.menus.items.MenuItem;
import me.flame.menus.modifiers.Modifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

/**
 * Universal menu builder for menus (Menu, PaginatedMenu).
 * @since 2.0.0
 * @author FlameyosFlow
 */
@SuppressWarnings("unused")
public class MenuBuilder {
    protected TextHolder title;

    @NotNull
    protected MenuType type = MenuType.CHEST;

    @NotNull
    protected final EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);

    protected int rows = 1, pages = 2, nextItemSlot = -1, previousItemSlot = -1;

    private MenuItem nextItem, previousItem;

    /**
     * Sets the title of the menu.
     *
     * @param  title  the title to be set
     * @return        the builder for chaining
     */
    public MenuBuilder title(@NonNull String title) {
        this.title = TextHolder.of(title);
        return this;
    }

    /**
     * Sets the title of the menu.
     *
     * @param  title  the title to be set
     * @return        the builder for chaining
     */
    public MenuBuilder title(@NonNull TextHolder title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the amount of rows of the menu.
     *
     * @param  rows  the amount of rows to be set
     * @return        the builder for chaining
     */
    public MenuBuilder rows(int rows) {
        checkRows(rows);
        this.rows = rows;
        return this;
    }

    /**
     * Sets the type of the menu.
     * @param type the type, ex. {@link MenuType#HOPPER}, {@link MenuType#FURNACE}, etc.
     * @apiNote By default, it is {@link MenuType#CHEST}.
     * @return the builder for chaining
     */
    public MenuBuilder type(MenuType type) {
        this.type = type;
        return this;
    }

    /**
     * Adds a modifier to the list of modifiers.
     *
     * @param  modifier  the modifier to be added
     * @return           the builder for chaining
     */
    public MenuBuilder addModifier(@NonNull Modifier modifier) {
        modifiers.add(modifier);
        return this;
    }

    /**
     * Remove a modifier from the list of modifiers.
     *
     * @param  modifier  the modifier to be removed
     * @return           the builder for chaining
     */
    public MenuBuilder removeModifier(@NonNull Modifier modifier) {
        modifiers.remove(modifier);
        return this;
    }

    /**
     * Add all the modifiers of {@link Modifier} to the list of modifiers.
     * @return the builder for chaining
     */
    public MenuBuilder addAllModifiers() {
        modifiers.addAll(Modifier.ALL);
        return this;
    }

    /**
     * Set the number of pages for the paginated builder.
     *
     * @param  pages  the number of pages to set
     * @return        the builder for chaining
     */
    @NotNull
    public MenuBuilder pages(final int pages) {
        this.pages = pages;
        return this;
    }

    public void nextPageItem(int nextItemSlot, MenuItem nextItem) {
        this.nextItemSlot = nextItemSlot;
        this.nextItem = nextItem;
    }

    public void previousPageItem(int previousItemSlot, MenuItem previousItem) {
        this.previousItemSlot = previousItemSlot;
        this.previousItem = previousItem;
    }

    @NotNull
    @Contract(" -> new")
    public Menu normal() {
        checkRequirements(rows, title);
        return type == MenuType.CHEST ? Menu.create(title, rows, modifiers) : Menu.create(title, type, modifiers);
    }

    @NotNull
    @Contract(" -> new")
    public PaginatedMenu pagination() {
        checkRequirements(rows, title);
        checkPaginatedRequirements(pages, nextItemSlot, previousItemSlot, nextItem, previousItem);
        PaginatedMenu menu = type == MenuType.CHEST
                ? PaginatedMenu.create(title, rows, pages, modifiers)
                : PaginatedMenu.create(title, type, pages, modifiers);

        @NotNull List<ItemData> pages = menu.pages;
        if (nextItemSlot != -1 && nextItem != null) menu.setPageItem(nextItemSlot, nextItem);

        if (previousItemSlot != -1 && previousItem != null) menu.setPageItem(previousItemSlot, previousItem);
        return menu;
    }

    private static void checkRows(int rows) {
        if (rows <= 0 || rows > 6) {
            throw new IllegalArgumentException(
                    "Rows must be more than 1 or 6 and less" +
                    "\nRows: " + rows +
                    "\nFix: Rows must be 1-6"
            );
        }
    }

    private static void checkRequirements(int rows, TextHolder title) {
        checkRows(rows);

        if (title == null) {
            throw new IllegalArgumentException(
                    "Title must not be null or empty" +
                    "\nTitle equals null" +
                    "\nFix: Title must not be null or empty"
            );
        }
    }

    private static void checkPaginatedRequirements(int pages,
                                                   int next,
                                                   int previous,
                                                   MenuItem nextItem,
                                                   MenuItem previousItem) {
        if (pages <= 1) {
            throw new IllegalArgumentException(
                    "Pages must be more than 1" +
                            "Pages: " + pages +
                            "\nFix: Pages must be more than 1"
            );
        }

        if (next == -1 || previous == -1 || nextItem == null || previousItem == null) {
            throw new IllegalArgumentException(
                    "Next and previous item slots and items must not be null/-1" +
                            "\nNext equals null: " + (nextItem == null) +
                            "\nPrevious equals null: " + (previousItem == null) +
                            "\nNext Item Slot: " + next +
                            "\nPrevious Item Slot: " + previous +
                            "\nFix: The items and item slots must be set."
            );
        }
    }
}
