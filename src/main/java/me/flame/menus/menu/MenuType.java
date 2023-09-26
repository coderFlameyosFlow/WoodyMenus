package me.flame.menus.menu;

import org.bukkit.event.inventory.InventoryType;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum MenuType implements Serializable {
    CHEST(InventoryType.CHEST, 9),
    FURNACE(InventoryType.FURNACE, 3),
    WORKBENCH(InventoryType.WORKBENCH, 9),
    HOPPER(InventoryType.HOPPER, 5),
    DISPENSER(InventoryType.DISPENSER, 8),
    BREWING(InventoryType.BREWING, 4);

    private final InventoryType type;
    private final int limit;

    MenuType(InventoryType type, int limit) {
        this.type = type;
        this.limit = limit;
    }
}
