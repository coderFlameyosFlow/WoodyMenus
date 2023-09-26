package me.flame.menus.listeners;

import lombok.AllArgsConstructor;
import me.flame.menus.components.nbt.ItemNbt;
import me.flame.menus.events.ClickActionEvent;
import me.flame.menus.events.OpenMenuEvent;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.ActionResponse;
import me.flame.menus.menu.BaseMenu;
import me.flame.menus.menu.IMenu;
import me.flame.menus.menu.Result;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
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

    private static final InventoryType PLAYER = InventoryType.PLAYER;
    private static final InventoryAction OTHER_INV = InventoryAction.MOVE_TO_OTHER_INVENTORY;
    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory inventory = view.getTopInventory();
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof BaseMenu)) return;

        BaseMenu menu = ((BaseMenu) holder);
        Inventory clickedInventory = event.getClickedInventory();
        InventoryAction action = event.getAction();

        int slot = event.getSlot();
        ItemStack current = event.getCurrentItem();

        ClickActionEvent e = new ClickActionEvent(
            view, event.getSlotType(), event.getRawSlot(),
            slot, event.getClick(), current, action
        );

        if (clickedInventory == null) {
            menu.click(e, BaseMenu.Click.OUTSIDE);
            return;
        }

        if (modifierDetected(menu, action, clickedInventory.getType(), inventory.getType()))
            event.setResult(Event.Result.DENY);
        executeActions(e, view, menu, inventory, clickedInventory);
        executeItem(e, menu, current, slot);
    }

    @EventHandler
    public void onGuiDrag(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof BaseMenu)) return;
        BaseMenu menu = ((BaseMenu) holder);

        if (!menu.areItemsPlaceable() || isDraggingOnGui(inventory, event.getRawSlots()))
            event.setResult(Event.Result.DENY);
        menu.click(event);
    }

    @EventHandler
    public void onGuiClose(InventoryCloseEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();
        if (!(holder instanceof BaseMenu)) return;
        BaseMenu menu = ((BaseMenu) holder);

        Result result = Result.allowed();
        menu.click(e, result);

        if (result.equals(Result.denied()))
            SCHEDULER.runTaskLater(plugin, () -> menu.open(e.getPlayer()), 1);
    }

    @EventHandler
    public void onGuiOpen(InventoryOpenEvent event) {
        InventoryView view = event.getView();
        InventoryHolder holder = view.getTopInventory().getHolder();
        if (!(holder instanceof BaseMenu)) return;
        BaseMenu menu = ((BaseMenu) holder);

        menu.click(new OpenMenuEvent(view));
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
    private static boolean isDraggingOnGui(Inventory inventory, Iterable<Integer> rawSlots) {
        final int topSlots = inventory.getSize();
        for (int slot : rawSlots) if (slot < topSlots) return true;
        return false;
    }

    private static boolean isOtherAction(final InventoryAction action) {
        return action == InventoryAction.CLONE_STACK || action == InventoryAction.UNKNOWN;
    }

    private static void executeActions(ClickActionEvent event,
                                       InventoryView view,
                                       BaseMenu menu,
                                       Inventory inventory,
                                       Inventory clickedInventory) {
        if (inventory.equals(clickedInventory)) {
            menu.click(event, BaseMenu.Click.TOP);
        } else if (view.getBottomInventory().equals(clickedInventory)) {
            menu.click(event, BaseMenu.Click.BOTTOM);
        }
        menu.click(event, BaseMenu.Click.DEFAULT);
    }

    private static void executeItem(ClickActionEvent actionEvent, IMenu menu, ItemStack it, int num) {
        MenuItem menuItem = menu.getItem(num);

        if (it == null || menuItem == null) return;
        final String nbt = ItemNbt.getString(it, "woody-menu");
        if (nbt == null || !nbt.equals(menuItem.getUniqueId().toString())) return;

        CompletableFuture<ActionResponse> response = menuItem.click(num, actionEvent);
        response.thenAccept(e -> attemptRetry(num, menuItem, actionEvent, e));
    }

    private static void attemptRetry(int num, MenuItem menuItem, ClickActionEvent actionEvent, ActionResponse e) {
        if (e.isRetry()) {
            CompletableFuture<ActionResponse> futureResponse = menuItem.click(num, actionEvent);
            futureResponse.thenCompose(response -> handleRetry(num, menuItem, actionEvent, response));
        }
    }

    private static CompletableFuture<ActionResponse> handleRetry(int num, MenuItem menuItem, ClickActionEvent actionEvent, ActionResponse response) {
        if (response.isRetry()) {
            return menuItem.isAsync() 
            		? menuItem.click(num, actionEvent)
                    		  .thenComposeAsync(e -> handleRetry(num, menuItem, actionEvent, response))
                    : menuItem.click(num, actionEvent)
                    		  .thenCompose(e -> handleRetry(num, menuItem, actionEvent, response));
        }
        return CompletableFuture.completedFuture(response);
    }

    private static boolean modifierDetected(IMenu menu, InventoryAction action, InventoryType ciType, InventoryType invType) {
        boolean unremovable = !menu.areItemsRemovable();
        return ((!menu.areItemsPlaceable() && isPlaceItemEvent(action, ciType, invType)) ||
                (unremovable && isTakeItemEvent(action, ciType, invType)) ||
                (!menu.areItemsSwappable() && isSwapItemEvent(action, ciType, invType)) ||
                (unremovable && isDropItemEvent(action, invType)) ||
                (!menu.areItemsCloneable() && isOtherEvent(action, invType)));
    }
}
