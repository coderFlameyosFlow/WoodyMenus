package me.flame.menus.builders.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({  "unused" })
public class ItemBuilder extends BaseItemBuilder<ItemBuilder> {
    ItemBuilder(Material material, int amount) {
        super(material, amount);
    }

    ItemBuilder(@NotNull ItemStack item) {
        super(item);
    }

    public static ItemBuilder of(ItemStack item) {
        return new ItemBuilder(item);
    }

    public static ItemBuilder of(Material item) {
        return new ItemBuilder(item, 1);
    }

    public static ItemBuilder of(Material item, int amount) {
        return new ItemBuilder(item, amount);
    }
}
