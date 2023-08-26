package me.flame.menus.menu;

public class SimpleBuilder extends BaseBuilder<Menu, SimpleBuilder> {
    SimpleBuilder() {
        super();
    }

    public Menu create() {
        checkRows(rows);
        return new Menu(rows, title, modifiers);
    }
}
