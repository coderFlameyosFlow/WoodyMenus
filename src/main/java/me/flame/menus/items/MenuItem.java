package me.flame.menus.items;

import me.flame.menus.components.nbt.ItemNbt;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * A Gui itemStack which was particularly made to have an action.
 * <p>
 * It can be used for MCMAPI.
 * <p>
 * Good example of using "MenuItem":
 * <pre>{@code
 *      var menuItem = ...;
 *      menuItem.setClickAction(event -> {
 *          ...
 *      });
 *
 *      // implementing a new itemStack:
 *      menu.addItem(ItemBuilder.of(itemStack, 2) // 2 is the amount of items you get from this "ItemBuilder"
 *                                  .setName(...).setLore(...)
 *                                  .buildItem(() -> ...);
 *      // the lambda (Consumer) at ItemBuilder#buildItem(Consumer) is optional and you do not have to provide an action, you can use ItemBuilder#buildItem()
 * }</pre>
 */
@SuppressWarnings("unused")
public final class MenuItem implements Cloneable, ConfigurationSerializable {
    @NotNull
    Consumer<InventoryClickEvent> clickAction;

    @NotNull
    ItemStack itemStack;

    @NotNull
    private final UUID uuid;

    @ApiStatus.Obsolete
    public MenuItem(ItemStack itemStack, @Nullable Consumer<InventoryClickEvent> action) {
        Objects.requireNonNull(itemStack);
        this.uuid =  UUID.randomUUID();
        this.itemStack = ItemNbt.setString(itemStack, "woody-menu", uuid.toString());
        this.clickAction = action == null ? (event -> {}) : action;
    }

    public static @NotNull MenuItem of(ItemStack itemStack, @Nullable Consumer<InventoryClickEvent> action) {
        return new MenuItem(itemStack, action);
    }

    public static @NotNull MenuItem of(ItemStack itemStack) {
        return new MenuItem(itemStack, null);
    }

    public @NotNull Consumer<InventoryClickEvent> getClickAction() {
        return clickAction;
    }

    public void setClickAction(@NotNull Consumer<InventoryClickEvent> clickAction) {
        this.clickAction = clickAction;
    }

    @Contract(" -> new")
    public @NotNull ItemEditor editor() {
        return new ItemEditor(this);
    }

    public @NotNull ItemStack getItemStack() {
        return itemStack;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = ItemNbt.setString(itemStack, "woody-menu", uuid.toString());
    }

    public @NotNull Material getType() {
        return itemStack.getType();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MenuItem)) return false;
        MenuItem item = (MenuItem) o;

        if (this == item) return true;
        return uuid.equals(item.uuid) && itemStack.equals(item.itemStack);
    }

    @Override
    public @NotNull MenuItem clone() {
        try {
            return (MenuItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void click(InventoryClickEvent event) {
        clickAction.accept(event);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("type", getType().name());
        result.put("uuid", uuid);

        ItemMeta meta = itemStack.getItemMeta();
        int amount = itemStack.getAmount();

        if (amount != 1) result.put("amount", amount);
        if (meta != null) result.put("meta", meta);

        return result;
    }

    public @NotNull MenuItem deserialize(@NotNull Map<String, Object> serialized) {
        String type = (String) serialized.get("type");
        int amount = (int) serialized.getOrDefault("amount", 1);
        ItemMeta meta = (ItemMeta) serialized.get("meta");
        UUID uuid = (UUID) serialized.get("uuid");

        ItemStack result = new ItemStack(Material.valueOf(type), amount);
        if (meta != null) result.setItemMeta(meta);

        return MenuItem.of(itemStack);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode(); // they provide a fast hashcode
    }
}
