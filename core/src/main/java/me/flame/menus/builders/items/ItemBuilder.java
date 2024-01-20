package me.flame.menus.builders.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ItemBuilder extends BaseItemBuilder<ItemBuilder> {
    ItemBuilder(Material material, int amount) {
        super(material, amount);
    }

    ItemBuilder(@NotNull ItemStack item) {
        super(item);
    }

    @NotNull
    @Contract("_ -> new")
    public static ItemBuilder of(ItemStack item) {
        return new ItemBuilder(item);
    }

    @NotNull
    @Contract("_ -> new")
    public static ItemBuilder of(Material item) {
        return new ItemBuilder(item, 1);
    }

    @NotNull
    @Contract("_, _ -> new")
    public static ItemBuilder of(Material item, int amount) {
        return new ItemBuilder(item, amount);
    }
}
