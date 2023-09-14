package me.flame.menus.menu;

import me.flame.menus.modifiers.Modifier;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

@SuppressWarnings("unused")
public final class Menu extends BaseMenu<Menu> {
    public Menu(int rows, String title, EnumSet<Modifier> modifiers, boolean colorize) {
        super(rows, title, modifiers, colorize);
    }

    public Menu(int rows, String title, EnumSet<Modifier> modifiers) {
        super(rows, title, modifiers);
    }

    public Menu(MenuType type, String title, EnumSet<Modifier> modifiers) {
        super(type, title, modifiers);
    }

    public Menu(int rows, String title) {
        super(rows, title, EnumSet.noneOf(Modifier.class));
    }

    public Menu(MenuType type, String title) {
        super(type, title, EnumSet.noneOf(Modifier.class));
    }

    public Menu(MenuType type, String title, EnumSet<Modifier> modifiers, boolean colorize) {
        super(type, title, modifiers, colorize);
    }

    /**
     * Generate a paginated menu; convert to a {@link PaginatedMenu} from a {@link Menu}
     *
     * @return         	The generated PaginatedMenu object
     *                  Comes with 3 pages by default.
     */
    @NotNull
    public PaginatedMenu pagination() {
        PaginatedMenu menu = PaginatedMenu.create(rows, 3, title, modifiers);
        for (int i = 0; i < size; i++) {
            menu.setItem(i, itemMap.get(i));
        }
        return menu;
    }

    /**
     * Generate a paginated menu; convert to a {@link PaginatedMenu} from a {@link Menu}
     * @param pages the amount of pages to generate with the paged menu
     * @return         	The generated PaginatedMenu object
     */
    @NotNull
    public PaginatedMenu pagination(int pages) {
        PaginatedMenu menu = PaginatedMenu.create(rows, pages, title, modifiers);
        for (int i = 0; i < size; i++) {
            menu.setItem(i, itemMap.get(i));
        }
        return menu;
    }
}
