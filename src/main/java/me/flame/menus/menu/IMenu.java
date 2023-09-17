package me.flame.menus.menu;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.iterator.MenuIterator;
import me.flame.menus.modifiers.Modifier;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IMenu<M extends IMenu<M>> extends Iterable<MenuItem>, InventoryHolder {
    /**
     * Get a LIST iterator of the items in the menu
     * @return the list iterator
     */
    MenuIterator iterator();

    /**
     * Get a LIST iterator of the items in the menu
     * @param direction the direction of the iteration
     * @return the list iterator
     */
    MenuIterator iterator(IterationDirection direction);

    /**
     * Get a LIST iterator of the items in the menu
     * @param direction the direction of the iteration
     * @return the list iterator
     */
    MenuIterator iterator(int startingRow, int startingCol, IterationDirection direction);

    /**
     * Get a stream loop of the items in the menu
     * <p>This is streaming on LinkedHashMap#values()</p>
     * @return the stream
     */
    Stream<MenuItem> stream();

    /**
     * Get a parallel stream loop of the items in the menu
     * <p>This is streaming on LinkedHashMap#values()</p>
     * @apiNote use this if you want to do things in parallel, and you're sure of how to use it, else it might even get slower than the normal .stream()
     * @return the stream
     */
    Stream<MenuItem> parallelStream();

    void recreateInventory();

    List<HumanEntity> getViewers();

    /**
     * Add a list of items to the list of items in the menu.
     * @param items varargs of itemStack stacks
     * @return the object for chaining
     */
    M addItem(@NotNull final ItemStack... items);

    /**
     * Add a list of items to the list of items in the menu.
     * @param items the items
     * @return the object for chaining
     */
    M addItem(@NotNull final MenuItem... items);

    /**
     * Add the itemStack to the list of items in the menu.
     * <p>
     * As this is the itemStack to add, it's not a menu itemStack, so it'd be converted to a MenuItem first
     * @param item the itemStack to add
     * @return the object for chaining
     */
    M setItem(@NotNull Slot slot, ItemStack item);

    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     * @return the object for chaining
     */
    M setItem(@NotNull Slot slot, MenuItem item);

    /**
     * Add the itemStack to the list of items in the menu.
     * <p>
     * As this is the itemStack to add, it's not a menu itemStack, so it'd be converted to a MenuItem first
     * @param item the itemStack to add
     * @return the object for chaining
     */
    M setItem(int slot, ItemStack item);
    
    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     * @return the object for chaining
     */
    M setItem(int slot, MenuItem item);

    /**
     * get the itemStack from the list of items in the menu.
     * @param i the index of the itemStack
     * @return the itemStack or null
     */
    @Nullable MenuItem getItem(int i);

    /**
     * get the itemStack from the list of items in the menu.
     * <p></p>
     * Usually this is the recommended way when using Java.
     * <p></p>
     * It is wrapped in an Optional which may or may not make the code cleaner and safer.
     * @param i the index of the itemStack
     * @return the optional itemStack or an empty optional
     */
    Optional<MenuItem> get(int i);

    /**
     * get the itemStack from the list of items in the menu.
     * @param slot the slot (row and col) of the itemStack
     * @return the itemStack or null
     */
    @Nullable MenuItem getItem(@NotNull Slot slot);

    /**
     * get the itemStack from the list of items in the menu.
     * <p></p>
     * Usually this is the recommended way when using Java.
     * <p></p>
     * It is wrapped in an Optional which may or may not make the code cleaner and safer.
     * @param slot the index of the itemStack
     * @return the optional itemStack or an empty optional
     */
    Optional<MenuItem> get(@NotNull Slot slot);
    
    boolean hasItem(@NotNull Slot slot);

    /**
     * Checks if the given slot has an item.
     *
     * @param  slot  the slot to check
     * @return       true if the slot has an item, false otherwise
     */
    boolean hasItem(int slot);

    /**
     * Checks if the given slot has an item.
     *
     * @param  item  the item to check
     * @return       true if the slot has an item, false otherwise
     */
    boolean hasItem(ItemStack item);

    /**
     * Checks if the given slot has an item.
     *
     * @param  item  the item to check
     * @return       true if the slot has an item, false otherwise
     */
    boolean hasItem(MenuItem item);
    /**
     * get the itemStack from the list of items in the menu from the provided description of the itemStack
     * <p></p>
     * Usually this is the recommended way when using Java.
     * <p></p>
     * It is wrapped in an Optional which may or may not make the code cleaner and safer.
     * @param itemDescription the description of the itemStack
     * @return the optional itemStack or an empty optional
     */
    Optional<MenuItem> get(Predicate<MenuItem> itemDescription);

    /**
     * get the itemStack from the list of items in the menu from the provided description of the itemStack
     * @param itemDescription the description of the itemStack
     * @return the itemStack or null
     */
    @Nullable MenuItem getItem(Predicate<MenuItem> itemDescription);

    /**
     * Remove all the specified items from the inventory.
     * @param itemStacks the items to remove
     * @return the object for chaining
     */
    M removeItem(@NotNull final ItemStack... itemStacks);

    /**
     * Remove all the specified items from the inventory.
     * @param itemStacks the items to remove
     * @return the object for chaining
     */
    M removeItemStacks(@NotNull final List<ItemStack> itemStacks);

    /**
     * Remove all the specified items from the inventory.
     * @param itemStacks the items to remove
     * @return the object for chaining
     */
    M removeItem(@NotNull final MenuItem... itemStacks);

    /**
     * Remove all the specified items from the inventory.
     * @param itemStacks the items to remove
     * @return the object for chaining
     */
    M removeItem(@NotNull final List<MenuItem> itemStacks);

    /**
     * Update the inventory which recreates the items on default
     * @return the object for chaining
     */
    M update();

    /**
     * Updates the menu every X ticks (repeatTime)
     *
     * @param  repeatTime  the time interval between each execution of the task
     */
    void updatePer(long repeatTime);

    /**
     * Updates the menu every X time (repeatTime)
     *
     * @param  repeatTime  the time interval between each execution of the task
     */
    void updatePer(Duration repeatTime);

    /**
     * Updates the menu every X ticks (repeatTime) with the delay of X ticks
     *
     * @param  delay       the time interval before the first execution
     * @param  repeatTime  the time interval between each execution of the task
     */
    void updatePer(long delay, long repeatTime);

    /**
     * Updates the menu every X ticks (repeatTime) with the delay of X ticks
     *
     * @param  delay       the time interval before the first execution
     * @param  repeatTime  the time interval between each execution of the task
     */
    void updatePer(Duration delay, Duration repeatTime);

    /**
     * Update the inventory with the title (RE-OPENS THE INVENTORY)
     * @param title the new title
     * @return the object for chaining
     */
    M updateTitle(String title);

    /**
     * Open the inventory for the provided player.
     * @apiNote Will not work if the player is sleeping.
     * @param entity the provided entity to open the inventory for.
     * @return the object for chaining
     */
    M open(@NotNull HumanEntity entity);

    boolean addModifier(Modifier modifier);

    boolean removeModifier(Modifier modifier);

    boolean addAllModifiers();

    void removeAllModifiers();

    boolean areItemsPlaceable();

    boolean areItemsRemovable();

    boolean areItemsSwappable();

    boolean areItemsCloneable();

    void updateItem(final int slot, @NotNull final ItemStack itemStack);

    /**
     * Alternative {@link #updateItem(int, ItemStack)} that takes <i>ROWS</i> and <i>COLUMNS</i> instead of slots.
     *
     * @param slot      The row and col of the slot.
     * @param itemStack The {@link ItemStack} to replace in the original one in the {@link MenuItem}.
     */
    void updateItem(@NotNull Slot slot, @NotNull final ItemStack itemStack);


    /**
     * Alternative {@link #updateItem(int, ItemStack)} that takes <i>ROWS</i> and <i>COLUMNS</i> instead of slots. also using MenuItem
     *
     * @param slot      The row and col of the slot.
     * @param itemStack The {@link ItemStack} to replace in the original one in the {@link MenuItem}.
     */
    void updateItem(@NotNull Slot slot, @NotNull final MenuItem itemStack);

    /**
     * get the map of items in the menu
     * <p>The returned map is unmodifiable; {@link UnsupportedOperationException} is thrown when attempting to modify it</p>
     * @return the map of items in the menu which is unmodifiable
     */
    @NotNull
    @Unmodifiable Map<Integer, MenuItem> getItemMap();

    void clear();
}
