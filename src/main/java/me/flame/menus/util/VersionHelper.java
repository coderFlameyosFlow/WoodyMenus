package me.flame.menus.util;

import com.google.common.primitives.Ints;
import me.flame.menus.exceptions.MenuException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

// changed

public class VersionHelper {
    // Unbreakable change
    private static final int V1_11 = 1110;
    // Material and components on items change
    private static final int V1_13 = 1130;
    // PDC and customModelData
    private static final int V1_14 = 1140;
    // Paper adventure changes
    private static final int V1_16_5 = 1165;
    // SkullMeta#setOwningPlayer was added
    private static final int V1_12_1 = 1121;

    private static final int CURRENT_VERSION = getCurrentVersion();

    /**
     * Checks if the version is lower than 1.13 due to the item changes
     */
    public static final boolean IS_ITEM_LEGACY = CURRENT_VERSION < V1_13;

    /**
     * Checks if the version supports the {@link org.bukkit.inventory.meta.ItemMeta#setUnbreakable(boolean)} method
     */
    public static final boolean IS_UNBREAKABLE_LEGACY = CURRENT_VERSION < V1_11;

    /**
     * Checks if the version supports {@link org.bukkit.persistence.PersistentDataContainer}
     */
    public static final boolean IS_PDC_VERSION = CURRENT_VERSION >= V1_14;

    /**
     * Checks if the version doesn't have {@link org.bukkit.inventory.meta.SkullMeta#setOwningPlayer(OfflinePlayer)} and
     * {@link org.bukkit.inventory.meta.SkullMeta#setOwner(String)} should be used instead
     */
    public static final boolean IS_SKULL_OWNER_LEGACY = CURRENT_VERSION < V1_12_1;

    /**
     * Checks if the version has {@link org.bukkit.inventory.meta.ItemMeta#setCustomModelData(Integer)}
     */
    public static final boolean IS_CUSTOM_MODEL_DATA = CURRENT_VERSION >= V1_14;

    /**
     * Gets the current server version
     *
     * @return A protocol like number representing the version, for example 1.16.5 - 1165
     */
    private static int getCurrentVersion() {
        String bukkitVersion = Bukkit.getBukkitVersion();
        Matcher matcher = Pattern.compile("(\\d+\\.\\d+)(\\.\\d+)?").matcher(bukkitVersion);

        if (matcher.find()) {
            String version = matcher.group(1).replace(".", "");
            String patch = matcher.group(2);
            patch = patch == null ? "0" : patch.replace(".", "");
            return Integer.parseInt(version + patch, 10);
        }

        throw new RuntimeException("Could not retrieve server version!");
    }
}
