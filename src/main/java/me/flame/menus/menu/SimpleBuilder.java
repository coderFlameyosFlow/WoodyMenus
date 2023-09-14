package me.flame.menus.menu;

//changed

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
        menuConsumer.accept(menu);
        return menu;
    }
}
