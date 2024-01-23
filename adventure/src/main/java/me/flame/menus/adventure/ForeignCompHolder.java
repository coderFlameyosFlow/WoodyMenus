package me.flame.menus.adventure;

import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link CompHolder} implementation for platforms where Adventure isn't natively supported.
 * Adventure components are converted to legacy Strings before passed to the Bukkit API.
 *
 * @see NativeCompHolder
 * @since 0.10.0
 * @author stefvanschie at <a href="https://github.com/stefvanschie/IF">IF's github.</a>
 */
class ForeignCompHolder extends CompHolder {
    @NotNull
    private final StringHolder legacy;

    ForeignCompHolder(@NotNull Component value) {
        super(value);
        legacy = StringHolder.of(toString());
    }
    
    @NotNull
    @Contract(pure = true)
    @Override
    public Inventory toInventory(InventoryHolder holder, InventoryType type) {
        return legacy.toInventory(holder, type);
    }
    
    @NotNull
    @Contract(pure = true)
    @Override
    public Inventory toInventory(InventoryHolder holder, int size) {
        return legacy.toInventory(holder, size);
    }
    
    @Override
    public void asItemDisplayName(ItemMeta meta) {
        legacy.asItemDisplayName(meta);
    }
    
    @Override
    public void asItemLoreAtEnd(ItemMeta meta) {
        legacy.asItemLoreAtEnd(meta);
    }

    @Override
    public void asItemLore(ItemMeta meta) {
        legacy.asItemLore(meta);
    }

    @Override
    public boolean contains(@NotNull TextHolder string) {
        return this.legacy.contains(string);
    }
}
