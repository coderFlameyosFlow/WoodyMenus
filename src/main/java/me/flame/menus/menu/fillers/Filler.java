package me.flame.menus.menu.fillers;

import lombok.val;
import lombok.var;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.BaseMenu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Filler implements MenuFiller {
    @NotNull
    private final BaseMenu<?> menu;

    private Filler(@NotNull BaseMenu<?> menu) {
        this.menu = menu;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Filler from(@NotNull BaseMenu<?> menu) {
        return new Filler(menu);
    }

    /**
     * Fills the menu with the specified border material.
     *
     * @param  borderMaterial the material to be used for filling the borders
     */
    public void fill(Material borderMaterial) {
        final int size = menu.getSize();

        for (int i = 0; i < size; i++) {
            val item = menu.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                menu.setItem(i, new ItemStack(borderMaterial));
            }
        }
    }

    /**
     * Fills the menu with the specified border material.
     * @param menuItem the material to be used for filling the borders
     */
    public void fill(@NotNull MenuItem menuItem) {
        final int size = menu.getSize();

        for (int i = 0; i < size; i++) {
            val item = menu.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                menu.setItem(i, menuItem);
            }
        }
    }

    /**
     * Fills the with the specified border material.
     *
     * @param  itemStack the material to be used for filling the borders
     */
    public void fill(ItemStack itemStack) {
        final int size = menu.getSize();

        for (int i = 0; i < size; i++) {
            val item = menu.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                menu.setItem(i, itemStack);
            }
        }
    }

    /**
     * Fills the borders of the menu with the specified border material.
     *
     * @param  borderMaterial the material to be used for filling the borders
     */
    public void fillBorders(Material borderMaterial) {
        final int size = menu.getSize();
        final var itemStack = new ItemStack(borderMaterial);

        for (int i = 0; i < size; i++) {
            if (MenuFiller.isBorderSlot(i, size)) menu.setItem(i, itemStack);
        }
    }

    /**
     * Fills the borders of the menu with the specified border material.
     *
     * @param  borderMaterial the material to be used for filling the borders
     */
    public void fillBorders(@NotNull MenuItem borderMaterial) {
        final int size = menu.getSize();

        for (int i = 0; i < size; i++) {
            if (MenuFiller.isBorderSlot(i, size)) menu.setItem(i, borderMaterial);
        }
    }

    /**
     * Fills the borders of the menu with the specified border material.
     *
     * @param  itemStack the material to be used for filling the borders
     */
    public void fillBorders(ItemStack itemStack) {
        final int size = menu.getSize();

        for (int i = 0; i < size; i++) {
            if (MenuFiller.isBorderSlot(i, size)) menu.setItem(i, itemStack);
        }
    }
}
