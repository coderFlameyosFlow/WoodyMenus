package me.flame.menus.menu.layout;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.Menu;
import me.flame.menus.menu.PaginatedMenu;
import me.flame.menus.modifiers.Modifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ApiStatus.Experimental
@SuppressWarnings("unused")
public class MenuLayoutBuilder {
    @NotNull
    public final Map<Character, MenuItem> itemMap;

    private final MenuItem[] items = new MenuItem[54];

    private int size;

    private static final int MAX_ROW_SIZE = 9;

    MenuLayoutBuilder(@NotNull Map<Character, MenuItem> itemMap) {
        this.itemMap = itemMap;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static MenuLayoutBuilder bind(Map<Character, MenuItem> itemMap) {
        return new MenuLayoutBuilder(itemMap);
    }

    /**
     * Add a new row to the menu.
     * @param string the string to add (can be partially empty)
     * @return the object for chaining
     */
    public MenuLayoutBuilder row(@NotNull String string) {
        int stringLength = size + string.length();
        if (stringLength > 9) {
            throw new IllegalArgumentException(
                "Too many strings (Temporary.. maybe?), length = "
                + stringLength + "\n String = " + string +
                "\nFix: Reduce the amount of letters to 9 letters in total."
            );
        }

        for (int i = size; i < stringLength; i++) {
            addItem(i, size >= 54 ? ' ' : string.charAt(i));
        }

        if (stringLength < 9) {
            Arrays.fill(items, stringLength, stringLength + 9, null);
        }
        return this;
    }

    public MenuLayoutBuilder pattern(@NotNull String... string) {
        for (String s : string) row(s);
        return this;
    }

    private void addItem(int i, char character) {
        size++;

        if (character == ' ') return; // null by default
        MenuItem item = itemMap.get(character);
        if (item == null) {
            items[i] = null;
            throw new IllegalArgumentException(
                "Unknown item: " + character +
                "\nLikely a letter not in the bound map." +
                "\nMap: " + itemMap + "\nFix: Add or Change the letter."
            );
        }
        items[i] = item;
    }

    /**
     * Creates a menu with the given title and populates it with items.
     *
     * @param  title  the title of the menu
     * @return        the created menu
     */
    public Menu createMenu(String title) {
        if (size >= 54) {
            throw new IllegalArgumentException(
                "Too many rows. \nAmount of rows:" + size / 9 +
                "\nFix: Reduce the amount of letters to 9 letters in total."
            );
        }
        Menu menu = new Menu(Math.min(size / 9, 6), title, EnumSet.noneOf(Modifier.class));
        for (int i = 0; i < size; i++) {
            menu.setItem(i, items[i]);
        }
        return menu;
    }

    /**
     * Creates a paginated menu with the given title and populates it with items.
     *
     * @param title   the title of the paginated menu
     * @param pages   the number of pages
     * @return        the created paginated menu
     */
    public PaginatedMenu createPaginated(String title, int pages) {
        PaginatedMenu menu = PaginatedMenu.create(title, Math.min(size / 9, 6), pages);

        for (int i = 0; i < size; i++) {
            menu.setItem(i, items[i]);
        }
        return menu;
    }

    /**
     * Creates a paginated menu with the given title and populates it with items.
     *
     * @param  title  the title of the paginated menu
     * @return        the created paginated menu
     */
    public PaginatedMenu createPaginated(String title) {
        PaginatedMenu menu = PaginatedMenu.create(title, Math.min(size / 9, 6), 3);

        for (int i = 0; i < size; i++) {
            menu.setItem(i, items[i]);
        }
        return menu;
    }
}
