package me.flame.menus.components.nbt;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.errorprone.annotations.CompileTimeConstant;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Enhanced Legacy NBT wrapper for {@link ItemStack}
 * @since 2.0.0
 */
public class LegacyNbt implements NbtWrapper {
    @CompileTimeConstant
    private static final Table<String, ItemStack, String> nbtData = HashBasedTable.create();

    public ItemStack setString(@NotNull ItemStack itemStack, String key, String value) {
        nbtData.put(key, itemStack, value);
        return itemStack;
    }

    public ItemStack removeTag(@NotNull ItemStack itemStack, String key) {
        nbtData.remove(key, itemStack);
        return itemStack;
    }

    public ItemStack setBoolean(@NotNull ItemStack itemStack, String key, boolean value) {
        nbtData.put(key, itemStack, String.valueOf(value));
        return itemStack;
    }

    public String getString(@NotNull ItemStack itemStack, String key) {
        return nbtData.get(key, itemStack);
    }
}