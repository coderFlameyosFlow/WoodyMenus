package me.flame.menus.items;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

/**
 * @since 2.0.0
 */
public class SkullItemEditor extends ItemEditor {
    private final SkullMeta skullMeta;
    public SkullItemEditor(MenuItem item) {
        super(item);
        this.skullMeta = (SkullMeta) meta;
    }

    public SkullItemEditor setOwningPlayer(OfflinePlayer player) {
        skullMeta.setOwningPlayer(player);
        return this;
    }

    /**
     * Sets the owner of the skull.
     *
     * @param owner the new owner of the skull
     * @return true if the owner was successfully set
     * @deprecated see {@link #setOwningPlayer(org.bukkit.OfflinePlayer)}.
     */
    @Deprecated
    public SkullItemEditor setOwner(@Nullable String owner) {
        skullMeta.setOwner(owner);
        return this;
    }

    @Override
    public MenuItem done() {
        this.item.setItemMeta(skullMeta);
        menuItem.itemStack = this.item;
        menuItem.clickAction = clickAction;
        return menuItem;
    }
}
