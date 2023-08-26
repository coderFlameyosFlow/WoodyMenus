package me.flame.menus.listeners;

import lombok.val;

import me.flame.menus.menu.BaseMenu;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import org.jetbrains.annotations.NotNull;

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
}
