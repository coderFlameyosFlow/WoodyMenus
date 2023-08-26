package me.flame.menus.menu;

import me.flame.menus.modifiers.Modifier;

import java.util.EnumSet;
import java.util.Set;

public final class Menu extends BaseMenu<Menu> {
    Menu(int rows, String title, EnumSet<Modifier> modifiers, boolean colorize) {
        super(rows, title, modifiers, colorize);
    }

    Menu(int rows, String title, EnumSet<Modifier> modifiers) {
        super(rows, title, modifiers);
    }
}
