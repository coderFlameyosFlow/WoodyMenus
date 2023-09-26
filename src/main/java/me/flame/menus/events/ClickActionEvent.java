package me.flame.menus.events;

import me.flame.menus.items.MenuItem;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class ClickActionEvent extends InventoryInteractEvent {
    private static final HandlerList handlers = new HandlerList();
    private final ClickType click;
    private final InventoryAction action;
    private final InventoryView view;
    private final InventoryType.SlotType slot_type;
    private final int whichSlot;
    private final int rawSlot;
    private ItemStack current;
    private MenuItem currentItem;

    public ClickActionEvent(@NotNull InventoryClickEvent e) {
        super(e.getView());
        this.view = e.getView();
        this.slot_type = e.getSlotType();
        this.rawSlot = e.getRawSlot();
        this.whichSlot = e.getSlot();
        this.click = e.getClick();
        this.action = e.getAction();
    }

    public ClickActionEvent(@NotNull InventoryView view, @NotNull InventoryType.SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action) {
        super(view);
        this.view = view;
        this.slot_type = type;
        this.rawSlot = slot;
        this.whichSlot = view.convertSlot(slot);
        this.click = click;
        this.action = action;
    }

    public ClickActionEvent(@NotNull InventoryView view, @NotNull InventoryType.SlotType type, int rawSlot, int slot, @NotNull ClickType click, ItemStack current, @NotNull InventoryAction action) {
        super(view);
        this.view = view;
        this.slot_type = type;
        this.rawSlot = rawSlot;
        this.whichSlot = slot;
        this.click = click;
        this.action = action;
        this.current = current;
    }

    public Player getPlayer() {
        return (Player) view.getPlayer();
    }

    public MenuItem getCursorItem() {
        return MenuItem.of(view.getCursor());
    }

    /**
     * Gets the type of slot that was clicked.
     *
     * @return the slot type
     */
    @NotNull
    public InventoryType.SlotType getSlotType() {
        return slot_type;
    }

    /**
     * Gets the current ItemStack on the cursor.
     *
     * @return the cursor ItemStack
     */
    @Nullable
    public ItemStack getCursor() {
        return view.getCursor();
    }

    /**
     * Gets the ItemStack currently in the clicked slot.
     *
     * @return the item in the clicked
     */
    @Nullable
    public ItemStack getCurrentItem() {
        if (slot_type == InventoryType.SlotType.OUTSIDE) {
            return current;
        }
        return view.getItem(rawSlot);
    }

    /**
     * Gets the ItemStack currently in the clicked slot.
     *
     * @return the item in the clicked
     */
    @Nullable
    public MenuItem getCurrentMenuItem() {
        if (slot_type == InventoryType.SlotType.OUTSIDE) {
            return currentItem;
        }
        return MenuItem.of(view.getItem(rawSlot));
    }

    /**
     * Gets whether the ClickType for this event represents a right
     * click.
     *
     * @return true if the ClickType uses the right mouse button.
     * @see ClickType#isRightClick()
     */
    public boolean isRightClick() {
        return click.isRightClick();
    }

    /**
     * Gets whether the ClickType for this event represents a left
     * click.
     *
     * @return true if the ClickType uses the left mouse button.
     * @see ClickType#isLeftClick()
     */
    public boolean isLeftClick() {
        return click.isLeftClick();
    }

    /**
     * Gets whether the ClickType for this event indicates that the key was
     * pressed down when the click was made.
     *
     * @return true if the ClickType uses Shift or Ctrl.
     * @see ClickType#isShiftClick()
     */
    public boolean isShiftClick() {
        return click.isShiftClick();
    }

    /**
     * Sets the ItemStack currently in the clicked slot.
     *
     * @param stack the item to be placed in the current slot
     */
    public void setCurrentItem(@Nullable ItemStack stack) {
        if (slot_type == InventoryType.SlotType.OUTSIDE) {
            current = stack;
        } else {
            view.setItem(rawSlot, stack);
        }
    }

    /**
     * Sets the ItemStack currently in the clicked slot.
     *
     * @param stack the item to be placed in the current slot
     */
    public void setCurrentItem(@Nullable MenuItem stack) {
        if (stack == null) return;

        ItemStack item = stack.getItemStack();
        if (slot_type == InventoryType.SlotType.OUTSIDE) {
            currentItem = stack;
            return;
        }
        view.setItem(rawSlot, item);
    }

    /**
     * Gets the inventory corresponding to the clicked slot.
     *
     * @see InventoryView#getInventory(int)
     * @return inventory, or null if clicked outside
     */
    @Nullable
    public Inventory getClickedInventory() {
        return view.getInventory(rawSlot);
    }

    /**
     * The slot number that was clicked, ready for passing to
     * {@link Inventory#getItem(int)}. Note that there may be two slots with
     * the same slot number, since a view links two different inventories.
     *
     * @return The slot number.
     */
    public int getSlot() {
        return whichSlot;
    }

    /**
     * The raw slot number clicked, ready for passing to {@link InventoryView
     * #getItem(int)} This slot number is unique for the view.
     *
     * @return the slot number
     */
    public int getRawSlot() {
        return rawSlot;
    }

    /**
     * Gets the InventoryAction that triggered this event.
     * <p>
     * This action cannot be changed, and represents what the normal outcome
     * of the event will be. To change the behavior of this
     * InventoryClickEvent, changes must be manually applied.
     *
     * @return the InventoryAction that triggered this event.
     */
    @NotNull
    public InventoryAction getAction() {
        return action;
    }

    /**
     * Gets the ClickType for this event.
     * <p>
     * This is insulated against changes to the inventory by other plugins.
     *
     * @return the type of inventory click
     */
    @NotNull
    public ClickType getClick() {
        return click;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
