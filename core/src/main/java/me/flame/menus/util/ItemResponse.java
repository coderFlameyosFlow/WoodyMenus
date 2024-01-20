package me.flame.menus.util;

import me.flame.menus.events.ClickActionEvent;
import me.flame.menus.menu.ActionResponse;

@FunctionalInterface
public interface ItemResponse {
    ActionResponse apply(int slot, ClickActionEvent event);
}
