package me.flame.menus.menu;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.bukkit.event.inventory.InventoryType;

import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum MenuType {
    CHEST(InventoryType.CHEST, 9),
    FURNACE(InventoryType.FURNACE, 3),
    WORKBENCH(InventoryType.WORKBENCH, 9),
    HOPPER(InventoryType.HOPPER, 5),
    DISPENSER(InventoryType.DISPENSER, 8),
    BREWING(InventoryType.BREWING, 4);

    private final InventoryType type;
    private final int limit;
}
