package me.flame.menus.menu.fillers;

import me.flame.menus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public interface MenuFiller {
    /**
     * Fills the menu with the specified border material.
     *
     * @param  borderMaterial the material to be used for filling the borders
     */
    void fill(Material borderMaterial);

    /**
     * Fills the menu with the specified border material.
     * @param menuItem the material to be used for filling the borders
     */
    void fill(MenuItem menuItem);

    /**
     * Fills the with the specified border material.
     *
     * @param  itemStack the material to be used for filling the borders
     */
    void fill(ItemStack itemStack);

    /**
     * Fills the borders of the menu with the specified border material.
     *
     * @param  borderMaterial the material to be used for filling the borders
     */
    void fillBorders(Material borderMaterial);

    /**
     * Fills the borders of the menu with the specified border material.
     *
     * @param  borderMaterial the material to be used for filling the borders
     */
    void fillBorders(MenuItem borderMaterial);

    /**
     * Fills the borders of the menu with the specified border material.
     *
     * @param  itemStack the material to be used for filling the borders
     */
    void fillBorders(ItemStack itemStack);

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

    void fillSide(Filler.Side side, Material borderMaterial);

    void fillSide(Filler.Side side, ItemStack borderMaterial);

    void fillSide(Filler.Side side, MenuItem borderMaterial);

    static boolean isBorderSlot(int position, int size) {
        int index = position % 9; // convert slot into the column, for example 45 % 9 is 0, 50 % 9 is 5.
        return (index == 0 || index == 8) || (position < 9 || position >= size - 9);
    }

    static boolean isInArea(int slot, int length, int width) {
        return (slot / 9) < (length / 9) && (slot % 9) < width;
    }

    static int findFirstAreaSlot(int width, int length) {
        int area = width * length;
        return (area & -area) - 1;
    }
}
