package me.flame.menus.menu;

public class SimpleBuilder extends BaseBuilder<Menu, SimpleBuilder> {
    SimpleBuilder() {
        super();
    }

    public Menu create() {
        checkRows(rows);
        final Menu menu = new Menu(rows, title, modifiers);
        if (menuConsumer != null) menuConsumer.accept(menu);
        return menu;
    }
}
