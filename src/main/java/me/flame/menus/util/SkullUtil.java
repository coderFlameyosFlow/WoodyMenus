package me.flame.menus.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class SkullUtil {

    /**
     * The main SKULL material for the version
     */
    private static final Material SKULL = ((Supplier<Material>) () -> {
        if (!VersionHelper.IS_ITEM_LEGACY)
            return Material.PLAYER_HEAD;
        return Material.valueOf("SKULL_ITEM");
    }).get();

    /**
     * Create a player skull
     *
     * @return player skull
     */
    @SuppressWarnings("deprecation") // I HATE LEGACY!!!!!!!!!!!!!!
    public static ItemStack skull() {
        return VersionHelper.IS_ITEM_LEGACY ? new ItemStack(SKULL, 1, (short) 3) : new ItemStack(SKULL);
    }

    /**
     * Checks if an {@link ItemStack} is NOT a player skull
     * @param item itemStack to check
     * @return true if the itemStack is NOT a player skull
     */
    public static boolean isNotPlayerSkull(@NotNull final ItemStack item) {
        return item.getType() != SKULL;
    }

    /**
     * Checks if an {@link ItemStack} is a player skull
     * @param item itemStack to check
     * @return if the itemStack is a player skull
     */
    public static boolean isPlayerSkull(@NotNull final ItemStack item) {
        return item.getType() == SKULL;
    }
}
