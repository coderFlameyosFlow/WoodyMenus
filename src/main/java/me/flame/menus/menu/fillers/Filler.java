package me.flame.menus.menu.fillers;

import lombok.val;
import lombok.var;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.BaseMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Filler {
    private final BaseMenu<?> menu;

    Filler(@NotNull BaseMenu<?> menu) {
        this.menu = menu;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Filler from(@NotNull BaseMenu<?> menu) {
        return new Filler(menu);
    }

    public void fill(Material borderMaterial) {
        final int size = menu.getSize();

        for (int i = 0; i < size; i++) {
            val item = menu.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                menu.setItem(i, new ItemStack(borderMaterial));
            }
        }
    }

    public void fill(@NotNull MenuItem menuItem) {
        final int size = menu.getSize();

        for (int i = 0; i < size; i++) {
            val item = menu.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                menu.setItem(i, menuItem.getItemStack());
            }
        }
    }

    public void fill(ItemStack itemStack) {
        final int size = menu.getSize();

        for (int i = 0; i < size; i++) {
            val item = menu.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                menu.setItem(i, itemStack);
            }
        }
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
