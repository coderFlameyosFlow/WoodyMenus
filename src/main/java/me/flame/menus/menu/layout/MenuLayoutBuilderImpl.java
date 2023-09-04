package me.flame.menus.menu.layout;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.Menu;
import me.flame.menus.menu.PaginatedMenu;
import me.flame.menus.modifiers.Modifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.Internal
@ApiStatus.Experimental
public class MenuLayoutBuilderImpl implements MenuLayoutBuilder {
    public final Map<Character, MenuItem> itemMap;
    private final List<MenuItem> items = new ArrayList<>(54);
    private static final int MAX_ROW_SIZE = 8;

    MenuLayoutBuilderImpl(Map<Character, MenuItem> itemMap) {
        this.itemMap = itemMap;
    }

    @Override
    public MenuLayoutBuilder row(String string) {
        int stringLength = items.size() + string.length();
        if (stringLength > MAX_ROW_SIZE) {
            throw new IllegalArgumentException("Too many strings (Temporary.. maybe?), length = "
                    + stringLength +
                    "\n String = " + string +
                    "\nFix: Reduce the amount of letters to 9 letters in total.");
        }

        int index = items.size();
        while (true) {
            if (items.get(53) != null) {
                throw new IllegalArgumentException(
                        "Attempted to add more than 54 (53 if considering 0 index) items (max inventory size)" +
                        "\nString = " + string +
                        "\nFix: Remove a few rows."
                );
            }
            try {
                char character = string.charAt(index - items.size());
                if (character == ' ') {
                    items.set(index, null);
                } else {
                    MenuItem item = itemMap.get(character);
                    if (item == null) {
                        throw new IllegalArgumentException("Unknown item: " + character +
                                "\nLikely a letter not in the bound map." +
                                "\nMap: " + itemMap +
                                "\nFix: Add or Change the letter.");
                    }
                    items.set(index, item);
                }
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        if (stringLength < MAX_ROW_SIZE) {
            for (; index != stringLength + MAX_ROW_SIZE; index++) {
                items.set(index, null);
            }
        }
        return this;
    }

    @Override
    public Menu createMenu(String title) {
        int size = items.size();
        Menu menu = new Menu(size / 9, title, EnumSet.noneOf(Modifier.class));

        for (int i = 0; i < size; i++) {
            menu.setItem(i, items.get(i));
        }
        return menu;
    }

    @Override
    public PaginatedMenu createPaginated(String title, int pageSize) {
        int size = items.size();
        PaginatedMenu menu = new PaginatedMenu(size / 9, pageSize, title);

        for (int i = 0; i < size; i++) {
            menu.setItem(i, items.get(i));
        }
        return menu;
    }
}
