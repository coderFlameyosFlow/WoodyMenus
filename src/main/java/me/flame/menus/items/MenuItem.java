package me.flame.menus.items;

import me.flame.menus.components.nbt.ItemNbt;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * A Gui itemStack which was particularly made to have an action.
 * <p>
 * It can be used for MCMAPI (Woody is the recommended menus library for MCMAPI, you can use whatever you like).
 * <p>
 * Good example of using "GuiItem":
 * <pre>{@code
 *      var guiItem = ...;
 *      guiItem.setClickAction(event -> {
 *          ...
 *      });
 *
 *      // implementing a new itemStack:
 *      menu.addItem(new ItemBuilder(itemStack, 2) // 2 is the amount of items you get from this "ItemBuilder"
 *                                  .setName(...).setLore(...)
 *                                  .buildItem(() -> ...);
 *      // the lambda (Consumer) at ItemBuilder#buildItem(Consumer) is optional and you do not have to provide an action, you can use ItemBuilder#buildItem()
 * }</pre>
 */
@SuppressWarnings("unused")
public final class MenuItem implements Cloneable {
    private Consumer<InventoryClickEvent> clickAction;
    ItemStack itemStack;

    private UUID uuid;

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

    public @Nullable Consumer<InventoryClickEvent> getClickAction() {
        return clickAction;
    }

    public void setClickAction(@Nullable Consumer<InventoryClickEvent> clickAction) {
        this.clickAction = clickAction;
    }

    @Contract(" -> new")
    public @NotNull ItemEditor editor() {
        return new ItemEditor(this);
    }

    public ItemStack getItemStack() {
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
            MenuItem item = (MenuItem) super.clone();
            item.uuid = uuid;
            return item;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void click(InventoryClickEvent event) {
        clickAction.accept(event);
    }
}
