package me.flame.menus.adventure;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.meta.ItemMeta;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

/**
 * Wrapper of a legacy string value.
 * {@link org.bukkit.ChatColor} based formatting is used.
 *
 * @since 0.10.0
 */
@SuppressWarnings("deprecation")
public final class StringHolder extends TextHolder {
    /**
     * Cached instance which wraps an empty {@link String}.
     */
    @NotNull
    private static final StringHolder EMPTY = StringHolder.of("");
    
    /**
     * Wraps the specified legacy string.
     *
     * @param value the value to wrap
     * @return an instance that wraps the specified value
     * @since 0.10.0
     */
    @NotNull
    @Contract(pure = true)
    public static StringHolder of(@NotNull String value) {
        Objects.requireNonNull(value, "value mustn't be null");
        return new StringHolder(value);
    }
    
    /**
     * Gets an instance that contains no characters.
     *
     * @return an instance without any characters
     * @since 0.10.0
     */
    @NotNull @Contract(pure = true)
    public static StringHolder empty() {
        return EMPTY;
    }
    
    /**
     * The legacy string this instance wraps.
     */
    @NotNull
    private final String value;
    
    /**
     * Creates and initializes a new instance.
     *
     * @param value the legacy string this instance should wrap
     * @since 0.10.0
     */
    private StringHolder(@NotNull String value) {
        this.value = translateAlternateColorCodes('&', value);
    }
    
    @NotNull
    @Override
    @Contract(pure = true)
    public String toString() {
        return value;
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        return other != null && getClass() == other.getClass()
                && Objects.equals(value, ((StringHolder) other).value);
    }
    
    @NotNull @Contract(pure = true) @Override
    public Inventory toInventory(InventoryHolder holder, InventoryType type) {
        return Bukkit.createInventory(holder, type, value);
    }
    
    @NotNull
    @Contract(pure = true)
    @Override
    public Inventory toInventory(InventoryHolder holder, int size) {
        return Bukkit.createInventory(holder, size, value);
    }

    @Override
    public void asItemDisplayName(@NotNull ItemMeta meta) {
        //noinspection deprecation
        meta.setDisplayName(value);
    }
    
    @Override
    public void asItemLoreAtEnd(@NotNull ItemMeta meta) {
        List<String> lore = meta.hasLore()
                ? Objects.requireNonNull(meta.getLore())
                : new ArrayList<>();
        lore.add(value);
        meta.setLore(lore);
    }

    @Override
    public void asItemLore(@NotNull ItemMeta meta) {
        List<String> lore = new ArrayList<>();
        lore.add(value);
        meta.setLore(lore);
    }
}
