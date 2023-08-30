/**
 * MIT License
 * <p>
 * Copyright (c) 2021 TriumphTeam
 * Copyright (c) 2023 FlameyosFlow
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.flame.menus.listeners;

import lombok.val;

import me.flame.menus.components.nbt.ItemNbt;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.BaseMenu;

import me.flame.menus.menu.PaginatedMenu;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

public final class MenuListeners implements Listener {
    private static final Set<InventoryAction> TAKE = EnumSet.of(InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ALL, InventoryAction.COLLECT_TO_CURSOR, InventoryAction.HOTBAR_SWAP, InventoryAction.MOVE_TO_OTHER_INVENTORY);
    private static final Set<InventoryAction> PLACE = EnumSet.of(InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.PLACE_ALL);
    private static final Set<InventoryAction> SWAP = EnumSet.of(InventoryAction.HOTBAR_SWAP, InventoryAction.SWAP_WITH_CURSOR, InventoryAction.HOTBAR_MOVE_AND_READD);
    private static final Set<InventoryAction> DROP = EnumSet.of(InventoryAction.DROP_ONE_SLOT, InventoryAction.DROP_ALL_SLOT, InventoryAction.DROP_ONE_CURSOR, InventoryAction.DROP_ALL_CURSOR);

    private static final Event.Result denied = Event.Result.DENY;
    private static final InventoryType player = InventoryType.PLAYER;
    private static final InventoryAction otherInv = InventoryAction.MOVE_TO_OTHER_INVENTORY;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof BaseMenu<?>)) return;
        BaseMenu<?> menu = (BaseMenu<?>) holder;

        Inventory ci = event.getClickedInventory();
        Inventory inv = event.getInventory();
        InventoryAction action = event.getAction();

        if (ci == null) {
            val outsideClick = menu.getOutsideClickAction();
            if (outsideClick == null) return;
            outsideClick.accept(event);
            return;
        }

        InventoryType ciType = ci.getType();
        InventoryType invType = inv.getType();

        boolean unremovable = !menu.areItemsRemovable();
        if ((!menu.areItemsPlaceable() && isPlaceItemEvent(ci, action, ciType, invType)) ||
                (unremovable && isTakeItemEvent(ci, action, ciType, invType)) ||
                (!menu.areItemsSwappable() && isSwapItemEvent(ci, action, ciType, invType)) ||
                (unremovable && isDropItemEvent(ci, action, invType)) ||
                (!menu.areItemsCloneable() && isOtherEvent(ci, action, invType)))
            event.setResult(denied);

        val topClick = menu.getOutsideClickAction();
        val bottomClick = menu.getOutsideClickAction();
        val defaultClick = menu.getOutsideClickAction();

        if (topClick != null && inv.equals(ci)) topClick.accept(event);
        else if (bottomClick != null && event.getView().getBottomInventory().equals(ci)) bottomClick.accept(event);
        else if (defaultClick != null) defaultClick.accept(event);

        MenuItem guiItem;

        // Checks whether it's a paginated menu or not
        if (menu instanceof PaginatedMenu) {
            final PaginatedMenu paginatedGui = (PaginatedMenu) menu;

            // Gets the menu item from the added items or the page items
            guiItem = paginatedGui.getItem(event.getSlot());
            if (guiItem == null) guiItem = paginatedGui.getFromPageItems(event.getSlot());

        } else {
            // The clicked GUI Item
            guiItem = menu.getItem(event.getSlot());
        }

        if (!isMenuItem(event.getCurrentItem(), guiItem)) return;

        // Executes the action of the item
        final Consumer<InventoryClickEvent> itemAction = guiItem.getClickAction();
        if (itemAction != null) itemAction.accept(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGuiDrag(final InventoryDragEvent event) {
        val inventory = event.getInventory();
        val holder = inventory.getHolder();
        if (!(holder instanceof BaseMenu<?>)) return;
        val menu = (BaseMenu<?>) holder;
        val rawSlots = event.getRawSlots();

        if (!menu.areItemsPlaceable() || isDraggingOnGui(inventory, rawSlots))
            event.setResult(denied);
        final Consumer<InventoryDragEvent> dragAction = menu.getDragAction();
        if (dragAction != null) dragAction.accept(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGuiClose(final InventoryCloseEvent event) {
        val holder = event.getInventory().getHolder();
        if (!(holder instanceof BaseMenu<?>)) return;
        val menu = (BaseMenu<?>) holder;

        val closeAction = menu.getCloseAction();
        if (!menu.isUpdating() && closeAction != null) closeAction.accept(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGuiOpen(final InventoryOpenEvent event) {
        val holder = event.getInventory().getHolder();
        if (!(holder instanceof BaseMenu<?>)) return;
        val menu = (BaseMenu<?>) holder;

        val openAction = menu.getOpenAction();
        if (!menu.isUpdating() && openAction != null) openAction.accept(event);
    }

    private boolean isTakeItemEvent(Inventory ci, InventoryAction action,
                                    InventoryType ciType, InventoryType type) {
        if (ci == null || ciType == player || type == player) return false;
        return action == otherInv || TAKE.contains(action);
    }

    private boolean isPlaceItemEvent(Inventory ci, InventoryAction action,
                                        InventoryType ciType, InventoryType type) {
        if (ci == null) return false;
        if (action == otherInv && ciType == player && type != ciType) return true;
        return (ciType != player && type != player) || PLACE.contains(action);
    }


    private boolean isSwapItemEvent(Inventory ci, InventoryAction action,
                                    InventoryType ciType, InventoryType type) {
        return (ci != null && ciType != player && type != player) && SWAP.contains(action);
    }

    private boolean isDropItemEvent(Inventory ci, InventoryAction action, InventoryType type) {
        return (ci != null || type != player) && DROP.contains(action);
    }

    private boolean isOtherEvent(Inventory ci, InventoryAction action, InventoryType type) {
        return isOtherAction(action) && (ci != null || type != player);
    }
    private boolean isDraggingOnGui(final @NotNull Inventory inventory, Set<Integer> rawSlots) {
        final int topSlots = inventory.getSize();
        for (int slot : rawSlots) if (slot < topSlots) return true;
        return false;
    }

    private boolean isOtherAction(final InventoryAction action) {
        return action == InventoryAction.CLONE_STACK || action == InventoryAction.UNKNOWN;
    }

    /**
     * Checks if the item is or not a GUI item
     *
     * @param currentItem The current item clicked
     * @param guiItem     The GUI item in the slot
     * @return Whether it is or not a GUI item
     */
    private boolean isMenuItem(@Nullable final ItemStack currentItem, @Nullable final MenuItem guiItem) {
        if (currentItem == null || guiItem == null) return false;
        final String nbt = ItemNbt.getString(currentItem, "woody-menu");
        if (nbt == null) return false;
        return nbt.equals(guiItem.getUniqueId().toString());
    }
}
