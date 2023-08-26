package me.flame.menus.items;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

@SuppressWarnings("unused")
public final class ItemEditor {
    private final ItemStack item;
    private final MenuItem menuItem;
    private final ItemMeta meta;

    public ItemEditor(MenuItem item) {
        this.menuItem = item;
        this.item = item.getItemStack();
        this.meta = this.item.getItemMeta();
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
     * Edits the name of the itemStack to whatever the provided title is.
     * <p>
     * Automatically colorized, so no need to try to colorize it again.
     * @param title the new name of the title
     * @return the builder for chaining
     */
    public ItemEditor setName(String title, boolean colorize) {
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
    @SuppressWarnings("ForLoopReplaceableByForEach") // slight performance improvement
    public ItemEditor setLore(List<String> lore) {
        int loreSize = lore.size();
        List<String> ogLore = new ArrayList<>(loreSize);
        for (int i = 0; i < loreSize; i++)
            ogLore.add(translateAlternateColorCodes('&', lore.get(i)));
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
        if (!colorized) return this.setLore(lore);
        this.meta.setLore(lore);
        return this;
    }

    /**
     * Empty the lore completely and leave it with no lines
     * @return the builder for chaining
     */
    public ItemEditor emptyLore() {
        this.meta.setLore(Collections.emptyList());
        return this;
    }

    /**
     * Enchant the itemStack regularly
     * @param enchantment the enchantment
     * @return the builder for chaining
     */
    public ItemEditor enchant(Enchantment enchantment) {
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
        this.item.add(amount);
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
        return new MenuItem(this.item, this.menuItem.getClickAction());
    }
}
