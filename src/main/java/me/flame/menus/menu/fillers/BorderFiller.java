package me.flame.menus.menu.fillers;

import lombok.var;
import me.flame.menus.items.MenuItem;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class BorderFiller {
    private final Inventory menu;
    private final int size;

    BorderFiller(@NotNull Inventory menu) {
        this.menu = menu;
        this.size = menu.getSize();
    }

    public static BorderFiller from(@NotNull Inventory menu) {
        return new BorderFiller(menu);
    }

    public void fillBorders(Material borderMaterial) {
        var itemStack = new ItemStack(borderMaterial);
        for (int i = 0; i < size; i++) {
            if ((i < 9 || i >= size - 9) || (i % 9 == 0 || i % 9 == 8))
                menu.setItem(i, itemStack);
        }
    }

    public void fillBorders(@NotNull MenuItem borderMaterial) {
        var itemStack = borderMaterial.getItemStack();
        for (int i = 0; i < size; i++) {
            if ((i < 9 || i >= size - 9) ||
                (i % 9 == 0 || i % 9 == 8)) menu.setItem(i, itemStack);
        }
    }

    public void fillBorders(ItemStack itemStack) {
        for (int i = 0; i < size; i++) {
            if ((i < 9 || i >= size - 9) ||
                (i % 9 == 0 || i % 9 == 8)) menu.setItem(i, itemStack);
        }
    }
}
