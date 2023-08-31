package me.flame.menus.menu.fillers;

import lombok.var;
import me.flame.menus.items.MenuItem;

import me.flame.menus.menu.BaseMenu;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class BorderFiller {
    private final BaseMenu<?> menu;

    BorderFiller(@NotNull BaseMenu<?> menu) {
        this.menu = menu;
    }

    public static BorderFiller from(@NotNull BaseMenu<?> menu) {
        return new BorderFiller(menu);
    }

    public void fillBorders(Material borderMaterial) {
        final int size = menu.getSize();
        final var itemStack = new ItemStack(borderMaterial);

        for (int i = 0; i < size; i++) {
            if (isBorderSlot(i, size)) menu.setItem(i, itemStack);
        }
    }

    public void fillBorders(@NotNull MenuItem borderMaterial) {
        final int size = menu.getSize();
        final var itemStack = borderMaterial.getItemStack();

        for (int i = 1; i < size; i++) {
            if (isBorderSlot(i, size)) menu.setItem(i, itemStack);
        }
    }

    public void fillBorders(ItemStack itemStack) {
        final int size = menu.getSize();

        for (int i = 0; i < size; i++) {
            if (isBorderSlot(i, size)) menu.setItem(i, itemStack);
        }
    }

    private static boolean isBorderSlot(int i, int size) {
        return (i < 9 || i >= size - 9) || (i % 9 == 0 || i % 9 == 8);
    }
}
