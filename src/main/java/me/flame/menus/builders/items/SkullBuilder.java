package me.flame.menus.builders.items;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.flame.menus.exceptions.MenuException;
import me.flame.menus.util.SkullUtil;
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
@SuppressWarnings("unused")

public final class SkullBuilder extends BaseItemBuilder<SkullBuilder> {

    private static final Field PROFILE_FIELD;

    static {
        Field field;
        try {
            Class<?> itemMetaClass = Objects.requireNonNull(SkullUtil.skull().getItemMeta()).getClass();
            field = itemMetaClass.getDeclaredField("profile");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to get profile field", e);
            field = null;
        }
        PROFILE_FIELD = field;
    }

    SkullBuilder() {
        super(SkullUtil.skull());
    }

    SkullBuilder(final @NotNull ItemStack itemStack) throws MenuException {
        super(itemStack);
        if (SkullUtil.isNotPlayerSkull(itemStack)) {
            throw new MenuException(
                    "SkullBuilder requires the material to be a PLAYER_HEAD!" +
                    "\nFix: Change material to a SKULL_ITEM or PLAYER_HEAD."
            );
        }
    }

    @Contract("_ -> new")
    public static @NotNull SkullBuilder of(ItemStack item) throws MenuException {
        return new SkullBuilder(item);
    }

    @Contract("_ -> new")
    public static @NotNull SkullBuilder of(Material item) throws MenuException {
        return new SkullBuilder(new ItemStack(item, 1));
    }

    @Contract("_, _ -> new")
    public static @NotNull SkullBuilder of(Material item, int amount) throws MenuException {
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
        if (SkullUtil.isNotPlayerSkull(item) || PROFILE_FIELD == null) return this;

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
    @SuppressWarnings("deprecation")
    public SkullBuilder owner(@NotNull final OfflinePlayer player) {
        if (SkullUtil.isNotPlayerSkull(item)) return this;
        final SkullMeta skullMeta = (SkullMeta) meta;

        if (VersionHelper.IS_SKULL_OWNER_LEGACY)
            skullMeta.setOwner(player.getName());
        else
            skullMeta.setOwningPlayer(player);

        this.meta = skullMeta;
        return this;
    }

}
