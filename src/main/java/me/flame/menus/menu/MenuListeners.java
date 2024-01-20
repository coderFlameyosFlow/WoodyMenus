package me.flame.menus.menu;

import lombok.AllArgsConstructor;
import me.flame.menus.components.nbt.ItemNbt;
import me.flame.menus.events.ClickActionEvent;
import me.flame.menus.events.BeforeAnimatingEvent;
import me.flame.menus.events.PageChangeEvent;
import me.flame.menus.items.MenuItem;

import me.flame.menus.menu.animation.Animation;

import me.flame.menus.util.ItemResponse;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.*;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import org.jetbrains.annotations.NotNull;

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

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory inventory = view.getTopInventory();
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof Menu)) return;
        int slot = event.getSlot(), raw = event.getRawSlot();

        Inventory clickedInventory = event.getClickedInventory();
        InventoryType.SlotType type = event.getSlotType();
        ClickType click = event.getClick();
        InventoryAction action = event.getAction();
        ItemStack current = event.getCurrentItem();
        ClickActionEvent clicked = new ClickActionEvent(view, type, raw, slot, click, current, action);

        Menu menu = ((Menu) holder);
        if (clickedInventory == null) {
            menu.outsideClickAction.accept(clicked);
            return;
        }

        ItemResponse response = menu.slotActions[slot];
        if (response != null) response.apply(slot, clicked);

        if (modifierDetected(menu, action, clickedInventory.getType(), inventory.getType()))
            clicked.setResult(Event.Result.DENY);
        executeActions(clicked, view, menu, inventory, clickedInventory);
        executeItem(clicked, menu, current, (Player) event.getWhoClicked(), slot);
    }

    @EventHandler
    public void onGuiClose(@NotNull InventoryCloseEvent event) {
        Inventory inventory =  event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof Menu)) return;
        Menu menu = ((Menu) holder);

        Result result = Result.allowed();
        if (!menu.updating) menu.closeAction.accept(event, result);
        if (result.isDenied()) {
            Menu.SCHEDULER.runTaskLater(plugin, () -> menu.open(event.getPlayer()), 1);
            return;
        }
        if (menu.hasAnimationsStarted && inventory.getViewers().isEmpty()) {
            menu.animations.forEach(Animation::stop);
            menu.hasAnimationsStarted = false;
        }
    }

    @EventHandler
    public void onGuiDrag(@NotNull InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof Menu)) return;
        Menu menu = ((Menu) holder);

        if (!menu.areItemsPlaceable() || isDraggingOnGui(menu.size, event.getRawSlots()))
            event.setResult(Event.Result.DENY);
        menu.dragAction.accept(event);
    }

    @EventHandler
    public void onGuiOpen(@NotNull InventoryOpenEvent event) {
        InventoryView view = event.getView();
        InventoryHolder holder = view.getTopInventory().getHolder();
        if (!(holder instanceof Menu)) return;
        Menu menu = ((Menu) holder);

        menu.update();

        checkAnimations(event, menu);

        if (!menu.updating) menu.openAction.accept(event);
    }

    private static void checkAnimations(@NotNull InventoryOpenEvent event, @NotNull Menu menu) {
        if (menu.hasAnimations() && !menu.hasAnimationsStarted) {
            BeforeAnimatingEvent animatingEvent = new BeforeAnimatingEvent((Player) event.getPlayer(), menu);
            menu.onAnimate.accept(animatingEvent);
            if (animatingEvent.isCancelled()) return;

            menu.animations.forEach(Animation::start);
            menu.hasAnimationsStarted = true;
        }
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

    private static boolean isDraggingOnGui(int size, @NotNull Iterable<Integer> rawSlots) {
        for (int slot : rawSlots) if (slot < size) return true;
        return false;
    }

    private static boolean isOtherAction(final InventoryAction action) {
        return action == InventoryAction.CLONE_STACK || action == InventoryAction.UNKNOWN;
    }

    private static void executeActions(ClickActionEvent event,
                                       InventoryView view,
                                       Menu menu,
                                       @NotNull Inventory inventory,
                                       Inventory clickedInventory) {
        if (inventory.equals(clickedInventory)) {
            menu.topClickAction.accept(event);
        } else if (view.getBottomInventory().equals(clickedInventory)) {
            menu.bottomClickAction.accept(event);
        }
        menu.clickAction.accept(event);
    }

    private static void executeItem(ClickActionEvent actionEvent, Menu menu, ItemStack it, Player player, int slot) {
        MenuItem menuItem;
        if (it == null || (menuItem = menu.getItem(slot)) == null) return;

        final String nbt = ItemNbt.getString(it, "woody-menu");
        if (nbt == null || !nbt.equals(menuItem.getUniqueId().toString())) return;

        if (menuItem.isOnCooldown(player)) return;
        CompletableFuture<ActionResponse> response = menuItem.click(slot, actionEvent);
        response.thenAccept(e -> handleRetry(slot, menuItem, actionEvent, e));

        if (menu instanceof PaginatedMenu) handlePaginatedMenu((PaginatedMenu) menu, player, slot);
    }

    @SuppressWarnings("UnusedReturnValue")
    private static boolean handlePaginatedMenu(@NotNull PaginatedMenu menu, Player player, int slot) {
        boolean nextPage = slot == menu.getNextItemSlot(), previousPage = slot == menu.getPreviousItemSlot();
        if (!nextPage && !previousPage) return false;

        int newNumber = menu.getCurrentPageNumber(), oldNumber = newNumber - 1;
        ItemData oldPage = menu.getPage(oldNumber), currentPage = menu.data;

        // page has changed by now, execute page action
        PageChangeEvent event = new PageChangeEvent(menu, oldPage, currentPage, player, newNumber, oldNumber);
        menu.onPageChange.accept(event);

        // if cancelled go back to the page it was on.
        if (event.isCancelled()) return (nextPage) ? menu.previous() : menu.next();
        return true;
    }

    private static CompletableFuture<ActionResponse> handleRetry(int num, MenuItem menuItem, ClickActionEvent actionEvent, @NotNull ActionResponse response) {
        if (response.isRetry()) {
            CompletableFuture<ActionResponse> clicked = menuItem.click(num, actionEvent);
            return menuItem.isAsync()
                    ? clicked.thenComposeAsync(e -> handleRetry(num, menuItem, actionEvent, response))
                    : clicked.thenCompose(e -> handleRetry(num, menuItem, actionEvent, response));
        }
        return CompletableFuture.completedFuture(response);
    }

    private static boolean modifierDetected(@NotNull IMenu menu, InventoryAction action, InventoryType ciType, InventoryType invType) {
        if (menu.allModifiersAdded()) return true;

        boolean irremovable;
        return ((!menu.areItemsPlaceable() && isPlaceItemEvent(action, ciType, invType)) ||
                ((irremovable = !menu.areItemsRemovable()) && isTakeItemEvent(action, ciType, invType)) ||
                (!menu.areItemsSwappable() && isSwapItemEvent(action, ciType, invType)) ||
                (irremovable && isDropItemEvent(action, invType)) ||
                (!menu.areItemsCloneable() && isOtherEvent(action, invType)));
    }
}
