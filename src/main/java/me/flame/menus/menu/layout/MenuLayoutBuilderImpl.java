package me.flame.menus.menu.layout;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.Menu;
import me.flame.menus.menu.MenuFactory;
import me.flame.menus.menu.Menus;
import me.flame.menus.menu.PaginatedMenu;
import me.flame.menus.modifiers.Modifier;

import java.util.*;

public class MenuLayoutBuilderImpl implements MenuLayoutBuilder {
    public final Map<Character, MenuItem> itemMap;
    private final List<MenuItem> items = new ArrayList<>(54);
    private static final int MAX_ROW_SIZE = 8;
    private static final MenuFactory FACTORY = Menus.getFactory();

    public MenuLayoutBuilderImpl(Map<Character, MenuItem> itemMap) {
        this.itemMap = itemMap;
    }

    @Override
    public MenuLayoutBuilder row(String string) {
        int stringsLength = string.length();
        if (items.size() == 54) {
            throw new IllegalArgumentException("Attempted to add more than 54 items (max inventory size)");
        }
        if (stringsLength > MAX_ROW_SIZE) {
            throw new IllegalArgumentException("Too many strings (Temporary.. maybe?), length = "
                    + stringsLength +
                    "\n String = " + string);
        }

        char[] chars = string.toCharArray();
        int index = 0;
        for (; index != stringsLength; index++) {
            if (items.size() == 54) {
                throw new IllegalArgumentException("Attempted to add more than 54 items (max inventory size)");
            }
            char character = chars[index];
            if (character == ' ') {
                items.add(null);
                continue;
            }
            MenuItem item = itemMap.get(character);
            if (item == null) {
                throw new IllegalArgumentException("Unknown item: " + character +
                        "\nLikely a letter not in the bound map." +
                        "\nMap: " + itemMap);
            }
            items.add(item);
        }

        if (stringsLength < MAX_ROW_SIZE)
            for (; index != MAX_ROW_SIZE; index++) items.add(null);
        return this;
    }

    @Override
    public Menu createMenu(String title) {
        int i = 0, size = items.size();
        Menu menu = FACTORY.createMenu(title, size / 9, EnumSet.noneOf(Modifier.class));

        for (; i < size; i++)
            menu.setItem(i, items.get(i));
        return menu;
    }

    @Override
    public PaginatedMenu createPaginated(String title, int pageSize) {
        int i = 0, size = items.size();
        PaginatedMenu menu = FACTORY.createPaginated(title, size / 9, pageSize);

        for (; i < size; i++)
            menu.setItem(i, items.get(i));
        return menu;
    }
}
