package me.flame.menus.menu;

public class SimpleBuilder extends BaseBuilder<Menu, SimpleBuilder> {
    SimpleBuilder() {
        super();
    }

    @Override
    public Menu create() {
        checkRows(rows);
        final Menu menu = type == MenuType.CHEST
                ? new Menu(rows, title, modifiers)
                : new Menu(type, title, modifiers);
        if (menuConsumer != null) menuConsumer.accept(menu);
        return menu;
    }
}
