package me.flame.menus.menu;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import lombok.Setter;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.iterator.PageIterator;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings({ "unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue" })
public final class Page implements Iterable<MenuItem> {
    @NotNull
    private final PaginatedMenu holder;

    @NotNull
    private final PageIterator iterator = new PageIterator(IterationDirection.HORIZONTAL, this);

    @Setter
    private boolean dynamicSizing;

    int size, rows;

    MenuItem[] itemMap;

    private Page(final @NotNull PaginatedMenu holder) {
        this.holder = holder;
        this.size = holder.size;
        this.rows = holder.rows;
        this.itemMap = new MenuItem[size];
        this.dynamicSizing = holder.isDynamicSizing();
    }

    @Contract("_ -> new")
    static @NotNull Page of(final PaginatedMenu holder) {
        return new Page(holder);
    }

    /**
     * Add a list of items to the list of items in the menu.
     *
     * @param items varargs of itemStack stacks
     * @return the object for chaining
     */
    public boolean addItem(@NotNull final ItemStack... items) {
        final List<ItemStack> notAddedItems = new ArrayList<>();


        int slot = 0;
        for (final ItemStack guiItem : items) {
            if (slot >= size) {
                if (rows == 6) break; // save some performance
                notAddedItems.add(guiItem);
                continue;
            }

            slot = getSlot(itemMap, slot);

            try {
                itemMap[slot] = MenuItem.of(guiItem);
            } catch (IndexOutOfBoundsException e) { // slot will only get bigger when too high so exit
                break;
            }
            slot++;
        }

        if (this.dynamicSizing && !notAddedItems.isEmpty() && (this.rows < 6 && this.holder.getType() == MenuType.CHEST)) {
            this.holder.recreateInventory();
            this.holder.update();
            return this.addItem(notAddedItems.toArray(new ItemStack[0]));
        }
        return true;
    }

    /**
     * Add a list of items to the list of items in the menu.
     * @param items the items
     * @return the object for chaining
     */
    public boolean addItem(@NotNull final MenuItem... items) {
        final List<MenuItem> notAddedItems = new ArrayList<>();

        int slot = 0;
        for (final MenuItem guiItem : items) {
            if (slot >= size) {
                if (rows == 6) break; // save some performance
                notAddedItems.add(guiItem);
                continue;
            }

            slot = getSlot(itemMap, slot);

            try {
                itemMap[slot] = guiItem;
            } catch (IndexOutOfBoundsException e) { // slot will only get bigger when too high so exit
                break;
            }
            slot++;
        }

        if (dynamicSizing && !notAddedItems.isEmpty() && (this.rows < 6 && holder.getType() == MenuType.CHEST)) {
            holder.recreateInventory();
            holder.update();
            return this.addItem(notAddedItems.toArray(new MenuItem[0]));
        }
        return true;
    }

    private static boolean isSlotGreater(Collection<MenuItem> notAddedItems, int slot, int size, MenuItem guiItem) {
        if (slot < size) return false;
        notAddedItems.add(guiItem);
        return true;
    }


    private static boolean isSlotGreater(Collection<ItemStack> notAddedItems, int slot, int size, ItemStack guiItem) {
        if (slot < size) return false;
        notAddedItems.add(guiItem);
        return true;
    }

    private static int getSlot(MenuItem[] occupiedSlots, int slot) {
        while (occupiedSlots[slot] != null) {
            slot++;
        }
        return slot;
    }

    /**
     * Set the item at the specified slot in the page.
     * @param slot the slot to set
     * @param item the item to set
     */
    public void setItem(int slot, MenuItem item) {
        this.itemMap[slot] = item;
    }

    /**
     * Set the item at the specified slot in the page.
     * @param slot the slot to set
     * @param item the item to set
     */
    public void setItem(int slot, ItemStack item) {
        this.itemMap[slot] = MenuItem.of(item);
    }

    /**
     * Set the item at the specified slot in the page.
     * @param slot the slot to set
     * @param item the item to set
     */
    public void setItem(Slot slot, MenuItem item) {
        if (!slot.isSlot()) return;
        this.itemMap[slot.slot] = item;
    }

    /**
     * Set the item at the specified slot in the page.
     * @param slot the slot to set
     * @param item the item to set
     */
    public void setItem(Slot slot, ItemStack item) {
        if (!slot.isSlot()) return;
        this.itemMap[slot.slot] = MenuItem.of(item);
    }

    /**
     * Remove every specified item from the page.
     * @param items the items to remove
     */
    public void removeItem(MenuItem... items) {
        Set<MenuItem> slots = ImmutableSet.copyOf(items);

        int size = itemMap.length;
        Inventory inventory = holder.getInventory();
        for (int i = 0; i < size; i++) {
            MenuItem item = itemMap[i];
            if (item == null) continue;

            ItemStack itemStack = item.getItemStack();
            if (slots.contains(item)) {
                remove(i);
                inventory.remove(itemStack);
            }
        }
    }

    /**
     * Remove every specified item from the page.
     * @param items the items to remove
     */
    public void removeItem(ItemStack... items) {
        Set<ItemStack> slots = ImmutableSet.copyOf(items);

        int size = itemMap.length;
        Inventory inventory = holder.getInventory();
        for (int i = 0; i < size; i++) {
            MenuItem item = itemMap[i];
            if (item == null) continue;

            ItemStack itemStack = item.getItemStack();
            if (slots.contains(itemStack)) {
                remove(i);
                inventory.remove(itemStack);
            }
        }
    }

    /**
     * Remove every specified item from the page that matches the given predicate (description).
     * @param itemDescription the predicate to check
     */
    public void removeItem(Predicate<MenuItem> itemDescription) {
        int size = itemMap.length;
        for (int i = 0; i < size; i++) {
            MenuItem item = itemMap[i];
            if (item != null && itemDescription.test(item)) remove(i);
        }
    }

    /**
     * Remove the specified item from the page that matches the slot.
     * @apiNote recommended to use {@link #removeItem(int)} or this method; O(1) constant-time baby!
     * @param slot the slot
     * @return the removed item
     */
    public MenuItem removeItem(Slot slot) {
        return remove(slot.slot);
    }

    /**
     * Remove the specified item from the page that matches the slot.
     * @apiNote recommended to use {@link #removeItem(Slot)} or this method; O(1) constant-time baby!
     * @param slot the slot
     * @return the removed item
     */
    public MenuItem removeItem(int slot) {
        return remove(slot);
    }

    /**
     * Get the item at the specified slot in the page.
     * @apiNote recommended to use {@link #getItem(int)} or this method; O(1) constant-time baby!
     * @param slot the slot to check
     * @return the item or null
     */
    public @Nullable MenuItem getItem(Slot slot) {
        if (!slot.isSlot()) return null;
        try {
            return itemMap[slot.slot];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Get the item at the specified slot in the page.
     * @apiNote recommended to use {@link #getItem(Slot)} or this method; O(1) constant-time baby!
     * @param slot the slot to check
     * @return the item or null
     */
    public @Nullable MenuItem getItem(int slot) {
        try {
            return itemMap[slot];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Get the item that matches the given predicate (description).
     * @param itemDescription the predicate to check
     * @return the item or null
     */
    public @Nullable MenuItem getItem(Predicate<MenuItem> itemDescription) {
        for (MenuItem item : itemMap) {
            if (item != null && itemDescription.test(item)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Get an Optional of the item at the specified slot in the page.
     * @apiNote guaranteed to not be null, guaranteed to save your life for dealing with heavy nullability
     * @param slot the slot to check
     * @return the item
     */
    public Optional<MenuItem> get(Slot slot) {
        if (!slot.isSlot()) return Optional.empty();
        try {
            return Optional.ofNullable(itemMap[slot.slot]);
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    /**
     * Get an Optional of the item at the specified slot in the page.
     * @apiNote guaranteed to not be null, guaranteed to save your life for dealing with heavy nullability
     * @param slot the slot to check
     * @return the item
     */
    public Optional<MenuItem> get(int slot) {
        try {
            return Optional.ofNullable(itemMap[slot]);
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    /**
     * Get an Optional of the item that matches the given predicate (description).
     * @param itemDescription the predicate to check
     * @return the item
     */
    public Optional<MenuItem> get(Predicate<MenuItem> itemDescription) {
        int size = itemMap.length;
        for (MenuItem item : itemMap) {
            if (item != null && itemDescription.test(item)) return Optional.of(item);
        }
        return Optional.empty();
    }

    /**
     * Get the PageIterator for the page.
     * @return the iterator
     */
    public @NotNull PageIterator iterator() {
        return iterator;
    }

    /**
     * Get the PageIterator for the page.
     * @param direction the direction of the iteration
     * @return the iterator
     */
    public @NotNull PageIterator iterator(IterationDirection direction) {
        return new PageIterator(direction, this);
    }

    /**
     * Get the PageIterator for the page.
     * @param startingRow the starting row of the iteration
     * @param startingCol the starting column of the iteration
     * @param direction the direction of the iteration
     * @return the iterator
     */
    public PageIterator iterator(int startingRow, int startingCol, IterationDirection direction) {
        return new PageIterator(startingRow, startingCol, direction, this);
    }

    /**
     * Clear the entire page.
     */
    public void clear() {
        Arrays.fill(itemMap, null);
    }

    /**
     * Get a stream of every value in the map.
     * @return the stream
     */
    public Stream<MenuItem> stream() {
        return Arrays.stream(itemMap).parallel();
    }

    /**
     * Get a <strong>PARALLEL</strong> stream of every value in the map.
     * @return the parallel stream
     */
    public Stream<MenuItem> parallelStream() {
        return Arrays.stream(itemMap).parallel();
    }

    /**
     * The paginated menu this page belongs to.
     * @return the paginated menu
     */
    public @NotNull PaginatedMenu getHolder() {
        return holder;
    }

    /**
     * Get the size of the page. (and generally the size of menu)
     * @return the size
     */
    public int size() {
        return size;
    }

    /**
     * Get the number of rows. (And generally all the rows the menu contains)
     * @return the number of rows
     */
    public int rows() {
        return rows;
    }

    /**
     * Check if the page has an item at the specified slot.
     * @param slot the slot
     * @return true if there is an item
     */
    public boolean hasItem(int slot) {
        return itemMap[slot] != null;
    }

    /**
     * Check if the page has an item at the specified slot.
     * @param slot the slot
     * @return true if there is an item
     */
    public boolean hasItem(@NotNull Slot slot) {
        if (!slot.isSlot()) return false;
        return itemMap[slot.slot] != null;
    }

    private MenuItem remove(int index) {
        int length = this.itemMap.length;
        Preconditions.checkElementIndex(index, length);

        MenuItem item = this.itemMap[index];
        this.itemMap[index] = null;
        return item;
    }

    public void setContents(MenuItem... items) {
        this.itemMap = items;
        this.holder.update();
    }

    public List<MenuItem> getItems() {
        return Arrays.asList(itemMap);
    }
}
