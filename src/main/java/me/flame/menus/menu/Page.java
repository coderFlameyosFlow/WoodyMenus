package me.flame.menus.menu;

import com.google.common.collect.ImmutableSet;
import lombok.Setter;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.iterator.PageIterator;

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
    private final Map<Integer, MenuItem> itemMap;

    @NotNull
    private final PageIterator iterator = new PageIterator(IterationDirection.HORIZONTAL, this);
    
    private final int size, rows;

    private @Setter boolean dynamicSizing;

    private Page(final @NotNull PaginatedMenu holder) {
        this.holder = holder;
        this.size = holder.size;
        this.rows = holder.rows;
        this.itemMap = new HashMap<>(size);
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
        final Set<Integer> occupiedSlots = itemMap.keySet();

        int slot = 0;
        for (final ItemStack guiItem : items) {
            if (slot >= size) {
                notAddedItems.add(guiItem);
                continue;
            }

            while (occupiedSlots.contains(slot)) {
                slot++;
            }

            itemMap.put(slot, MenuItem.of(guiItem));
            slot++;
        }

        if (dynamicSizing && !notAddedItems.isEmpty() && (this.rows < 6 && holder.getType() == MenuType.CHEST)) {
            holder.recreateInventory();
            holder.update();
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
        final Set<Integer> occupiedSlots = itemMap.keySet();

        int slot = 0;
        for (final MenuItem guiItem : items) {
            if (slot >= size) {
                notAddedItems.add(guiItem);
                continue;
            }

            while (occupiedSlots.contains(slot)) {
                slot++;
            }

            itemMap.put(slot, guiItem);
            slot++;
        }

        if (dynamicSizing && !notAddedItems.isEmpty() && (this.rows < 6 && holder.getType() == MenuType.CHEST)) {
            holder.recreateInventory();
            holder.update();
            return this.addItem(notAddedItems.toArray(new MenuItem[0]));
        }
        return true;
    }

    /**
     * Set the item at the specified slot in the page.
     * @param slot the slot to set
     * @param item the item to set
     */
    public void setItem(int slot, MenuItem item) {
        itemMap.put(slot, item);
    }

    /**
     * Set the item at the specified slot in the page.
     * @param slot the slot to set
     * @param item the item to set
     */
    public void setItem(int slot, ItemStack item) {
        itemMap.put(slot, MenuItem.of(item));
    }

    /**
     * Set the item at the specified slot in the page.
     * @param slot the slot to set
     * @param item the item to set
     */
    public void setItem(Slot slot, MenuItem item) {
        if (!slot.isSlot()) return;
        itemMap.put(slot.slot, item);
    }

    /**
     * Set the item at the specified slot in the page.
     * @param slot the slot to set
     * @param item the item to set
     */
    public void setItem(Slot slot, ItemStack item) {
        if (!slot.isSlot()) return;
        itemMap.put(slot.slot, MenuItem.of(item));
    }

    /**
     * Remove every specified item from the page.
     * @param items the items to remove
     */
    public void removeItem(MenuItem... items) {
        Set<MenuItem> slots = ImmutableSet.copyOf(items);

        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = itemMap.get(i);
            if (item != null && slots.contains(item)) itemMap.remove(i);
        }
    }

    /**
     * Remove every specified item from the page.
     * @param items the items to remove
     */
    public void removeItem(ItemStack... items) {
        Set<ItemStack> slots = ImmutableSet.copyOf(items);

        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = itemMap.get(i);
            if (item != null && slots.contains(item.getItemStack())) itemMap.remove(i);
        }
    }

    /**
     * Remove every specified item from the page that matches the given predicate (description).
     * @param itemDescription the predicate to check
     */
    public void removeItem(Predicate<MenuItem> itemDescription) {
        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = itemMap.get(i);
            if (item != null && itemDescription.test(item)) itemMap.remove(i);
        }
    }

    /**
     * Remove the specified item from the page that matches the slot.
     * @apiNote recommended to use {@link #removeItem(int)} or this method; O(1) constant-time baby!
     * @param slot the slot
     * @return the removed item
     */
    public MenuItem removeItem(Slot slot) {
        return itemMap.remove(slot.slot);
    }

    /**
     * Remove the specified item from the page that matches the slot.
     * @apiNote recommended to use {@link #removeItem(Slot)} or this method; O(1) constant-time baby!
     * @param slot the slot
     * @return the removed item
     */
    public MenuItem removeItem(int slot) {
        return itemMap.remove(slot);
    }

    /**
     * Get the item at the specified slot in the page.
     * @apiNote recommended to use {@link #getItem(int)} or this method; O(1) constant-time baby!
     * @param slot the slot to check
     * @return the item or null
     */
    public MenuItem getItem(Slot slot) {
        return itemMap.get(slot.slot);
    }

    /**
     * Get the item at the specified slot in the page.
     * @apiNote recommended to use {@link #getItem(Slot)} or this method; O(1) constant-time baby!
     * @param slot the slot to check
     * @return the item or null
     */
    public MenuItem getItem(int slot) {
        return itemMap.get(slot);
    }

    /**
     * Get the item that matches the given predicate (description).
     * @param itemDescription the predicate to check
     * @return the item or null
     */
    public @Nullable MenuItem getItem(Predicate<MenuItem> itemDescription) {
        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = itemMap.get(i);
            if (item != null && itemDescription.test(item)) return item;
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
        return Optional.ofNullable(itemMap.get(slot.slot));
    }

    /**
     * Get an Optional of the item at the specified slot in the page.
     * @apiNote guaranteed to not be null, guaranteed to save your life for dealing with heavy nullability
     * @param slot the slot to check
     * @return the item
     */
    public Optional<MenuItem> get(int slot) {
        return Optional.ofNullable(itemMap.get(slot));
    }

    /**
     * Get an Optional of the item that matches the given predicate (description).
     * @param itemDescription the predicate to check
     * @return the item
     */
    public Optional<MenuItem> get(Predicate<MenuItem> itemDescription) {
        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = itemMap.get(i);
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
        itemMap.clear();
    }

    /**
     * Get a stream of every value in the map.
     * @return the stream
     */
    public Stream<MenuItem> stream() {
        return itemMap.values().stream();
    }

    /**
     * Get a <strong>PARALLEL</strong> stream of every value in the map.
     * @return the parallel stream
     */
    public Stream<MenuItem> parallelStream() {
        return itemMap.values().parallelStream();
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
        return itemMap.get(slot) != null;
    }

    /**
     * Check if the page has an item at the specified slot.
     * @param slot the slot
     * @return true if there is an item
     */
    public boolean hasItem(Slot slot) {
        if (!slot.isSlot()) return false;
        return itemMap.get(slot.slot) != null;
    }
}