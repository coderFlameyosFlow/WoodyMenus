package me.flame.menus.builders.items;

import me.flame.menus.items.MenuItem;
import me.flame.menus.util.ItemResponse;
import me.flame.menus.util.VersionHelper;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

@SuppressWarnings({ "unchecked", "unused" })
public abstract class BaseItemBuilder<B extends BaseItemBuilder<B>> {
    final ItemStack item;

    ItemMeta meta;

    private final boolean hasNoItemMeta;

    BaseItemBuilder(Material material, int amount) {
        this(new ItemStack(material, amount));
    }

    BaseItemBuilder(@NotNull ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
        this.hasNoItemMeta = this.meta == null;
    }

    /**
     * Sets the glow effect on the item.
     *
     * @param  glow  true to add enchantment and hide it, false to remove enchantment and show it
     * @apiNote Will hide the enchantments by default.
     * @return       the builder for chaining
     */
    public B setGlow(boolean glow) {
        // add enchantment and hide it if "glow" is true
        if (this.hasNoItemMeta) return (B) this;
        if (!glow) {
            this.meta.removeEnchant(Enchantment.DURABILITY);
            this.meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            return (B) this;
        }
        this.meta.addEnchant(Enchantment.DURABILITY, 1, true);
        this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return (B) this;
    }

    /**
     * Sets the amount of the item.
     * @param amount the amount to set
     * @return the builder for chaining
     */
    public B setAmount(int amount) {
        this.item.setAmount(amount);
        return (B) this;
    }

    /**
     * Sets the name of the itemStack to whatever the provided name is.
     * @param name the new name
     * @return the builder for chaining
     */
    public B setName(String name) {
        if (this.hasNoItemMeta) return (B) this;
        this.meta.setDisplayName(translateAlternateColorCodes('&', name));
        return (B) this;
    }

    /**
     * Sets the lore of the itemStack to whatever the provided lore is.
     * @param lore the new lore
     * @return the builder for chaining
     */
    public B setLore(String... lore) {
        if (this.hasNoItemMeta) return (B) this;
        this.meta.setLore(Arrays.asList(lore));
        return (B) this;
    }

    /**
     * Sets the lore of the itemStack to whatever the provided lore is.
     * @param lore the new lore
     * @return the builder for chaining
     */
    public B setLore(List<String> lore) {
        if (this.hasNoItemMeta) return (B) this;
        this.meta.setLore(lore);
        return (B) this;
    }

    /**
     * Enchant the itemStack with the provided enchantment
     * @param enchant the enchantment to enchant the itemStack with
     * @return the builder for chaining
     */
    public B addEnchant(Enchantment enchant) {
        if (this.hasNoItemMeta) return (B) this;
        this.meta.addEnchant(enchant, 1, false);
        return (B) this;
    }

    /**
     * Enchant the itemStack with the provided enchantment
     * @param enchant the enchantment to enchant the itemStack with
     * @param level the level of the enchantment
     * @return the builder for chaining
     */
    public B addEnchant(Enchantment enchant, int level) {
        if (this.hasNoItemMeta) return (B) this;
        this.meta.addEnchant(enchant, level, false);
        return (B) this;
    }

    /**
     * Enchant the itemStack with the provided enchantment
     * @param enchant the enchantment to enchant the itemStack with
     * @param level the level of the enchantment
     * @param ignore whether to ignore the enchantment restrictions
     * @return the builder for chaining
     */
    public B addEnchant(Enchantment enchant, int level, boolean ignore) {
        if (this.hasNoItemMeta) return (B) this;
        this.meta.addEnchant(enchant, level, ignore);
        return (B) this;
    }

    /**
     * Apply all the enchantments to the itemStack with the same level & ignore restrictions
     * @param level the level of the enchantment
     * @param ignore whether to ignore the enchantment restrictions
     * @param enchant the enchantments to apply
     * @return the builder for chaining
     */
    public B addEnchant(int level, boolean ignore, Enchantment... enchant) {
        if (this.hasNoItemMeta) return (B) this;
        for (Enchantment enchantment : enchant)
            this.meta.addEnchant(enchantment, level, ignore);
        return (B) this;
    }

    /**
     * Apply all the enchantments to the itemStack with the same level
     * @param level the level of the enchantment
     * @param enchant the enchantments to apply
     * @return the builder for chaining
     */
    public B addEnchant(int level, Enchantment... enchant) {
        if (this.hasNoItemMeta) return (B) this;
        for (Enchantment enchantment : enchant)
            this.meta.addEnchant(enchantment, level, false);
        return (B) this;
    }

    /**
     * Apply all the enchantments to the itemStack (level 1)
     * @param enchant the enchantments to apply
     * @return the builder for chaining
     */
    public B addEnchant(Enchantment... enchant) {
        if (this.hasNoItemMeta) return (B) this;
        for (Enchantment enchantment : enchant)
            this.meta.addEnchant(enchantment, 1, false);
        return (B) this;
    }

    /**
     * Set the itemStack to be unbreakable
     * @return the builder for chaining
     */
    public B setUnbreakable() {
        if (this.hasNoItemMeta || VersionHelper.IS_UNBREAKABLE_LEGACY) return (B) this;
        this.meta.setUnbreakable(true);
        return (B) this;
    }

    /**
     * Set the itemStack to be unbreakable or not
     * @param breakable whether the itemStack is unbreakable
     * @return the builder for chaining
     */
    public B setUnbreakable(boolean breakable) {
        if (this.hasNoItemMeta || VersionHelper.IS_UNBREAKABLE_LEGACY) return (B) this;
        this.meta.setUnbreakable(breakable);
        return (B) this;
    }

    /**
     * Set the damage to the itemStack
     * @param d the damage
     * @return the builder for chaining
     */
    public B setDamage(int d) {
        if (this.hasNoItemMeta || !(meta instanceof Damageable)) return (B) this;
        ((Damageable) meta).damage(d);
        return (B) this;
    }

    /**
     * Build the item into a new ItemStack.
     * @return the new ItemStack
     */
    public ItemStack build() {
        this.item.setItemMeta(meta);
        return item;
    }

    /**
     * Build the item into a new MenuItem.
     * @return the new MenuItem
     */
    public MenuItem buildItem() {
        return MenuItem.of(build());
    }

    /**
     * Build the item into a new MenuItem with the provided Click Event.
     * @param event the event
     * @return the new MenuItem
     */
    public MenuItem buildItem(ItemResponse event) {
        return MenuItem.of(build(), event);
    }
}
