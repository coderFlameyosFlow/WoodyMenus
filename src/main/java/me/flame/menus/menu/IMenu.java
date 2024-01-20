package me.flame.menus.menu;

import me.flame.menus.adventure.TextHolder;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.animation.Animation;
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
public interface IMenu extends Iterable<MenuItem>, InventoryHolder {
    /**
     * Get a map of the items in the menu.
     * <p>This is an unmodifiable map.</p>
     * <p>Using a map isn't a good idea, it's better to use a list or an array,</p>
     * <p>Not because a map boxes integers (those are stored in memory) but it's simpler.</p>
     * @return the item map.
     */
    @Unmodifiable Map<Integer, MenuItem> getItemMap();

    /**
     * Get a LIST iterator of the items in the menu
     * @return the list iterator
     */
    @NotNull
    Iterator<MenuItem> iterator();

    /**
     * Get a LIST iterator of the items in the menu
     * @param direction the direction of the iteration
     * @return the list iterator
     */
    Iterator<MenuItem> iterator(IterationDirection direction);

    /**
     * Get a LIST iterator of the items in the menu
     * @param direction the direction of the iteration
     * @return the list iterator
     */
    Iterator<MenuItem> iterator(int startingRow, int startingCol, IterationDirection direction);

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
     *
     * @param items varargs of itemStack stacks
     * @return true if the items were added and the menu was changed
     */
    boolean addItem(@NotNull final ItemStack... items);

    /**
     * Add a list of items to the list of items in the menu.
     *
     * @param items the items
     * @return true if the items were added and the menu was changed
     */
    boolean addItem(@NotNull final MenuItem... items);

    /**
     * Add the itemStack to the list of items in the menu.
     * <p>
     * As this is the itemStack to add, it's not a menu itemStack, so it'd be converted to a MenuItem first
     * @param item the itemStack to add
     */
    void setItem(@NotNull Slot slot, ItemStack item);

    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     */
    void setItem(@NotNull Slot slot, MenuItem item);

    /**
     * Add the itemStack to the list of items in the menu.
     * <p>
     * As this is the itemStack to add, it's not a menu itemStack, so it'd be converted to a MenuItem first
     * @param item the itemStack to add
     */
    void setItem(int slot, ItemStack item);
    
    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     */
    void setItem(int slot, MenuItem item);

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
     */
    void removeItem(@NotNull final ItemStack... itemStacks);

    /**
     * Remove all the specified items from the inventory.
     * @param itemStacks the items to remove
     */
    void removeItemStacks(@NotNull final List<ItemStack> itemStacks);

    /**
     * Remove all the specified items from the inventory.
     * @param itemStacks the items to remove
     */
    void removeItem(@NotNull final MenuItem... itemStacks);

    /**
     * Remove all the specified items from the inventory.
     * @param itemStacks the items to remove
     */
    void removeItem(@NotNull final List<MenuItem> itemStacks);

    /**
     * Update the inventory which recreates the items on default
     */
    void update();

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
     */
    void updateTitle(String title);

    /**
     * Open the inventory for the provided player.
     * @apiNote Will not work if the player is sleeping.
     * @param entity the provided entity to open the inventory for.
     */
    void open(@NotNull HumanEntity entity);

    /**
     * Add a modifier to prevent a player from doing an action.
     * @param modifier the action to prevent the player from doing
     * @return the result of the operation
     */
    boolean addModifier(Modifier modifier);

    /**
     * Remove a modifier to allow a player to do an action once again.
     * @param modifier the action to allow a player to do
     * @return the result of the operation
     */
    boolean removeModifier(Modifier modifier);

    /**
     * Add every modifier to prevent a player from doing all actions
     * @return the result of the operation
     */
    boolean addAllModifiers();

    /**
     * Remove every modifier to allow a player to do all actions
     */
    void removeAllModifiers();

    /**
     * Check if items are placeable in the menu
      * @return if the items are placeable
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean areItemsPlaceable();

    /**
     * Check if items are removable in the menu
     * @return if the items are removable
     */
    boolean areItemsRemovable();

    /**
     * Check if items are swappable in the menu
     * @return if the items are swappable
     */
    boolean areItemsSwappable();

    /**
     * Check if items are cloneable in the menu
     * @return if the items are cloneable
     */
    boolean areItemsCloneable();

    /**
     * Update an item in the menu
     * @param slot the slot of the item
     * @param itemStack the item stack to update the menuItem with
     */
    void updateItem(final int slot, @NotNull final ItemStack itemStack);

    /**
     * Alternative {@link #updateItem(int, ItemStack)} that takes a {@link Slot} instead of int slots.
     *
     * @param slot      The row and col of the slot.
     * @param itemStack The {@link ItemStack} to replace in the original one in the {@link MenuItem}.
     */
    void updateItem(@NotNull Slot slot, @NotNull final ItemStack itemStack);

    /**
     * Clear every item in the menu.
     * @apiNote this doesn't iterate through every item in the menu and set it to null,
     *          but rather it just allocates a new array with a size of the menu with no items.
     */
    void clear();

    /**
     * Get how many rows there are in the menu
     * @return the number of rows
     */
    int rows();

    /**
     * Get the size of the menu
     * @return the size of the menu
     */
    int size();

    /**
     * Set the contents of the menu
     * @param items the contents to set
     * @apiNote this will replace all items in the menu, and this is an O(1) operation
     */
    void setContents(MenuItem... items);

    /**
     * Get a copy of the item data's menu items.
     * @return the copy
     */
    @NotNull
    MenuItem[] getItems();

    /**
     * Get a list of the menu items in the menu
     * @return an unmodifiable list.
     */
    @NotNull
    @Unmodifiable List<MenuItem> getItemList();

    /**
     * Get a copy of the item data in the menu
     * @return the copy
     */
    @NotNull ItemData getData();

    /**
     * Get a linked map of the items in the menu.
     * <p>This is an unmodifiable linked map.</p>
     * <p>Using a map isn't a good idea, it's better to use a list or an array,</p>
     * <p>Not because a map boxes integers (those are stored in memory) but it's simpler.</p>
     * @return the item map.
     * @apiNote A linked map shouldn't be used when unmodifiable, it's better to use a list or an array
     *          Some people HAVE to use a map, use {@link #getConcurrentItemMap()} for concurrency or {@link #getItemMap()} for better performance
     *          and memory consumption.
     */
    @NotNull
    @Unmodifiable Map<Integer, MenuItem> getLinkedItemMap();

    /**
     * Get a concurrent map of the items in the menu.
     * <p>This is an unmodifiable concurrent map.</p>
     * @return the item map.
     * @apiNote unless you need concurrency, you should use {@link #getItemMap()} or even better, a list/array/{@link #getData()}
     */

    @NotNull
    @Unmodifiable Map<Integer, MenuItem> getConcurrentItemMap();

    /**
     * Checks if the animations has any animations to execute
     * @return true if the list is not empty.
     */
    boolean hasAnimations();

    /**
     * Add an animation to be executed on the menu
     * @param animation the animation
     */
    void addAnimation(Animation animation);

    /**
     * Remove an animation from the menu and stop its execution
     * @param animation the animation
     */
    void removeAnimation(Animation animation);

    /**
     * Remove an animation from the menu and stop its execution
     * @param animationIndex the index of the animation
     * @apiNote it's recommended to use this because it has a time complexity of O(1)
     */
    void removeAnimation(int animationIndex);

    /**
     * Get a mutable list of animations
     * @return the list
     */
    List<Animation> getAnimations();

    /**
     * If the animations have started
     * @return true if animations have started animating
     */
    boolean hasAnimationsStarted();

    /**
     * Generate a paginated menu; convert to a {@link PaginatedMenu} from a {@link Menu}
     *
     * @return         	The generated PaginatedMenu object
     *                  Comes with 3 pages by default.
     */
    @NotNull
    default PaginatedMenu pagination() {
        return pagination(3);
    }

    /**
     * Generate a paginated menu; convert to a {@link PaginatedMenu} from a {@link Menu}
     * @param pages the amount of pages to generate with the paged menu
     * @return         	The generated PaginatedMenu object
     */
    @NotNull
    PaginatedMenu pagination(int pages);

    /**
     * Checks if all modifiers have been added
     * @return if the size of modifiers are equal to 4
     */
    boolean allModifiersAdded();

    /**
     * Get the title of the menu as a legacy String.
     * @return the title
     * @apiNote  Use {@link #title()}, it has the same control, but it supports legacy and adventure.
     */
    String getTitle();

    /**
     * Get the title of the menu as a {@link TextHolder}.
     * <p>
     * TextHolder+ exists to allow support for legacy and adventure titles.
     * @return the title
     */
    TextHolder title();

    /**
     * Get the menu data of the menu as a {@link MenuData}.
     * @return the menu data
     * @apiNote good for serializing and deserializing, and configuration serializing and deserializing.
     */
    MenuData getMenuData();
}
