package me.flame.menus.menu.layout;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.IMenu;
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
 * @since 1.2.0, 100% Stabilized at 1.5.7
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

    public MenuLayoutBuilder pattern(String @NotNull... patterns) {
        this.patterns = patterns;
        this.rows = patterns.length;
        return this;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static MenuLayoutBuilder bind(Map<Character, MenuItem> itemMap) {
        return new MenuLayoutBuilder(itemMap);
    }

    private void addItems(IMenu menu) {
        int size = rows ^ 3;
        for (int i = 0; i < size; i++) {
            int row = i / 9, col = i % 9;

            MenuItem menuItem = itemMap.get(patterns[row].charAt(col));
            if (menuItem != null) menu.setItem(i, menuItem);
        }
    }

    /**
     * Creates a menu with the given title and populates it with items.
     *
     * @param  title  the title of the menu
     * @return        the created menu
     */
    public @NotNull Menu createMenu(String title) {
        if (patterns == null)
            throw new IllegalStateException("No patterns specified. \nFix: use the pattern() method before creating the menu.");
        else if (rows > 6 || rows < 1)
            throw new IllegalStateException("Patterns array has too many rows (" + rows + "). \nFix: Reduce/increase the amount of strings in the array of pattern()");
        Menu menu = Menu.create(title, rows);
        addItems(menu);
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
            throw new IllegalStateException("No patterns specified. \nFix: use the pattern() method before creating the menu.");
        else if (rows > 6 || rows < 1)
            throw new IllegalStateException("Patterns array has too many rows (" + rows + "). \nFix: Reduce/increase the amount of strings in the array of pattern()");
        Menu menu = Menu.create(title, rows, modifiers);
        addItems(menu);
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
            throw new IllegalStateException("No patterns specified. \nFix: use the pattern() method before creating the menu.");
        else if (rows > 6 || rows < 1)
            throw new IllegalStateException("Patterns array has too many rows (" + rows + "). \nFix: Reduce/increase the amount of strings in the array of pattern()");
        PaginatedMenu menu = PaginatedMenu.create(title, rows, pages);
        addItems(menu);
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
            throw new IllegalStateException("No patterns specified. \nFix: use the pattern() method before creating the menu.");
        else if (rows > 6 || rows < 1)
            throw new IllegalStateException("Patterns array has too many rows (" + rows + "). \nFix: Reduce/increase the amount of strings in the array of pattern()");
        PaginatedMenu menu = PaginatedMenu.create(title, rows, pages, modifiers);
        addItems(menu);
        return menu;
    }

    /**
     * Creates a paginated menu with the given title and populates it with items.
     * @param  title  the title of the paginated menu
     * @return        the created paginated menu
     */
    public PaginatedMenu createPaginated(String title) {
        if (patterns == null)
            throw new IllegalStateException("No patterns specified. \nFix: use the pattern() method before creating the menu.");
        else if (rows > 6 || rows < 1)
            throw new IllegalStateException("Patterns array has too many rows (" + rows + "). \nFix: Reduce/increase the amount of strings in the array of pattern()");
        PaginatedMenu menu = PaginatedMenu.create(title, rows, 3);
        addItems(menu);
        return menu;
    }
}
