package me.flame.menus.items;

import lombok.Getter;
import lombok.Setter;
import me.flame.menus.components.nbt.ItemNbt;
import me.flame.menus.events.ClickActionEvent;
import me.flame.menus.menu.ActionResponse;

import me.flame.menus.util.ItemResponse;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
@SerializableAs("woody-menu")
public final class MenuItem implements Cloneable, ConfigurationSerializable {
    @NotNull
    CompletableFuture<ItemResponse> clickAction;

    @Getter @Setter
    boolean async = false;

    @NotNull
    ItemStack itemStack;

    @NotNull
    private final UUID uuid;

    public MenuItem(ItemStack itemStack, @Nullable ItemResponse action) {
        Objects.requireNonNull(itemStack);
        this.uuid =  UUID.randomUUID();
        this.itemStack = ItemNbt.setString(itemStack, "woody-menu", uuid.toString());

        this.clickAction = CompletableFuture
                .completedFuture(action == null ? (slot, event) -> ActionResponse.DONE : action);
    }

    private MenuItem(ItemStack itemStack, @Nullable ItemResponse action, UUID uuid) {
        Objects.requireNonNull(itemStack);
        this.uuid =  UUID.randomUUID();
        this.itemStack = ItemNbt.setString(itemStack, "woody-menu", uuid.toString());

        this.clickAction = CompletableFuture
            .completedFuture(action == null ? (slot, event) -> ActionResponse.DONE : action);
    }

    public static @NotNull MenuItem of(ItemStack itemStack, @Nullable ItemResponse action) {
        return new MenuItem(itemStack, action);
    }

    public static @NotNull MenuItem of(ItemStack itemStack) {
        return new MenuItem(itemStack, null);
    }

    @NotNull
    public CompletableFuture<ItemResponse> getClickAction() {
        return clickAction;
    }

    @NotNull
    public ItemResponse getClickActionNow(ItemResponse ifAbsent) {
        return clickAction.getNow(ifAbsent);
    }

    public void setClickAction(@NotNull ItemResponse clickAction) {
        this.clickAction = CompletableFuture.completedFuture(clickAction);
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
        if (o == this) return true;
        if (!(o instanceof MenuItem)) return false;

        MenuItem item = (MenuItem) o;
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

    public CompletableFuture<ActionResponse> click(final int slot, final ClickActionEvent event) {
        return async
                ? clickAction.thenApplyAsync(ca -> ca.apply(slot, event)) // this is what's executing async btw
                : clickAction.thenApply(ca -> ca.apply(slot, event));
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        final Map<String, Object> result = new LinkedHashMap<>(4);

        result.put("type", getType().name());
        result.put("uuid", uuid);

        final ItemMeta meta = itemStack.getItemMeta();
        final int amount = itemStack.getAmount();

        if (amount != 1) result.put("amount", amount);
        if (meta != null) result.put("meta", meta);

        return result;
    }

    @NotNull
    public static MenuItem deserialize(@NotNull Map<String, Object> serialized) {
        final String type = (String) serialized.get("type");
        final int amount = (int) serialized.getOrDefault("amount", 1);
        final ItemMeta meta = (ItemMeta) serialized.get("meta");
        final UUID uuid = (UUID) serialized.get("uuid");

        final ItemStack result = new ItemStack(Material.valueOf(type), amount);
        if (meta != null) result.setItemMeta(meta);

        return new MenuItem(result, null, uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode(); // they provide a fast hashcode
    }
}
