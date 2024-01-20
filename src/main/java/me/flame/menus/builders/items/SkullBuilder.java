package me.flame.menus.builders.items;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.flame.menus.util.VersionHelper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Skull builder, self-explanatory, it builds player heads.
 */
@SuppressWarnings({ "unused", "deprecation" })
public final class SkullBuilder extends BaseItemBuilder<SkullBuilder> {

    private static final Field PROFILE_FIELD;

    /**
     * The main SKULL material for the version
     */
    public static final Material SKULL = VersionHelper.IS_ITEM_LEGACY
            ? Material.valueOf("SKULL_ITEM")
            : Material.PLAYER_HEAD;


    /**
     * Create a player skull
     *
     * @return player skull
     */
    @SuppressWarnings("deprecation")
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

    static {
        Field field;
        try {
            Class<?> itemMetaClass = Objects.requireNonNull(skull().getItemMeta()).getClass();
            field = itemMetaClass.getDeclaredField("profile");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to get profile field", e);
            field = null;
        }
        PROFILE_FIELD = field;
    }

    SkullBuilder() {
        super(skull());
    }

    SkullBuilder(final @NotNull ItemStack itemStack) {
        super(itemStack);
        if (isNotPlayerSkull(itemStack)) {
            throw new IllegalArgumentException(
                    "SkullBuilder requires the material to be a PLAYER_HEAD!" +
                    "\nFix: Change material to a SKULL_ITEM or PLAYER_HEAD."
            );
        }
    }

    @Contract("_ -> new")
    public static @NotNull SkullBuilder of(ItemStack item) {
        return new SkullBuilder(item);
    }

    @Contract("_ -> new")
    public static @NotNull SkullBuilder of(Material item) {
        return new SkullBuilder(new ItemStack(item, 1));
    }

    @Contract("_, _ -> new")
    public static @NotNull SkullBuilder of(Material item, int amount) {
        return new SkullBuilder(new ItemStack(item, amount));
    }

    /**
     * Sets the skull texture using a BASE64 string
     *
     * @param texture The base64 texture
     * @param profileId The unique id of the profile
     * @return {@link SkullBuilder}
     */
    @NotNull
    @Contract("_, _ -> this")
    public SkullBuilder texture(@NotNull final String texture, @NotNull final UUID profileId) {
        if (isNotPlayerSkull(item) || PROFILE_FIELD == null) return this;

        final SkullMeta skullMeta = (SkullMeta) meta;
        final GameProfile profile = new GameProfile(profileId, null);
        profile.getProperties().put("textures", new Property("textures", texture));

        try {
            PROFILE_FIELD.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
        }
        this.meta = skullMeta;
        return this;
    }

    /**
     * Sets the skull texture using a BASE64 string
     *
     * @param texture The base64 texture
     * @return {@link SkullBuilder}
     */
    @NotNull
    @Contract("_ -> this")
    public SkullBuilder texture(@NotNull final String texture) {
        return texture(texture, UUID.randomUUID());
    }

    /**
     * Sets skull owner via bukkit methods
     *
     * @param player {@link OfflinePlayer} to set skull of
     * @return {@link SkullBuilder}
     */
    @NotNull
    @Contract("_ -> this")
    public SkullBuilder owner(@NotNull final OfflinePlayer player) {
        if (isNotPlayerSkull(item)) return this;
        final SkullMeta skullMeta = (SkullMeta) meta;

        if (VersionHelper.IS_SKULL_OWNER_LEGACY)
            skullMeta.setOwner(player.getName());
        else
            skullMeta.setOwningPlayer(player);

        this.meta = skullMeta;
        return this;
    }

}
