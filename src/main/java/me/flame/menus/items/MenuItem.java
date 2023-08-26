package me.flame.menus.items;

import me.flame.menus.components.nbt.ItemNbt;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
public final class MenuItem {
    private @Nullable Consumer<InventoryClickEvent> clickAction;

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = ItemNbt.setString(itemStack, "woody-menu", uuid.toString());;

    }

    private ItemStack itemStack;
    private final UUID uuid = UUID.randomUUID();

    public MenuItem(ItemStack itemStack, @Nullable Consumer<InventoryClickEvent> action) {
        Objects.requireNonNull(itemStack);
        this.itemStack = ItemNbt.setString(itemStack, "woody-menu", uuid.toString());;
        this.clickAction = action;
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
}
