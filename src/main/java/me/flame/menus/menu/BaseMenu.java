package me.flame.menus.menu;

import lombok.Getter;
import lombok.Setter;
import lombok.val;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.fillers.BorderFiller;
import me.flame.menus.menu.fillers.MenuFiller;
import me.flame.menus.menu.iterator.MenuIterator;
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
import java.util.stream.Stream;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

@SuppressWarnings({ "unused", "BooleanMethodIsAlwaysInverted",
                    "unchecked", "UnusedReturnValue" })
public abstract class BaseMenu<M extends BaseMenu<M>>
        implements InventoryHolder, Iterable<MenuItem> {
    protected @Getter Inventory inventory;

    protected @Getter int rows = 1;
    protected @Getter int size;
    private @Getter String title;
    protected @Getter boolean updating;

    private static final Material AIR = Material.AIR;
    private final MenuType type;
    private @Getter @Setter boolean dynamicSizing = false;

    private final EnumSet<Modifier> modifiers;
    protected final LinkedHashMap<Integer, MenuItem> itemMap;

    private final @Getter BorderFiller borderFiller;
    private final @Getter MenuFiller menuFiller;
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
    BaseMenu(MenuType type, String title, EnumSet<Modifier> modifiers) {
        this(type, title, modifiers, true);
    }

    BaseMenu(int rows, String title, EnumSet<Modifier> modifiers, boolean colorize) {
        this.type = MenuType.CHEST;
        this.modifiers = modifiers;
        this.rows = rows;
        this.title = colorize ? translateAlternateColorCodes('&', title) : title;
        this.size = rows * 9;
        this.itemMap = new LinkedHashMap<>(54);
        this.inventory = Bukkit.createInventory(this, size, title);

        this.borderFiller = BorderFiller.from(this);
        this.menuFiller = MenuFiller.from(this);
    }

    BaseMenu(@NotNull MenuType type, String title, EnumSet<Modifier> modifiers, boolean colorize) {
        this.type = type;
        this.modifiers = modifiers;
        this.title = colorize ? translateAlternateColorCodes('&', title) : title;
        this.size = type.getLimit();
        this.itemMap = new LinkedHashMap<>(size);
        this.inventory = Bukkit.createInventory(this, type.getType(), title);

        this.borderFiller = BorderFiller.from(this);
        this.menuFiller = MenuFiller.from(this);
    }

    /**
     * Get a LIST iterator of the items in the menu
     * @return the list iterator
     */
    public MenuIterator iterator() {
        return new MenuIterator(0, 0, MenuIterator.IterationDirection.HORIZONTAL, this);
    }

    /**
     * Get a LIST iterator of the items in the menu
     * @param direction the direction of the iteration
     * @return the list iterator
     */
    public MenuIterator iterator(MenuIterator.IterationDirection direction) {
        return new MenuIterator(0, 0, direction, this);
    }

    /**
     * Get a LIST iterator of the items in the menu
     * @param direction the direction of the iteration
     * @return the list iterator
     */
    public MenuIterator iterator(int startingRow, int startingCol, MenuIterator.IterationDirection direction) {
        return new MenuIterator(
                startingRow,
                startingCol,
                direction,
                this
        );
    }

    /**
     * Get a stream loop of the items in the menu
     * @return the stream
     */
    public Stream<MenuItem> stream() {
        return itemMap.values().stream();
    }

    /**
     * Get a parallel stream loop of the items in the menu
     * @apiNote use this if you want to do things in parallel, and you're sure of how to use it, else it might even get slower than the normal .stream()
     * @return the stream
     */
    public Stream<MenuItem> parallelStream() {
        return itemMap.values().parallelStream();
    }

    private void recreateInventory() {
        this.rows++;
        this.size = rows * 9;
        inventory = Bukkit.createInventory(this, size, title);
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
        if (size >= this.size) {
            if (!dynamicSizing || this.rows >= 6 || type != MenuType.CHEST) return (M) this;
            recreateInventory();
        }
        itemMap.put(size, new MenuItem(item, null));
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
        for (final MenuItem item : items) {
            int size = this.itemMap.size();
            if (size >= this.size) {
                if (!dynamicSizing || this.rows >= 6 || type != MenuType.CHEST) return (M) this;
                recreateInventory();
            }
            this.itemMap.put(size, item);
        }
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M addItem(MenuItem item, boolean update) {
        int size = this.itemMap.size();
        if (size >= this.size) {
            if (!dynamicSizing || this.rows >= 6 || type != MenuType.CHEST) return (M) this;
            recreateInventory();
        }
        itemMap.put(size, item);
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
    public M setItem(@NotNull Slot slot, ItemStack item, boolean update) {
        if (!validSlot(slot)) return (M) this;
        itemMap.put(slot.getSlot(), new MenuItem(item, null));
        if (update) update();
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * @param slot the row and col of the inventory
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M setItem(@NotNull Slot slot, MenuItem item, boolean update) {
        if (!validSlot(slot)) return (M) this;
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
    public M setItem(int slot, ItemStack item, boolean update) {
        itemMap.put(slot, new MenuItem(item, null));
        if (update) update();
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M setItem(int slot, MenuItem item, boolean update) {
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
        for (Map.Entry<Integer, MenuItem> entry : this.itemMap.entrySet()) {
            if (!item.equals(entry.getValue())) continue;
            itemMap.remove(entry.getKey());
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
        for (Map.Entry<Integer, MenuItem> entry : this.itemMap.entrySet()) {
            if (!entry.getValue().getItemStack().equals(itemStack)) continue;
            itemMap.remove(entry.getKey());
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
        int size = this.itemMap.size();
        if (size >= this.size) {
            if (!dynamicSizing || this.rows >= 6 || type != MenuType.CHEST) return (M) this;
            recreateInventory();
        }
        itemMap.put(size, new MenuItem(item, null));
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M addItem(MenuItem item) {
        int size = this.itemMap.size();
        if (size >= this.size) {
            if (!dynamicSizing || this.rows >= 6 || type != MenuType.CHEST) return (M) this;
            recreateInventory();
        }
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
    public M setItem(@NotNull Slot slot, ItemStack item) {
        if (!validSlot(slot)) return (M) this;
        itemMap.put(slot.getSlot(), new MenuItem(item, null));
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M setItem(@NotNull Slot slot, MenuItem item) {
        if (!validSlot(slot)) return (M) this;
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
    public M setItem(int slot, ItemStack item) {
        itemMap.put(slot, new MenuItem(item, null));
        return (M) this;
    }

    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     * @return the object for chaining
     */
    public M setItem(int slot, MenuItem item) {
        itemMap.put(slot, item);
        return (M) this;
    }

    /**
     * remove the itemStack from the list of items in the menu.
     * @param item the itemStack to remove
     * @return the object for chaining
     */
    public M removeItem(@NotNull final MenuItem item) {
        for (Map.Entry<Integer, MenuItem> entry : this.itemMap.entrySet()) {
            if (!item.equals(entry.getValue())) continue;
            itemMap.remove(entry.getKey());
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
        for (Map.Entry<Integer, MenuItem> entry : this.itemMap.entrySet()) {
            if (!itemStack.equals(entry.getValue().getItemStack())) continue;
            itemMap.remove(entry.getKey());
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
        return itemMap.get(i);
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
        return Optional.ofNullable(itemMap.get(i));
    }

    /**
     * get the itemStack from the list of items in the menu.
     * @param slot the slot (row and col) of the itemStack
     * @return the itemStack or null
     */
    public @Nullable MenuItem getItem(@NotNull Slot slot) {
        if (!validSlot(slot)) return null;
        return itemMap.get(slot.getSlot());
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
        if (!validSlot(slot)) return Optional.empty();
        return Optional.ofNullable(itemMap.get(slot.getSlot()));
    }

    public boolean hasItem(@NotNull Slot slot) {
        if (!validSlot(slot)) return false;
        return itemMap.get(slot.getSlot()) != null;
    }

    public boolean hasItem(int slot) {
        return itemMap.get(slot) != null;
    }

    public boolean hasItem(ItemStack item) {
        return getItem(itemOne -> itemOne.getItemStack().equals(item)) != null;
    }

    public boolean hasItem(MenuItem item) {
        return getItem(itemOne -> itemOne.equals(item)) != null;
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
        for (Map.Entry<Integer, MenuItem> entry : this.itemMap.entrySet()) {
            val value = entry.getValue();
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
        for (Map.Entry<Integer, MenuItem> entry : this.itemMap.entrySet()) {
            val value = entry.getValue();
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
        for (Map.Entry<Integer, MenuItem> entry : this.itemMap.entrySet()) {
            val itemStack = entry.getValue().getItemStack();
            if (!set.contains(itemStack)) continue;
            itemMap.remove(entry.getKey());
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
        for (Map.Entry<Integer, MenuItem> entry : this.itemMap.entrySet()) {
            val value = entry.getValue();
            if (!set.contains(value)) continue;
            itemMap.remove(entry.getKey());
        }
        return (M) this;
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
        Inventory updatedInventory = type == MenuType.CHEST
                ? Bukkit.createInventory(this, size, colorizedTitle)
                : Bukkit.createInventory(this, type.getType(), colorizedTitle);
        this.title = colorizedTitle;
        this.inventory = updatedInventory;

        this.updating = true;
        for (HumanEntity viewer : new ArrayList<>(inventory.getViewers()))
            viewer.openInventory(updatedInventory);
        this.updating = false;
        return (M) this;
    }

    protected void recreateItems() {
        if (itemMap.isEmpty()) return;
        for (Map.Entry<Integer, MenuItem> entry : this.itemMap.entrySet()) {
    		int i = entry.getKey();
    		ItemStack item = entry.getValue().getItemStack();
            if (item == null || item.getType() == AIR) {
                inventory.setItem(i, null);
                continue;
            }
            inventory.setItem(i, item);
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
        recreateItems();
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
        if (!validSlot(slot)) return;
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
        if (!validSlot(slot)) return;
        final int slotNum = slot.getSlot();
        final MenuItem guiItem = itemMap.get(slotNum);

        if (guiItem == null) {
            itemMap.put(slotNum, itemStack);
            return;
        }

        guiItem.setItemStack(itemStack.getItemStack());
        itemMap.put(slotNum, guiItem);
    }

    protected boolean validSlot(Slot slot) {
        return slot.isSlot();
    }
}
