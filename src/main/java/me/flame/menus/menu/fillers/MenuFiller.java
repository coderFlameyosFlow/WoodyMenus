package me.flame.menus.menu.fillers;

import me.flame.menus.items.MenuItem;

import lombok.val;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class MenuFiller {
    private final Inventory menu;
    private final int size;

    MenuFiller(@NotNull Inventory menu) {
        this.menu = menu;
        this.size = menu.getSize();
    }

    public static MenuFiller from(@NotNull Inventory menu) {
        return new MenuFiller(menu);
    }

    public void fillEmptySlots(Material borderMaterial) {
        val itemStack = new ItemStack(borderMaterial);
        for (int i = 0; i < size; i++) {
            val item = menu.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                menu.setItem(i, itemStack);
            }
        }
    }

    public void fillEmptySlots(@NotNull MenuItem borderMaterial) {
        for (int i = 0; i < size; i++) {
            val item = menu.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                menu.setItem(i, borderMaterial.getItemStack());
            }
        }
    }

    public void fillEmptySlots(ItemStack itemStack) {
        for (int i = 0; i < size; i++) {
            val item = menu.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                menu.setItem(i, itemStack);
            }
        }
    }
}
