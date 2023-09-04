package me.flame.menus.menu.fillers;

import me.flame.menus.items.MenuItem;

import lombok.val;

import me.flame.menus.menu.BaseMenu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Deprecated
@SuppressWarnings("unused")
@ApiStatus.ScheduledForRemoval(inVersion = "1.4.0")
public final class MenuFiller {
    private final BaseMenu<?> menu;

    MenuFiller(@NotNull BaseMenu<?> menu) {
        this.menu = menu;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull MenuFiller from(@NotNull BaseMenu<?> menu) {
        return new MenuFiller(menu);
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
}
