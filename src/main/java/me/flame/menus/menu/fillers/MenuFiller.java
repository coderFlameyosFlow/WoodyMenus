package me.flame.menus.menu.fillers;

import me.flame.menus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface MenuFiller {
    void fill(Material borderMaterial);
    void fill(MenuItem menuItem);
    void fill(ItemStack itemStack);
    void fillBorders(Material borderMaterial);
    void fillBorders(MenuItem borderMaterial);
    void fillBorders(ItemStack itemStack);

    static boolean isBorderSlot(int i, int size) {
        return (i < 9 || i >= size - 9) || (i % 9 == 0 || i % 9 == 8);
    }
}
