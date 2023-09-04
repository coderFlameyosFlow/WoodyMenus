package me.flame.menus.menu;

import me.flame.menus.modifiers.Modifier;

import java.util.EnumSet;
import java.util.Set;

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
}
