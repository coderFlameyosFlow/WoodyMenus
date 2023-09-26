package me.flame.menus.menu.fillers;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.BaseMenu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class Filler implements MenuFiller {
    @NotNull
    private final BaseMenu menu;

    private Filler(@NotNull BaseMenu menu) {
        this.menu = menu;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Filler from(@NotNull BaseMenu menu) {
        return new Filler(menu);
    }

    public void fill(Material borderMaterial) {
        final int size = menu.getSize();

        MenuItem stack = MenuItem.of(new ItemStack(borderMaterial));
        for (int i = 0; i < size; i++) {
            MenuItem item = menu.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                menu.setItem(i, stack);
            }
        }
    }

    public void fill(@NotNull MenuItem menuItem) {
        final int size = menu.getSize();

        for (int i = 0; i < size; i++) {
            MenuItem item = menu.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                menu.setItem(i, menuItem);
            }
        }
    }

    public void fill(ItemStack itemS) {
        final int size = menu.getSize();

        MenuItem stack = MenuItem.of(itemS);
        for (int i = 0; i < size; i++) {
            MenuItem item = menu.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                menu.setItem(i, stack);
            }
        }
    }

    public void fillBorders(Material borderMaterial) {
        final int size = menu.getSize();

        final MenuItem stack = MenuItem.of(new ItemStack(borderMaterial));
        for (int i = 0; i < size; i++) {
            if (MenuFiller.isBorderSlot(i, size)) menu.setItem(i, stack);
        }
    }

    public void fillBorders(@NotNull MenuItem borderMaterial) {
        final int size = menu.getSize();

        for (int i = 0; i < size; i++) {
            if (MenuFiller.isBorderSlot(i, size)) menu.setItem(i, borderMaterial);
        }
    }

    public void fillBorders(ItemStack itemStack) {
        final int size = menu.getSize();

        final MenuItem stack = MenuItem.of(itemStack);
        for (int i = 0; i < size; i++) {
            if (MenuFiller.isBorderSlot(i, size)) menu.setItem(i, stack);
        }
    }

    public void fillRow(int row, Material borderMaterial) {
        final int size = menu.getSize();

        MenuItem itemStack = MenuItem.of(new ItemStack(borderMaterial));
        for (int i = 0; i < size; i++) {
            if (i / 9 == row && !menu.hasItem(i)) menu.setItem(i, itemStack);
        }
    }

    public void fillRow(int row, ItemStack borderMaterial) {
        final int size = menu.getSize();

        MenuItem itemStack = MenuItem.of(borderMaterial);
        for (int i = 0; i < size; i++) {
            if (i / 9 == row && !menu.hasItem(i)) menu.setItem(i, itemStack);
        }
    }

    public void fillRow(int row, MenuItem itemStack) {
        final int size = menu.getSize();

        for (int i = 0; i < size; i++) {
            if ((i / 9) == row && !menu.hasItem(i)) menu.setItem(i, itemStack);
        }
    }

    public void fillArea(int length, int width, Material borderMaterial) {
        final int size = menu.getSize();

        MenuItem itemStack = MenuItem.of(new ItemStack(borderMaterial));
        for (int i = 0; i < size; i++) {
            if (MenuFiller.isInArea(i, length, width)) menu.setItem(i, itemStack);
        }
    }

    public void fillArea(int length, int width, ItemStack borderMaterial) {
        final int size = menu.getSize();

        MenuItem itemStack = MenuItem.of(borderMaterial);
        for (int i = 0; i < size; i++) {
            if (MenuFiller.isInArea(i, length, width)) menu.setItem(i, itemStack);
        }
    }

    public void fillArea(int length, int width, MenuItem itemStack) {
        final int size = menu.getSize();

        for (int i = 0; i < size; i++) {
            if (MenuFiller.isInArea(i, length, width)) menu.setItem(i, itemStack);
        }
    }

    // isInArea = simple geometry to check if a slot is in an area of L*W
}
