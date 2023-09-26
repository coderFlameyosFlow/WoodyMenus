package me.flame.menus.menu;

import me.flame.menus.items.MenuItem;
import me.flame.menus.modifiers.Modifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

/**
 * Most commonly used normal Menu extending BaseMenu.
 */
@SuppressWarnings("unused")
public final class Menu extends BaseMenu implements Cloneable {
    private Menu(int rows, String title, EnumSet<Modifier> modifiers) {
        super(rows, title, modifiers);
    }

    private Menu(MenuType type, String title, EnumSet<Modifier> modifiers) {
        super(type, title, modifiers);
    }

    private Menu(int rows, String title) {
        super(rows, title, EnumSet.noneOf(Modifier.class));
    }

    private Menu(MenuType type, String title) {
        super(type, title, EnumSet.noneOf(Modifier.class));
    }

    private Menu(MenuType type, String title, EnumSet<Modifier> modifiers, boolean colorize) {
        super(type, title, modifiers, colorize);
    }

    @NotNull
    @Contract("_, _ -> new")
    public static Menu create(String title, int rows) {
        return new Menu(rows, title);
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static Menu create(String title, int rows, EnumSet<Modifier> modifiers) {
        return new Menu(rows, title, modifiers);
    }

    @NotNull
    @Contract("_, _ -> new")
    public static Menu create(String title, MenuType type) {
        return new Menu(type, title);
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static Menu create(String title, MenuType type, EnumSet<Modifier> modifiers) {
        return new Menu(type, title, modifiers);
    }

    public static Menu create(MenuData menuData) {
        MenuType type = menuData.getType();
        Menu menu = type != MenuType.CHEST
                ? new Menu(type, menuData.getTitle(), menuData.getModifiers())
                : new Menu(menuData.getRows(), menuData.getTitle(), menuData.getModifiers());
        menu.setContents(menuData.getItems().toArray(new MenuItem[0]));
        return menu;
    }

    /**
     * Generate a paginated menu; convert to a {@link PaginatedMenu} from a {@link Menu}
     *
     * @return         	The generated PaginatedMenu object
     *                  Comes with 3 pages by default.
     */
    @NotNull
    public PaginatedMenu pagination() {
        PaginatedMenu menu = PaginatedMenu.create(title, rows, 3, modifiers);
        menu.setContents(itemMap);
        return menu;
    }

    /**
     * Generate a paginated menu; convert to a {@link PaginatedMenu} from a {@link Menu}
     * @param pages the amount of pages to generate with the paged menu
     * @return         	The generated PaginatedMenu object
     */
    @NotNull
    public PaginatedMenu pagination(int pages) {
        PaginatedMenu menu = PaginatedMenu.create(title, rows, pages, modifiers);
        menu.setContents(itemMap);
        return menu;
    }

    @Override
    public Menu clone() {
        try {
            return (Menu) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
