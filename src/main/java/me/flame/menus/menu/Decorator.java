package me.flame.menus.menu;

import me.flame.menus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface Decorator {
    /**
     * Fills the borders of the pages with a given item stack.
     *
     * @param itemStack the item stack to fill the pages with
     */
    void fillPageBorders(ItemStack itemStack);

    /**
     * Fills the borders of the pages with a given item stack.
     *
     * @param borderMaterial  the item stack to fill the pages with
     */
    void fillPageBorders(Material borderMaterial);

    /**
     * Fills the borders of the pages with a given item stack.
     *
     * @param item the item stack to fill the pages with
     */
    void fillPageBorders(MenuItem item);

    /**
     * Fills the pages of a menu with a given item stack.
     *
     * @param borderMaterial  the item stack to fill the pages with
     */
    void fillPages(Material borderMaterial);

    /**
     * Fills the pages of a menu with a given item stack.
     *
     * @param menuItem  the item stack to fill the pages with
     */
    void fillPages(@NotNull MenuItem menuItem);

    /**
     * Fills the pages of a menu with a given item stack.
     *
     * @param  itemStack  the item stack to fill the pages with
     */
    void fillPages(ItemStack itemStack);
}
