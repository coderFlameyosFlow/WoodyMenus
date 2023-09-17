package me.flame.menus.listeners;

import lombok.val;

import me.flame.menus.components.nbt.ItemNbt;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.BaseMenu;
import me.flame.menus.menu.Menus;
import me.flame.menus.menu.Result;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

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

    private final Plugin plugin = Menus.plugin();

    private static final InventoryType PLAYER = InventoryType.PLAYER;
    private static final InventoryAction OTHER_INV = InventoryAction.MOVE_TO_OTHER_INVENTORY;
    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
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

        if (modifierDetected(m, action, ci.getType(), inv.getType()))
            event.setResult(Event.Result.DENY);
        executeActions(event, m, inv, ci);
        executeItem(event, event.getCurrentItem(), m, event.getSlot());
    }

    @EventHandler
    public void onGuiDrag(InventoryDragEvent event) {
        val inventory = event.getInventory();
        val holder = inventory.getHolder();
        if (!(holder instanceof BaseMenu<?>)) return;
        val menu = (BaseMenu<?>) holder;

        val rawSlots = event.getRawSlots();
        if (!menu.areItemsPlaceable() || isDraggingOnGui(inventory, rawSlots))
            event.setResult(Event.Result.DENY);
        val dragAction = menu.getDragAction();
        dragAction.accept(event);
    }

    @EventHandler
    public void onGuiClose(InventoryCloseEvent e) {
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

    @EventHandler
    public void onGuiOpen(InventoryOpenEvent event) {
        val holder = event.getInventory().getHolder();
        if (!(holder instanceof BaseMenu<?>)) return;
        val menu = (BaseMenu<?>) holder;

        val openAction = menu.getOpenAction();
        if (!menu.isUpdating()) openAction.accept(event);
    }

    private static boolean isTakeItemEvent(InventoryAction action, InventoryType ciType, InventoryType type) {
        if (ciType == PLAYER || type == PLAYER) return false;
        return action == OTHER_INV || TAKE.contains(action);
    }

    private static boolean isPlaceItemEvent(InventoryAction action, InventoryType ciType, InventoryType type) {
        if (action == OTHER_INV && ciType == PLAYER && type != ciType) return true;
        return (ciType != PLAYER && type != PLAYER) || PLACE.contains(action);
    }


    private static boolean isSwapItemEvent(InventoryAction action, InventoryType ciType, InventoryType type) {
        return (ciType != PLAYER && type != PLAYER) && SWAP.contains(action);
    }

    private static boolean isDropItemEvent(InventoryAction action, InventoryType type) {
        return (type != PLAYER) && DROP.contains(action);
    }

    private static boolean isOtherEvent(InventoryAction action, InventoryType type) {
        return isOtherAction(action) && (type != PLAYER);
    }
    private static boolean isDraggingOnGui(Inventory inventory, Set<Integer> rawSlots) {
        final int topSlots = inventory.getSize();
        for (int slot : rawSlots) if (slot < topSlots) return true;
        return false;
    }

    private static boolean isOtherAction(final InventoryAction action) {
        return action == InventoryAction.CLONE_STACK || action == InventoryAction.UNKNOWN;
    }

    private static void executeActions(InventoryClickEvent event, BaseMenu<?> m, Inventory inv, Inventory ci) {
        Consumer<InventoryClickEvent> topClick = m.getTopClickAction(),
                bottomClick = m.getBottomClickAction(), defaultClick = m.getClickAction();

        if (inv.equals(ci)) topClick.accept(event);
        else if (event.getView().getBottomInventory().equals(ci)) bottomClick.accept(event);

        defaultClick.accept(event);
    }

    private static void executeItem(InventoryClickEvent event, ItemStack it, BaseMenu<?> m, int num) {
        MenuItem menuItem = m.getItem(num);

        if (it == null || menuItem == null) return;
        final String nbt = ItemNbt.getString(it, "woody-menu");
        if (nbt == null || !nbt.equals(menuItem.getUniqueId().toString())) return;

        menuItem.click(event);
    }

    private static boolean modifierDetected(BaseMenu<?> m, InventoryAction action,
                          InventoryType ciType, InventoryType invType) {
        boolean unremovable = !m.areItemsRemovable();
        return ((!m.areItemsPlaceable() && isPlaceItemEvent(action, ciType, invType)) ||
                (unremovable && isTakeItemEvent(action, ciType, invType)) ||
                (!m.areItemsSwappable() && isSwapItemEvent(action, ciType, invType)) ||
                (unremovable && isDropItemEvent(action, invType)) ||
                (!m.areItemsCloneable() && isOtherEvent(action, invType)));
    }
}
