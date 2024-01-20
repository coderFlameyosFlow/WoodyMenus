package me.flame.menus.menu.animation.variants;

import me.flame.menus.menu.IMenu;
import me.flame.menus.menu.Menu;
import me.flame.menus.menu.animation.Animation;
import me.flame.menus.menu.animation.Frame;

/**
 * @since 2.0.0
 */
public class NormalAnimation extends Animation {
    public NormalAnimation(int delay, Frame[] frames, Menu menu) {
        super(delay, frames, menu);
    }

    @Override
    public Frame onFinish() {
        stop();
        return null;
    }
}
