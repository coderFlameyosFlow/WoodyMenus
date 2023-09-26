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

    /**
     * Fill all the <strong>EMPTY</strong> slots in a specific row.
     * @param row the row to fill
     * @param borderMaterial the material to fill the row with
     */
    void fillRow(int row, Material borderMaterial);

    /**
     * Fill all the <strong>EMPTY</strong> slots in a specific row.
     * @param row the row to fill
     * @param borderMaterial the material to fill the row with
     */
    void fillRow(int row, ItemStack borderMaterial);

    /**
     * Fill all the <strong>EMPTY</strong> slots in a specific row.
     * @param row the row to fill
     * @param itemStack material to fill the row with
     */
    void fillRow(int row, MenuItem itemStack);

    /**
     * Fill all the empty slots in the A of Length * Width;
     * check out the implementation of "isInArea" before using this method
     * @param length the length.
     *               check out some geometry if you don't know what "length" is supposed to mean
     * @param width the width
     *              check out some geometry if you don't know what "length" is supposed to mean
     * @param borderMaterial the material
     */
    void fillArea(int length, int width, Material borderMaterial);

    /**
     * Fill all the empty slots in the A of Length * Width;
     * check out the implementation of "isInArea" before using this method
     * @param length the length.
     *               check out some geometry if you don't know what "length" is supposed to mean
     * @param width the width
     *              check out some geometry if you don't know what "length" is supposed to mean
     * @param borderMaterial the material
     */
    void fillArea(int length, int width, ItemStack borderMaterial);

    /**
     * Fill all the empty slots in the A of Length * Width;
     * check out the implementation of "isInArea" before using this method
     * @param length the length.
     *               check out some geometry if you don't know what "length" is supposed to mean
     * @param width the width
     *              check out some geometry if you don't know what "length" is supposed to mean
     * @param itemStack the material
     */
    void fillArea(int length, int width, MenuItem itemStack);
}
