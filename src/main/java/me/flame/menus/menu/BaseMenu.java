package me.flame.menus.menu;

import lombok.Getter;
import lombok.Setter;

import lombok.val;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.fillers.BorderFiller;
import me.flame.menus.menu.fillers.MenuFiller;
import me.flame.menus.modifiers.Modifier;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

@SuppressWarnings({
    "unused",
    "unchecked",
    "UnusedReturnValue",
    "BooleanMethodIsAlwaysInverted"
})
public class BaseMenu<M extends BaseMenu<M>> implements InventoryHolder {
    protected @Getter Inventory inventory;

    protected @Getter int rows;
    protected @Getter int size;
    private @Getter String title;
    protected @Getter boolean updating;

    private static final Material AIR = Material.AIR;

    private final EnumSet<Modifier> modifiers;
    protected final Map<Integer, MenuItem> itemMap;

    private @Getter BorderFiller borderFiller;
    private @Getter MenuFiller menuFiller;
    private @Nullable @Getter @Setter Consumer<InventoryClickEvent> outsideClickAction;
    private @Nullable @Getter @Setter Consumer<InventoryClickEvent> bottomClickAction;
    private @Nullable @Getter @Setter Consumer<InventoryClickEvent> topClickAction;
    private @Nullable @Getter @Setter Consumer<InventoryClickEvent> clickAction;
    private @Nullable @Getter @Setter Consumer<InventoryCloseEvent> closeAction;
    private @Nullable @Getter @Setter Consumer<InventoryOpenEvent> openAction;
    private @Nullable @Getter @Setter Consumer<InventoryDragEvent> dragAction;

    BaseMenu(int rows, String title, EnumSet<Modifier> modifiers) {
        this(rows, title, modifiers, true);
    }

    BaseMenu(int rows, String title, EnumSet<Modifier> modifiers, boolean colorize) {
        this.modifiers = modifiers;
        this.rows = rows;
        this.title = colorize ? translateAlternateColorCodes('&', title)
                              : title;
        this.size = rows * 9;
        this.itemMap = new LinkedHashMap<>(54);
        this.inventory = Bukkit.createInventory(this, size, title);

        this.borderFiller = BorderFiller.from(inventory);
        this.menuFiller = MenuFiller.from(inventory);
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * <p>
     * As this is the itemStack to add, it's not a menu itemStack, so it'd be converted to a MenuItem first
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M addItem(ItemStack item, boolean shouldUpdate) {
        int size = itemMap.size();
        if (size >= this.size) return (M) this;
        itemMap.put(size + 1, new MenuItem(item, null));

        if (shouldUpdate) update();
        return (M) this;
    }

    /**
     * Add a list of items to the list of items in the menu.
     * @param items varargs of itemStack stacks
     * @return the object for chaining
     */
    public M addItem(ItemStack @NotNull ... items) {
        for (ItemStack item : items) addItem(item);
        return (M) this;
    }

    /**
     * Add a list of items to the list of items in the menu.
     * @param items the items
     * @return the object for chaining
     */
    public M addItem(MenuItem @NotNull ... items) {
        for (val item : items)
            this.itemMap.put(this.itemMap.size(), item);
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M addItem(MenuItem item, boolean update) {
        int size = itemMap.size();
        if (size >= this.size) return (M) this;
        itemMap.put(size + 1, item);
        if (update) update();
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * <p>
     * As this is the itemStack to add, it's not a menu itemStack, so it'd be converted to a MenuItem first
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M setItem(ItemStack item, @NotNull Slot slot, boolean update) {
        itemMap.put(slot.getSlot(), new MenuItem(item, null));
        if (update) update();
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M setItem(MenuItem item, @NotNull Slot slot, boolean update) {
        itemMap.put(slot.getSlot(), item);
        if (update) update();
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * <p>
     * As this is the itemStack to add, it's not a menu itemStack, so it'd be converted to a MenuItem first
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M setItem(ItemStack item, int slot, boolean update) {
        itemMap.put(slot, new MenuItem(item, null));
        if (update) update();
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M setItem(MenuItem item, int slot, boolean update) {
        itemMap.put(slot, item);
        if (update) update();
        return (M) this;
    }

    /**
     * remove the itemStack from the list of items in the menu.
     * @param item the itemStack to remove
     * @return the object for chaining
     */
    public M removeItem(@NotNull final MenuItem item, boolean update) {
        for (Map.Entry<Integer, MenuItem> itemTwo : this.itemMap.entrySet()) {
            if (!item.equals(itemTwo.getValue())) continue;
            itemMap.remove(itemTwo.getKey());
            if (update) update();
            break;
        }
        return (M) this;
    }

    /**
     * remove the itemStack from the list of items in the menu.
     * @param itemStack the itemStack to remove
     * @return the object for chaining
     */
    public M removeItem(@NotNull final ItemStack itemStack, boolean update) {
        for (Map.Entry<Integer, MenuItem> itemTwo : this.itemMap.entrySet()) {
            val value = itemTwo.getValue();
            if (!value.getItemStack().equals(itemStack)) continue;
            itemMap.remove(itemTwo.getKey());
            if (update) update();
            break;
        }
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * <p>
     * As this is the itemStack to add, it's not a menu itemStack, so it'd be converted to a MenuItem first
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M addItem(ItemStack item) {
        int size = itemMap.size();
        if (size >= this.size) return (M) this;
        itemMap.put(size, new MenuItem(item, null));
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M addItem(MenuItem item) {
        int size = itemMap.size();
        if (size >= this.size) return (M) this;
        itemMap.put(size, item);
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * <p>
     * As this is the itemStack to add, it's not a menu itemStack, so it'd be converted to a MenuItem first
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M setItem(ItemStack item, @NotNull Slot slot) {
        itemMap.put(slot.getSlot(), new MenuItem(item, null));
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M setItem(MenuItem item, @NotNull Slot slot) {
        itemMap.put(slot.getSlot(), item);
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * <p>
     * As this is the itemStack to add, it's not a menu itemStack, so it'd be converted to a MenuItem first
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M setItem(ItemStack item, int slot) {
        itemMap.put(slot, new MenuItem(item, null));
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M setItem(MenuItem item, int slot) {
        itemMap.put(slot, item);
        return (M) this;
    }

    /**
     * remove the itemStack from the list of items in the menu.
     * @param item the itemStack to remove
     * @return the object for chaining
     */
    public M removeItem(@NotNull final MenuItem item) {
        for (Map.Entry<Integer, MenuItem> itemTwo : this.itemMap.entrySet()) {
            if (!item.equals(itemTwo.getValue())) continue;
            itemMap.remove(itemTwo.getKey());
            break;
        }
        return (M) this;
    }

    /**
     * remove the itemStack from the list of items in the menu.
     * @param itemStack the itemStack to remove
     * @return the object for chaining
     */
    public M removeItem(@NotNull final ItemStack itemStack) {
        for (Map.Entry<Integer, MenuItem> itemTwo : this.itemMap.entrySet()) {
            val value = itemTwo.getValue();
            if (!itemStack.equals(value.getItemStack())) continue;
            itemMap.remove(itemTwo.getKey());
            break;
        }
        return (M) this;
    }

    /**
     * get the itemStack from the list of items in the menu.
     * @param i the index of the itemStack
     * @return the itemStack or null
     */
    public @Nullable MenuItem getItem(int i) {
        try {
            return itemMap.get(i);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * get the itemStack from the list of items in the menu.
     * <p></p>
     * Usually this is the recommended way when using Java.
     * <p></p>
     * It is wrapped in an Optional which may or may not make the code cleaner and safer.
     * @param i the index of the itemStack
     * @return the optional itemStack or an empty optional
     */
    public Optional<MenuItem> getOptionalItem(int i) {
        try {
            return Optional.ofNullable(itemMap.get(i));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    /**
     * get the itemStack from the list of items in the menu.
     * @param slot the slot (row and col) of the itemStack
     * @return the itemStack or null
     */
    public @Nullable MenuItem getItem(@NotNull Slot slot) {
        try {
            return itemMap.get(slot.getSlot());
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * get the itemStack from the list of items in the menu.
     * <p></p>
     * Usually this is the recommended way when using Java.
     * <p></p>
     * It is wrapped in an Optional which may or may not make the code cleaner and safer.
     * @param slot the index of the itemStack
     * @return the optional itemStack or an empty optional
     */
    public Optional<MenuItem> getOptionalItem(@NotNull Slot slot) {
        try {
            return Optional.ofNullable(itemMap.get(slot.getSlot()));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    /**
     * get the itemStack from the list of items in the menu from the provided description of the itemStack
     * <p></p>
     * Usually this is the recommended way when using Java.
     * <p></p>
     * It is wrapped in an Optional which may or may not make the code cleaner and safer.
     * @param itemDescription the description of the itemStack
     * @return the optional itemStack or an empty optional
     */
    public Optional<MenuItem> getOptionalItem(Predicate<MenuItem> itemDescription) {
        for (Map.Entry<Integer, MenuItem> itemTwo : this.itemMap.entrySet()) {
            val value = itemTwo.getValue();
            if (!itemDescription.test(value)) continue;
            return Optional.of(value);
        }
        return Optional.empty();
    }

    /**
     * get the itemStack from the list of items in the menu from the provided description of the itemStack
     * @param itemDescription the description of the itemStack
     * @return the itemStack or null
     */
    public @Nullable MenuItem getItem(Predicate<MenuItem> itemDescription) {
        for (Map.Entry<Integer, MenuItem> itemTwo : this.itemMap.entrySet()) {
            val value = itemTwo.getValue();
            if (!itemDescription.test(value)) continue;
            return value;
        }
        return null;
    }

    /**
     * Remove all the specified items from the inventory.
     * @param itemStacks the items to remove
     * @return the object for chaining
     */
    public M removeItems(@NotNull final ItemStack... itemStacks) {
        return removeItemStacks(Arrays.asList(itemStacks));
    }

    /**
     * Remove all the specified items from the inventory.
     * @param itemStacks the items to remove
     * @return the object for chaining
     */
    public M removeItemStacks(@NotNull final List<ItemStack> itemStacks) {
        Set<ItemStack> set = new HashSet<>(itemStacks);
        for (Map.Entry<Integer, MenuItem> itemTwo : this.itemMap.entrySet()) {
            val itemStack = itemTwo.getValue().getItemStack();
            if (!set.contains(itemStack)) continue;
            itemMap.remove(itemTwo.getKey());
        }
        return (M) this;
    }

    /**
     * Remove all the specified items from the inventory.
     * @param itemStacks the items to remove
     * @return the object for chaining
     */
    public M removeItems(@NotNull final MenuItem... itemStacks) {
        return removeItems(Arrays.asList(itemStacks));
    }

    /**
     * Remove all the specified items from the inventory.
     * @param itemStacks the items to remove
     * @return the object for chaining
     */
    public M removeItems(@NotNull final List<MenuItem> itemStacks) {
        Set<MenuItem> set = new HashSet<>(itemStacks);
        for (Map.Entry<Integer, MenuItem> itemTwo : this.itemMap.entrySet()) {
            val value = itemTwo.getValue();
            if (!set.contains(value)) continue;
            itemMap.remove(itemTwo.getKey());
        }
        return (M) this;
    }

    /**
     * Add the specified items to the inventory.
     * @param expandIfFull whether to expand the inventory to add the un-added items or not
     * @param items the items to add to the inventory
     * @apiNote the expand might fail if the inventory is at 6 rows (at maximum capacity),
     * and the rest of the items will not be added.
     * @return the object for chaining
     */
    public M addItem(final boolean expandIfFull, @NotNull final MenuItem @NotNull ... items) {
        int itemsSize = this.itemMap.size();
        List<MenuItem> notAddedItems = new ArrayList<>(size);

        for (final MenuItem guiItem : items) {
            for (int slot = 0; slot < size; slot++) {
                if (this.itemMap.get(slot) != null) {
                    if (slot != size - 1) continue;
                    notAddedItems.add(guiItem);
                }
                this.itemMap.put(slot, guiItem);
                break;
            }
        }

        if (!expandIfFull || notAddedItems.isEmpty() || this.rows >= 6) return (M) this;
        this.rows++;
        this.size = rows * 9;

        this.inventory = Bukkit.createInventory(this, this.size, this.title);
        this.borderFiller = BorderFiller.from(inventory);
        this.menuFiller = MenuFiller.from(inventory);

        final int size = notAddedItems.size();
        MenuItem[] remainingItems = new MenuItem[size];

        /*
         *  "
         *   *  surely there's something wrong I can feel it,
         *   *  but no one knows I know there's a problem with this line.
         *   *  please just trust my gut. don't do this, it's bad quality!
         *   *  your code can & will be ruined, and your code will never work!!!
         *  "
         *      - Best IDE, Intellij 26/8/2023 @ Saturday @ 6:32am
         *              (while I was staying up and my body
         *               was begging me to go to sleep)
         */
        @SuppressWarnings("DataFlowIssue")
        final MenuItem[] newItems = (MenuItem[]) notAddedItems.toArray();

        System.arraycopy(newItems, 0, remainingItems, 0, size);
        return this.addItem(true, remainingItems);
    }


    /**
     * Update the inventory which recreates the items on default
     * @return the object for chaining
     */
    public M update() {
        recreateItems();
        for (HumanEntity viewer : new ArrayList<>(inventory.getViewers()))
            ((Player) viewer).updateInventory();
        return (M) this;
    }

    /**
     * Update the inventory with the title (RE-OPENS THE INVENTORY)
     * @param title the new title
     * @return the object for chaining
     */
    public M updateTitle(String title) {
        String colorizedTitle = translateAlternateColorCodes('&', title);
        Inventory updatedInventory = Bukkit.createInventory(this, size, colorizedTitle);
        this.title = colorizedTitle;
        this.inventory = updatedInventory;

        this.updating = true;
        for (HumanEntity viewer : new ArrayList<>(inventory.getViewers()))
            viewer.openInventory(updatedInventory);
        this.updating = false;
        return (M) this;
    }

    protected void recreateItems() {
        int itemsLength = itemMap.size(), i = 0;
        if (itemsLength == 0) return;

        // while instead of for to make sure it can fill up empty slots
        // if it works don't change it.
        while (i <= itemsLength) {
            ItemStack item = itemMap.get(i).getItemStack();
            if (item == null || item.getType() == AIR) {
                i++; continue;
            }
            inventory.setItem(i, item);
            i++;
        }
    }

    /**
     * Open the inventory for the provided player.
     * @apiNote Will not work if the player is sleeping.
     * @param entity the provided entity to open the inventory for.
     * @return the object for chaining
     */
    public M open(@NotNull HumanEntity entity) {
        if (entity.isSleeping()) return (M) this;

        this.updating = true;
        update();
        this.updating = false;

        entity.openInventory(inventory);
        return (M) this;
    }

    public boolean addModifier(Modifier modifier) {
        return modifiers.add(modifier);
    }

    public boolean removeModifier(Modifier modifier) {
        return modifiers.remove(modifier);
    }

    public boolean addAllModifiers() {
        return modifiers.addAll(Modifier.ALL);
    }

    public void removeAllModifiers() {
        Modifier.ALL.forEach(modifiers::remove);
    }

    public boolean areItemsPlaceable() {
        return !modifiers.contains(Modifier.DISABLE_ITEM_ADD);
    }

    public boolean areItemsRemovable() {
        return !modifiers.contains(Modifier.DISABLE_ITEM_REMOVAL);
    }

    public boolean areItemsSwappable() {
        return !modifiers.contains(Modifier.DISABLE_ITEM_SWAP);
    }

    public boolean areItemsCloneable() {
        return !modifiers.contains(Modifier.DISABLE_ITEM_CLONE);
    }

    public void updateItem(final int slot, @NotNull final ItemStack itemStack) {
        final MenuItem guiItem = itemMap.get(slot);

        if (guiItem == null) {
            itemMap.put(slot, new MenuItem(itemStack, null));
            return;
        }

        guiItem.setItemStack(itemStack);
        itemMap.put(slot, guiItem);
    }

    /**
     * Alternative {@link #updateItem(int, ItemStack)} that takes <i>ROWS</i> and <i>COLUMNS</i> instead of slots.
     *
     * @param slot      The row and col of the slot.
     * @param itemStack The {@link ItemStack} to replace in the original one in the {@link MenuItem}.
     */
    public void updateItem(@NotNull Slot slot, @NotNull final ItemStack itemStack) {
        final int slotNum = slot.getSlot();
        final MenuItem guiItem = itemMap.get(slotNum);

        if (guiItem == null) {
            itemMap.put(slotNum, new MenuItem(itemStack, null));
            return;
        }

        guiItem.setItemStack(itemStack);
        itemMap.put(slotNum, guiItem);
    }


    /**
     * Alternative {@link #updateItem(int, ItemStack)} that takes <i>ROWS</i> and <i>COLUMNS</i> instead of slots. also using MenuItem
     *
     * @param slot      The row and col of the slot.
     * @param itemStack The {@link ItemStack} to replace in the original one in the {@link MenuItem}.
     */
    public void updateItem(@NotNull Slot slot, @NotNull final MenuItem itemStack) {
        final int slotNum = slot.getSlot();
        final MenuItem guiItem = itemMap.get(slotNum);

        if (guiItem == null) {
            itemMap.put(slotNum, itemStack);
            return;
        }

        guiItem.setItemStack(itemStack.getItemStack());
        itemMap.put(slotNum, guiItem);
    }
}
