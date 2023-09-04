package me.flame.menus.builders.items;

import me.flame.menus.items.MenuItem;

import me.flame.menus.util.VersionHelper;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

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

    public B setAmount(int amount) {
        this.item.setAmount(amount);
        return (B) this;
    }

    public B setName(String name) {
        if (this.hasNoItemMeta) return (B) this;
        this.meta.setDisplayName(name);
        return (B) this;
    }

    public B setLore(String... lore) {
        if (this.hasNoItemMeta) return (B) this;
        this.meta.setLore(Arrays.asList(lore));
        return (B) this;
    }

    public B setLore(List<String> lore) {
        if (this.hasNoItemMeta) return (B) this;
        this.meta.setLore(lore);
        return (B) this;
    }

    public B addEnchant(Enchantment enchant) {
        if (this.hasNoItemMeta) return (B) this;
        this.meta.addEnchant(enchant, 1, false);
        return (B) this;
    }

    public B addEnchant(Enchantment enchant, int level) {
        if (this.hasNoItemMeta) return (B) this;
        this.meta.addEnchant(enchant, level, false);
        return (B) this;
    }

    public B addEnchant(Enchantment enchant, int level, boolean ignore) {
        if (this.hasNoItemMeta) return (B) this;
        this.meta.addEnchant(enchant, level, ignore);
        return (B) this;
    }

    public B addEnchant(int level, boolean ignore, Enchantment... enchant) {
        if (this.hasNoItemMeta) return (B) this;
        for (Enchantment enchantment : enchant)
            this.meta.addEnchant(enchantment, level, ignore);
        return (B) this;
    }

    public B addEnchant(int level, Enchantment... enchant) {
        if (this.hasNoItemMeta) return (B) this;
        for (Enchantment enchantment : enchant)
            this.meta.addEnchant(enchantment, level, false);
        return (B) this;
    }

    public B addEnchant(Enchantment... enchant) {
        if (this.hasNoItemMeta) return (B) this;
        for (Enchantment enchantment : enchant)
            this.meta.addEnchant(enchantment, 1, false);
        return (B) this;
    }

    public B setUnbreakable() {
        if (this.hasNoItemMeta || VersionHelper.IS_UNBREAKABLE_LEGACY) return (B) this;
        this.meta.setUnbreakable(true);
        return (B) this;
    }

    public B setUnbreakable(boolean breakable) {
        if (this.hasNoItemMeta || VersionHelper.IS_UNBREAKABLE_LEGACY) return (B) this;
        this.meta.setUnbreakable(breakable);
        return (B) this;
    }

    public B setDamage(int d) {
        if (this.hasNoItemMeta) return (B) this;
        if (meta instanceof Damageable) {
            Damageable damageable = ((Damageable) meta);
            damageable.damage(d);
        }
        return (B) this;
    }

    public ItemStack build() {
        this.item.setItemMeta(meta);
        return item;
    }

    public MenuItem buildItem() {
        this.item.setItemMeta(meta);
        return MenuItem.of(item);
    }

    public MenuItem buildItem(Consumer<InventoryClickEvent> event) {
        this.item.setItemMeta(meta);
        return MenuItem.of(item, event);
    }
}
