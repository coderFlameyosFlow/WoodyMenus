package me.flame.menus.builders.items;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.flame.menus.exceptions.MenuException;
import me.flame.menus.util.SkullUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
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
            field = SkullUtil.skull()
                             .getItemMeta()
                             .getClass()
                             .getDeclaredField("profile");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
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
            throw new MenuException("SkullBuilder requires the material to be a PLAYER_HEAD!");
        }
    }

    SkullBuilder(final @NotNull ItemStack itemStack, boolean dummy /* unchecked */) {
        super(itemStack);
    }

    public static SkullBuilder of(ItemStack item) throws MenuException {
        return new SkullBuilder(item);
    }

    public static SkullBuilder of(Material item) throws MenuException {
        return new SkullBuilder(new ItemStack(item, 1));
    }

    public static SkullBuilder of(Material item, int amount) throws MenuException {
        return new SkullBuilder(new ItemStack(item, amount));
    }

    public static SkullBuilder ofUnchecked(ItemStack item) {
        return new SkullBuilder(item, true);
    }

    public static SkullBuilder ofUnchecked(Material item) {
        return new SkullBuilder(new ItemStack(item, 1), true);
    }

    public static SkullBuilder ofUnchecked(Material item, int amount) {
        return new SkullBuilder(new ItemStack(item, amount), true);
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
    public SkullBuilder owner(@NotNull final OfflinePlayer player) {
        if (SkullUtil.isNotPlayerSkull(item)) return this;
        final SkullMeta skullMeta = (SkullMeta) meta;
        skullMeta.setOwningPlayer(player);

        this.meta = skullMeta;
        return this;
    }

}
