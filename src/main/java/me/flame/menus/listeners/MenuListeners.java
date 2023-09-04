package me.flame.menus.listeners;

import lombok.val;

import me.flame.menus.components.nbt.ItemNbt;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.BaseMenu;
import me.flame.menus.menu.PaginatedMenu;

import me.flame.menus.menu.Result;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

// changed

public final class MenuListeners implements Listener {
    private static final EnumSet<InventoryAction> TAKE = EnumSet.of(
            InventoryAction.PICKUP_ONE,
            InventoryAction.PICKUP_SOME,
            InventoryAction.PICKUP_HALF,
            InventoryAction.PICKUP_ALL,
            InventoryAction.COLLECT_TO_CURSOR,
            InventoryAction.HOTBAR_SWAP,
            InventoryAction.MOVE_TO_OTHER_INVENTORY
    );

    private static final EnumSet<InventoryAction> PLACE = EnumSet.of(
            InventoryAction.PLACE_ONE,
            InventoryAction.PLACE_SOME,
            InventoryAction.PLACE_ALL
    );

    private static final EnumSet<InventoryAction> SWAP = EnumSet.of(
            InventoryAction.HOTBAR_SWAP,
            InventoryAction.SWAP_WITH_CURSOR,
            InventoryAction.HOTBAR_MOVE_AND_READD
    );

    private static final EnumSet<InventoryAction> DROP = EnumSet.of(
            InventoryAction.DROP_ONE_SLOT,
            InventoryAction.DROP_ALL_SLOT,
            InventoryAction.DROP_ONE_CURSOR,
            InventoryAction.DROP_ALL_CURSOR
    );

    private final Plugin plugin;

    public MenuListeners(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final InventoryType player = InventoryType.PLAYER;
    private static final InventoryAction otherInv = InventoryAction.MOVE_TO_OTHER_INVENTORY;
    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        Inventory inv = event.getInventory();

        InventoryHolder holder = inv.getHolder();
        if (!(holder instanceof BaseMenu<?>)) return;

        BaseMenu<?> m = (BaseMenu<?>) holder;
        Inventory ci = event.getClickedInventory();
        InventoryAction action = event.getAction();

        if (ci == null) {
            val outsideClick = m.getOutsideClickAction();
            if (outsideClick != null) outsideClick.accept(event);
            return;
        }
        Objects.requireNonNull(ci);

        executeActions(event, m, inv, ci);
        executeMenuItem(event, event.getCurrentItem(), ci, inv, action, m, event.getSlot());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGuiDrag(final @NotNull InventoryDragEvent event) {
        val inventory = event.getInventory();
        val holder = inventory.getHolder();
        if (!(holder instanceof BaseMenu<?>)) return;
        val menu = (BaseMenu<?>) holder;

        val rawSlots = event.getRawSlots();
        if (!menu.areItemsPlaceable() || isDraggingOnGui(inventory, rawSlots))
            event.setResult(Event.Result.DENY);
        val dragAction = menu.getDragAction();
        if (dragAction != null) dragAction.accept(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGuiClose(final @NotNull InventoryCloseEvent e) {
        val holder = e.getInventory().getHolder();
        if (!(holder instanceof BaseMenu<?>)) return;
        val menu = (BaseMenu<?>) holder;

        Result result = Result.allowed();
        val closeAction = menu.getCloseAction();
        if (!menu.isUpdating() && closeAction != null) closeAction.accept(e, result);

        if (result.equals(Result.denied())) {
            SCHEDULER.runTaskLater(plugin, () -> menu.open(e.getPlayer()), 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGuiOpen(final @NotNull InventoryOpenEvent event) {
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

    private void executeActions(final @NotNull InventoryClickEvent event,
                                   final @NotNull BaseMenu<?> m,
                                   final @NotNull Inventory inv,
                                   final Inventory ci) {
        val topClick = m.getTopClickAction();
        val bottomClick = m.getBottomClickAction();
        val defaultClick = m.getClickAction();

        if (topClick != null && inv.equals(ci)) topClick.accept(event);
        else if (bottomClick != null && event.getView().getBottomInventory().equals(ci)) bottomClick.accept(event);
        else if (defaultClick != null) defaultClick.accept(event);
    }

    private void executeMenuItem(final @NotNull InventoryClickEvent event,
                                 final @Nullable ItemStack it,
                                 final @NotNull Inventory ci,
                                 final @NotNull Inventory inv,
                                 final @NotNull InventoryAction action,
                                 final @NotNull BaseMenu<?> m,
                                 final int num) {
        if (modifierDetected(m, ci, action, ci.getType(), inv.getType()))
            event.setResult(Event.Result.DENY);

        MenuItem menuItem = (m instanceof PaginatedMenu)
                ? (m.get(num).orElse(((PaginatedMenu) m).getFromPageItems(num)))
                : (m.getItem(num));

        if (it == null || menuItem == null) return;
        final String nbt = ItemNbt.getString(it, "woody-menu");
        if (nbt == null || !nbt.equals(menuItem.getUniqueId().toString())) return;

        menuItem.click(event);
    }

    private boolean modifierDetected(
            BaseMenu<?> m,
            Inventory ci,
            InventoryAction action,
            InventoryType ciType,
            InventoryType invType) {
        boolean unremovable = !m.areItemsRemovable();
        return ((!m.areItemsPlaceable() && isPlaceItemEvent(ci, action, ciType, invType)) ||
                (unremovable && isTakeItemEvent(ci, action, ciType, invType)) ||
                (!m.areItemsSwappable() && isSwapItemEvent(ci, action, ciType, invType)) ||
                (unremovable && isDropItemEvent(ci, action, invType)) ||
                (!m.areItemsCloneable() && isOtherEvent(ci, action, invType)));
    }
}
