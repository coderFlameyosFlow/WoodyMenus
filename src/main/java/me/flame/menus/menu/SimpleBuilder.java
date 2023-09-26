package me.flame.menus.menu;

public class SimpleBuilder extends BaseBuilder<Menu, SimpleBuilder> {
    SimpleBuilder() {
        super();
    }

    @Override
    public Menu create() {
        checkRows(rows);
        final Menu menu = type == MenuType.CHEST
                ? Menu.create(title, rows, modifiers)
                : Menu.create(title, type, modifiers);
        menuConsumer.accept(menu);
        return menu;
    }
}
