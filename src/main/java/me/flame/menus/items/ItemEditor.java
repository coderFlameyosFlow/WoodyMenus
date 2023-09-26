package me.flame.menus.items;

import me.flame.menus.util.ItemResponse;
import me.flame.menus.util.VersionHelper;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

@SuppressWarnings("unused")
public final class ItemEditor {
    @NotNull
    private final ItemStack item;

    @NotNull
    private final MenuItem menuItem;

    @NotNull
    private CompletableFuture<ItemResponse> clickAction;

    private final ItemMeta meta;

    private final boolean hasNoItemMeta;

    public ItemEditor(MenuItem item) {
        this.menuItem = item;
        this.item = item.itemStack;
        this.clickAction = item.clickAction;
        this.meta = this.item.getItemMeta();
        this.hasNoItemMeta = this.meta == null;
    }

    /**
     * Edits the name of the itemStack to whatever the provided title is.
     * <p>
     * Automatically colorized, so no need to try to colorize it again.
     * @param title the new name of the title
     * @return the builder for chaining
     */
    public ItemEditor setName(String title) {
        return this.setName(title, true);
    }

    /**
     * Sets the glow effect on the item.
     *
     * @param  glow  true to add enchantment and hide it, false to remove enchantment and show it
     * @apiNote Will hide the enchantments by default.
     * @return       the builder for chaining
     */
    public ItemEditor setGlow(boolean glow) {
        // add enchantment and hide it if "glow" is true
        if (this.hasNoItemMeta) return this;
        if (!glow) {
            this.meta.removeEnchant(Enchantment.DURABILITY);
            this.meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            return this;
        }
        this.meta.addEnchant(Enchantment.DURABILITY, 1, true);
        this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    /**
     * Edits the name of the itemStack to whatever the provided title is.
     * <p>
     * Automatically colorized, so no need to try to colorize it again.
     * @param title the new name of the title
     * @return the builder for chaining
     */
    public ItemEditor setName(String title, boolean colorize) {
        if (this.hasNoItemMeta) return this;
        this.meta.setDisplayName(colorize
                ? translateAlternateColorCodes('&', title)
                : title);
        return this;
    }


    /**
     * Edits the lore of the itemStack to whatever the provided lore is.
     * <p>
     * Automatically colorized, so no need to try to colorize it again.
     * @param lore the new lore of the itemStack
     * @return the builder for chaining
     */
    public ItemEditor setLore(String... lore) {
        return this.setLore(Arrays.asList(lore));
    }

    /**
     * Edits the lore of the itemStack to whatever the provided lore is.
     * <p>
     * Automatically colorized, so no need to try to colorize it again.
     * @param lore the new lore of the itemStack
     * @return the builder for chaining
     */
    public ItemEditor setLore(List<String> lore) {
        if (this.hasNoItemMeta) return this;
        int loreSize = lore.size();
        List<String> ogLore = new ArrayList<>(loreSize);
        for (int i = 0; i < loreSize; i++) {
            ogLore.add(translateAlternateColorCodes('&', lore.get(i)));
        }
        this.meta.setLore(ogLore);
        return this;
    }

    /**
     * Edits the lore of the itemStack to whatever the provided lore is.
     * <p>
     * Automatically colorized, so no need to try to colorize it again.
     * @param colorized whether to colorize it or not
     * @param lore the new lore of the itemStack
     * @return the builder for chaining
     */
    public ItemEditor setLore(boolean colorized, String... lore) {
        return this.setLore(colorized, Arrays.asList(lore));
    }

    /**
     * Edits the lore of the itemStack to whatever the provided lore is.
     * <p>
     * Automatically colorized, so no need to try to colorize it again.
     * @param colorized whether to colorize it or not
     * @param lore the new lore of the itemStack
     * @return the builder for chaining
     */
    public ItemEditor setLore(boolean colorized, List<String> lore) {
        if (this.hasNoItemMeta) return this;
        if (!colorized) return this.setLore(lore);
        this.meta.setLore(lore);
        return this;
    }

    /**
     * Empty the lore completely and leave it with no lines
     * @return the builder for chaining
     */
    public ItemEditor emptyLore() {
        if (this.hasNoItemMeta) return this;
        this.meta.setLore(Collections.emptyList());
        return this;
    }

    /**
     * Enchant the itemStack regularly
     * @param enchantment the enchantment
     * @return the builder for chaining
     */
    public ItemEditor enchant(Enchantment enchantment) {
        if (this.hasNoItemMeta) return this;
        this.meta.addEnchant(enchantment, 1, false);
        return this;
    }

    /**
     * Enchant the itemStack regularly
     * @param enchantment the enchantment
     * @param level the level of the enchantment
     * @return the builder for chaining
     */
    public ItemEditor enchant(Enchantment enchantment, int level) {
        if (this.hasNoItemMeta) return this;
        this.meta.addEnchant(enchantment, level, false);
        return this;
    }

    /**
     * Enchant the itemStack regularly
     * @param enchantment the enchantment
     * @param level the level of the enchantment
     * @param ignoreEnchantRestriction ignore the enchant restriction or not (max level depends on the enchantment)
     * @return the builder for chaining
     */
    public ItemEditor enchant(Enchantment enchantment, int level, boolean ignoreEnchantRestriction) {
        if (this.hasNoItemMeta) return this;
        this.meta.addEnchant(enchantment, level, ignoreEnchantRestriction);
        return this;
    }

    /**
     * Set the amount of items to a specific provided amount
     * <p>
     * guaranteed to fail and return if over a stack
     * @param amount the provided amount
     * @return the builder for chaining
     */
    public ItemEditor setAmount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    /**
     * add an amount of items to a specific provided amount
     * <p>
     * guaranteed to fail and return if over a stack
     * @param amount the provided amount
     * @return the builder for chaining
     */
    public ItemEditor addAmount(int amount) {
        this.item.setAmount(item.getAmount() + amount);
        return this;
    }

    public ItemEditor setCustomModelData(Integer customModelData) {
        if (VersionHelper.IS_CUSTOM_MODEL_DATA)
            this.meta.setCustomModelData(customModelData);
        return this;
    }

    public ItemEditor setAction(@NotNull ItemResponse event) {
        this.clickAction = CompletableFuture.completedFuture(event);
        return this;
    }

    public ItemEditor setActionAsync(@NotNull CompletableFuture<ItemResponse> event) {
        this.clickAction = event;
        return this;
    }

    public ItemEditor setActionAsync(@NotNull ItemResponse event) {
        this.clickAction = CompletableFuture.supplyAsync(() -> event);
        return this;
    }

    /**
     * Calling this method means you're finally done.
     * <p>
     * and also that you want the new itemStack as you edited everything you need
     * @return the new menu itemStack
     */
    public MenuItem done() {
        this.item.setItemMeta(meta);
        menuItem.itemStack = this.item;
        menuItem.clickAction = clickAction;
        return menuItem;
    }
}
