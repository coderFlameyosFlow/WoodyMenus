package me.flame.menus.menu.animation;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.BaseMenu;

import java.util.List;

public class WaveWestAnimation extends Animation {
    public WaveWestAnimation(BaseMenu menu, List<MenuItem> items) {
        super(menu, items);
        this.init(menu.getRows());
    }

    @Override
    public void init(int rows) {
        add(0, getVertical(8, rows));
        add(0, getVertical(7, rows));
        add(1, getVertical(6, rows));
        add(1, getVertical(5, rows));
        add(2, getVertical(4, rows));
        add(2, getVertical(3, rows));
        add(3, getVertical(2, rows));
        add(3, getVertical(1, rows));
        add(4, getVertical(0, rows));
    }
}
