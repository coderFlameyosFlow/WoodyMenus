package me.flame.menus.menu;

import lombok.val;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.fillers.MenuFiller;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class PageDecoration implements Decorator {
    @NotNull
    private final PaginatedMenu menu;
    
    private PageDecoration(@NotNull PaginatedMenu menu) {
        this.menu = menu;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static PageDecoration create(PaginatedMenu menu) {
        return new PageDecoration(menu);
    }

    public void fillPageBorders(ItemStack itemStack) {
        int size = menu.size;
        for (Page page : menu.pageList) {
            for (int i = 0; i < size; i++) {
                if (MenuFiller.isBorderSlot(i, size)) page.setItem(i, itemStack);
            }
        }
    }

    public void fillPageBorders(Material borderMaterial) {
        ItemStack itemStack = new ItemStack(borderMaterial);
        int size = menu.size;
        for (Page page : menu.pageList) {
            for (int i = 0; i < size; i++) {
                if (MenuFiller.isBorderSlot(i, size)) page.setItem(i, itemStack);
            }
        }
    }

    public void fillPageBorders(MenuItem item) {
        int size = menu.size;
        for (Page page : menu.pageList) {
            for (int i = 0; i < size; i++) {
                if (MenuFiller.isBorderSlot(i, size)) page.setItem(i, item);
            }
        }
    }

    public void fillPages(Material borderMaterial) {
        ItemStack item = new ItemStack(borderMaterial);
        int size = menu.size;
        for (Page page : menu.pageList) {
            for (int i = 0; i < size; i++) {
                val menuItem = page.getItem(i);
                if (menuItem == null || menuItem.getType() == Material.AIR) {
                    page.setItem(i, item);
                }
            }
        }
    }

    public void fillPages(@NotNull MenuItem menuItem) {
        int size = menu.size;
        for (Page page : menu.pageList) {
            for (int i = 0; i < size; i++) {
                val item = page.getItem(i);
                if (item == null || item.getType() == Material.AIR) {
                    page.setItem(i, menuItem);
                }
            }
        }
    }

    public void fillPages(ItemStack itemStack) {
        int size = menu.size;
        for (Page page : menu.pageList) {
            for (int i = 0; i < size; i++) {
                val item = page.getItem(i);
                if (item == null || item.getType() == Material.AIR) {
                    page.setItem(i, itemStack);
                }
            }
        }
    }

    public void fillRow(int row, Material borderMaterial) {
        final int size = menu.getSize();

        MenuItem itemStack = MenuItem.of(new ItemStack(borderMaterial));
        for (Page page : menu.pageList) {
            for (int i = 0; i < size; i++) {
                if (i / 9 == row) page.setItem(i, itemStack);
            }
        }
    }

    public void fillRow(int row, ItemStack borderMaterial) {
        final int size = menu.getSize();

        MenuItem itemStack = MenuItem.of(borderMaterial);
        for (Page page : menu.pageList) {
            for (int i = 0; i < size; i++) {
                if (i / 9 == row) page.setItem(i, itemStack);
            }
        }
    }

    public void fillRow(int row, MenuItem itemStack) {
        final int size = menu.getSize();

        for (Page page : menu.pageList) {
            for (int i = 0; i < size; i++) {
                if (i / 9 == row) page.setItem(i, itemStack);
            }
        }
    }

    public void fillArea(int length, int width, Material borderMaterial) {
        final int size = menu.getSize();

        MenuItem itemStack = MenuItem.of(new ItemStack(borderMaterial));
        for (Page page : menu.pageList) {
            for (int i = 0; i < size; i++) {
                if (isInArea(i, length, width)) page.setItem(i, itemStack);
            }
        }
    }

    public void fillArea(int length, int width, ItemStack borderMaterial) {
        final int size = menu.getSize();

        MenuItem itemStack = MenuItem.of(borderMaterial);
        for (Page page : menu.pageList) {
            for (int i = 0; i < size; i++) {
                if (isInArea(i, length, width)) page.setItem(i, itemStack);
            }
        }
    }

    public void fillArea(int length, int width, MenuItem itemStack) {
        final int size = menu.getSize();

        for (Page page : menu.pageList) {
            for (int i = 0; i < size; i++) {
                if (isInArea(i, length, width)) page.setItem(i, itemStack);
            }
        }
    }

    // simple geometry to check if a slot is in an area of L*W

    private static boolean isInArea(int slot, int length, int width) {
        int rows = length / 9;
        int startRow = slot / 9;
        return startRow < rows && slot % 9 < width;
    }
}
