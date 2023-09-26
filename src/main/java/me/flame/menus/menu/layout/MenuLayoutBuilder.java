package me.flame.menus.menu.layout;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.Menu;
import me.flame.menus.menu.PaginatedMenu;

import me.flame.menus.modifiers.Modifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Map;

/**
 * Complex and Fast builder to build (paginated) menus from a list of strings or a so-called pattern.
 * <p>
 * Example usage:
 * <pre>{@code
 *     Map<Character, MenuItem> menuItems = ImmutableMap.of(
 *          'X', ItemBuilder.of(Material.STONE).buildItem();
 *          'K', ItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).buildItem();
 *     );
 *     Menu menu = MenuLayoutBuilder.bind(menuItems)
 *                  .pattern(
 *                      "KKKKKKKKK"
 *                      "KXX   XXK"
 *                      "KX     XK"
 *                      "KX     XK"
 *                      "KXX   XXK"
 *                      "KKKKKKKKK"
 *                  )
 *                  .createMenu("Awesome");
 * }</pre>
 * @author FlameyosFlow
 * @since 1.2.0. Stabilized at 1.5.0
 */
@SuppressWarnings("unused")
public final class MenuLayoutBuilder {
    @NotNull
    private final Map<Character, MenuItem> itemMap;

    @NotNull
    private String[] patterns;

    private int rows;

    MenuLayoutBuilder(@NotNull Map<Character, MenuItem> itemMap) {
        this.itemMap = itemMap;
        this.patterns = null;
        this.rows = 0;
    }


    public MenuLayoutBuilder pattern(String @NotNull ... patterns) {
        this.patterns = patterns;
        this.rows = patterns.length;
        return this;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static MenuLayoutBuilder bind(Map<Character, MenuItem> itemMap) {
        return new MenuLayoutBuilder(itemMap);
    }

    /**
     * Creates a menu with the given title and populates it with items.
     *
     * @param  title  the title of the menu
     * @return        the created menu
     */
    public Menu createMenu(String title) {
        if (patterns == null)
            throw new IllegalStateException("No patterns specified. Use the pattern() method before creating the menu.");
        Menu menu = Menu.create(title, rows);

        int size = ((rows * 9) + 9) - 10;
        for (int i = 0; i < size; i++) {
            int row = i / 9;
            int col = i % 9;

            char item = patterns[row].charAt(col);
            MenuItem menuItem = itemMap.get(item);

            if (menuItem != null) menu.setItem(i, menuItem);
        }

        return menu;
    }
    /**
     * Creates a menu with the given title and populates it with items.
     *
     * @param  title  the title of the menu
     * @return        the created menu
     */
    public Menu createMenu(String title, EnumSet<Modifier> modifiers) {
        if (patterns == null)
            throw new IllegalStateException("No patterns specified. Use the pattern() method before creating the menu.");
        Menu menu = Menu.create(title, rows, modifiers);

        int size = ((rows * 9) + 9) - 10;
        for (int i = 0; i < size; i++) {
            int row = i / 9;
            int col = i % 9;

            char item = patterns[row].charAt(col);
            MenuItem menuItem = itemMap.get(item);

            if (menuItem != null) menu.setItem(i, menuItem);
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
        if (patterns == null)
            throw new IllegalStateException("No patterns specified. Use the pattern() method before creating the menu.");
        PaginatedMenu menu = PaginatedMenu.create(title, rows, pages);

        int size = ((rows * 9) + 9) - 10;
        for (int i = 0; i < size; i++) {
            int row = i / 9;
            int col = i % 9;

            char item = patterns[row].charAt(col);
            MenuItem menuItem = itemMap.get(item);

            if (menuItem != null) menu.setItem(i, menuItem);
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
    public PaginatedMenu createPaginated(String title, int pages, EnumSet<Modifier> modifiers) {
        if (patterns == null)
            throw new IllegalStateException("No patterns specified. Use the pattern() method before creating the menu.");
        PaginatedMenu menu = PaginatedMenu.create(title, rows, pages, modifiers);

        int size = ((rows * 9) + 9) - 10;
        for (int i = 0; i < size; i++) {
            int row = i / 9;
            int col = i % 9;

            char item = patterns[row].charAt(col);
            MenuItem menuItem = itemMap.get(item);

            if (menuItem != null) menu.setItem(i, menuItem);
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
        if (patterns == null)
            throw new IllegalStateException("No patterns specified. Use the pattern() method before creating the menu.");
        PaginatedMenu menu = PaginatedMenu.create(title, rows, 3);

        int size = ((rows * 9) + 9) - 10;
        for (int i = 0; i < size; i++) {
            int row = i / 9;
            int col = i % 9;

            char item = patterns[row].charAt(col);
            MenuItem menuItem = itemMap.get(item);

            if (menuItem != null) menu.setItem(i, menuItem);
        }

        return menu;
    }
}
