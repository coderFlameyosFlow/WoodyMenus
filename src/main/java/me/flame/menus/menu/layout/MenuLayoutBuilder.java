package me.flame.menus.menu.layout;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.Menu;
import me.flame.menus.menu.PaginatedMenu;

import java.util.Map;

public interface MenuLayoutBuilder {
    static MenuLayoutBuilder bind(Map<Character, MenuItem> itemMap) {
        return new MenuLayoutBuilderImpl(itemMap);
    }

    /**
     * Add a new row to the menu.
     * @param strings the strings to add (can be empty)
     * @return the object for chaining
     */
    MenuLayoutBuilder row(String string);
    
    Menu createMenu(String title);

    PaginatedMenu createPaginated(String title, int pageSize);
}
