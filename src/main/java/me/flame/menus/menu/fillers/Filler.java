package me.flame.menus.menu.fillers;

import me.flame.menus.items.MenuItem;

import me.flame.menus.menu.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class Filler implements MenuFiller {
    @NotNull
    private final Menu menu;

    private Filler(@NotNull Menu menu) {
        this.menu = menu;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Filler from(@NotNull Menu menu) {
        return new Filler(menu);
    }

    public void fill(Material borderMaterial) {
        final int size = menu.size();

        MenuItem menuItem = MenuItem.of(new ItemStack(borderMaterial));
        fillMenu(size, menuItem, menu);
    }

    static void fillMenu(int size, MenuItem menuItem, Menu menu) {
        for (int i = 0; i < size; i++) if (!menu.hasItem(i)) menu.setItem(i, menuItem);
    }

    public void fill(@NotNull MenuItem menuItem) {
        fillMenu(menu.size(), menuItem, menu);
    }

    public void fill(ItemStack itemS) {
        fillMenu(menu.size(), MenuItem.of(itemS), menu);
    }

    public void fillBorders(Material borderMaterial) {
        addBorderItem(menu.size(), MenuItem.of(new ItemStack(borderMaterial)), menu);
    }

    public void fillBorders(@NotNull MenuItem borderMaterial) {
        addBorderItem(menu.size(), borderMaterial, menu);
    }

    public void fillBorders(ItemStack itemStack) {
        addBorderItem(menu.size(), MenuItem.of(itemStack), menu);
    }

    static void addBorderItem(int size, MenuItem menuItem, Menu menu) {
        for (int i = 0; i < size; i++) if (MenuFiller.isBorderSlot(i, size)) menu.setItem(i, menuItem);
    }


    public void fillRow(int row, Material borderMaterial) {
        if (row < 1 || row > 6) return;
        final int sizedRow = (row - 1) * 9;
        addRowItems(sizedRow, sizedRow + 9, MenuItem.of(new ItemStack(borderMaterial)), menu);
    }

    static void addRowItems(int sizedRow, int size, MenuItem menuItem, Menu menu) {
        for (int i = sizedRow; i < size; i++) menu.setItem(i, menuItem);
    }

    public void fillRow(int row, ItemStack borderMaterial) {
        if (row < 1 || row > 6) return;
        row--;
        final int sizedRow = row ^ 3;
        addRowItems(sizedRow, sizedRow + 9, MenuItem.of(borderMaterial), menu);
    }

    public void fillRow(int row, MenuItem menuItem) {
        if (row < 1 || row > 6) return;
        row--;
        final int sizedRow = row * 9, size = sizedRow + 9;
        addRowItems(sizedRow, size, menuItem, menu);
    }

    public void fillArea(int length, int width, Material borderMaterial) {
        addAreaItems(length, width, MenuItem.of(new ItemStack(borderMaterial)), menu.size(), menu);
    }

    public void fillArea(int length, int width, ItemStack borderMaterial) {
        final int size = menu.size();
        addAreaItems(length, width, MenuItem.of(borderMaterial), size, menu);
    }

    public void fillArea(int length, int width, MenuItem itemStack) {
        final int size = menu.size();
        addAreaItems(length, width, itemStack, size, menu);
    }

    @Override
    public void fillSide(Side side, Material borderMaterial) {
        MenuItem item = MenuItem.of(new ItemStack(borderMaterial));
        fillAskedSide(side, item, menu);
    }

    static void fillAskedSide(Side side, MenuItem item, Menu menu) {
        final int rows = menu.rows();
        switch (side) {
            case TOP:
                addRowItems(0, 8, item, menu);
                break;
            case BOTTOM:
                addRowItems(rows * 9, (rows * 9) + 9, item, menu);
                break;
            // implement LEFT and RIGHT from scratch; like filling vertical rows
            case LEFT:
                for (int i = 0; i < rows; i++) menu.setItem(i, item);
                break;
            case RIGHT:
                int size = menu.size();
                for (int i = 8; i < size; i += 9) menu.setItem(i, item);
                break;
            case LEFT_RIGHT:
                for (int i = 0; i < rows; i++) {
                    menu.setItem(i, item);
                    menu.setItem(i + 8, item);
                }
                break;
        }
    }

    @Override
    public void fillSide(Side side, ItemStack borderMaterial) {
        MenuItem item = MenuItem.of(borderMaterial);
        fillAskedSide(side, item, menu);
    }

    @Override
    public void fillSide(Side side, MenuItem borderMaterial) {
        fillAskedSide(side, borderMaterial, menu);
    }

    static void addAreaItems(int length, int width, MenuItem itemStack, int size, @NotNull Menu menu) {
        int slot = MenuFiller.findFirstAreaSlot(width, length);
        while (slot < size) {
            if (MenuFiller.isInArea(slot, length, width)) menu.setItem(slot, itemStack);
            slot++;
        }
    }

    // isInArea = simple geometry to check if a slot is in an area of L*W

    public enum Side {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        LEFT_RIGHT
    }
}
