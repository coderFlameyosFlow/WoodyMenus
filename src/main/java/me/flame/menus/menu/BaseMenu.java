package me.flame.menus.menu;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import lombok.Getter;
import lombok.Setter;
import lombok.val;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.fillers.Filler;
import me.flame.menus.menu.fillers.MenuFiller;
import me.flame.menus.menu.iterator.MenuIterator;
import me.flame.menus.modifiers.Modifier;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

@Getter
@ApiStatus.NonExtendable
@SuppressWarnings({ "unused", "BooleanMethodIsAlwaysInverted", "unchecked", "UnusedReturnValue" })
public abstract class BaseMenu<M extends BaseMenu<M>> implements IMenu<M> {
    @NotNull
    protected Inventory inventory;

    @NotNull
    protected MenuType type = MenuType.CHEST;

    @NotNull
    protected final EnumSet<Modifier> modifiers;

    @NotNull
    protected final Map<Integer, MenuItem> itemMap;

    @NotNull
    private final MenuIterator iterator = new MenuIterator(IterationDirection.HORIZONTAL, this);

    @NotNull
    protected String title;

    @NotNull
    private static final BukkitScheduler sch = Bukkit.getScheduler();

    protected @Setter MenuFiller defaultFiller = Filler.from(this);

    protected int rows = 1, size;

    protected @Setter boolean dynamicSizing = false, updating = false;

    private @Setter Consumer<InventoryClickEvent> outsideClickAction = event -> {};
    private @Setter Consumer<InventoryClickEvent> bottomClickAction = event -> {};
    private @Setter Consumer<InventoryClickEvent> topClickAction = event -> {};
    private @Setter Consumer<InventoryClickEvent> clickAction = event -> {};
    private @Setter BiConsumer<InventoryCloseEvent, Result> closeAction = (event, result) -> {};
    private @Setter Consumer<InventoryOpenEvent> openAction = event -> {};
    private @Setter Consumer<InventoryDragEvent> dragAction = event -> {};

    public BaseMenu(int rows, String title, EnumSet<Modifier> modifiers) {
        this(rows, title, modifiers, true);
    }
    public BaseMenu(MenuType type, String title, EnumSet<Modifier> modifiers) {
        this(type, title, modifiers, true);
    }

    public BaseMenu(int rows, String title, @NotNull EnumSet<Modifier> modifiers, boolean colorize) {
        this.modifiers = modifiers;
        this.rows = rows;
        this.title = colorize ? translateAlternateColorCodes('&', title) : title;
        this.size = rows * 9;
        this.itemMap = new HashMap<>(54);
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    BaseMenu(@NotNull MenuType type, String title, @NotNull EnumSet<Modifier> modifiers, boolean colorize) {
        this.type = type;
        this.modifiers = modifiers;
        this.title = colorize ? translateAlternateColorCodes('&', title) : title;
        this.size = type.getLimit();
        this.itemMap = new HashMap<>(size);
        this.inventory = Bukkit.createInventory(this, type.getType(), title);
    }

    private static final Material AIR = Material.AIR;

    public MenuFiller getFiller() {
        return getFiller(Filler.class);
    }

    public <T extends MenuFiller> T getFiller(Class<T> value) {
        return value.cast(defaultFiller);
    }

    public MenuIterator iterator() {
        return iterator;
    }

    public MenuIterator iterator(IterationDirection direction) {
        return this.iterator(1, 1, direction);
    }

    public MenuIterator iterator(int startingRow, int startingCol, IterationDirection direction) {
        return new MenuIterator(startingRow, startingCol, direction, this);
    }

    public Stream<MenuItem> stream() {
        return itemMap.values().stream();
    }

    public Stream<MenuItem> parallelStream() {
        return itemMap.values().parallelStream();
    }

    public void recreateInventory() {
        this.rows++;
        this.size = rows * 9;
        inventory = Bukkit.createInventory(this, size, title);
    }

    public List<HumanEntity> getViewers() {
        return inventory.getViewers();
    }

    public M addItem(@NotNull final ItemStack... items) {
    	final List<ItemStack> notAddedItems = new ArrayList<>();
        final Set<Integer> occupiedSlots = itemMap.keySet();

        int slot = 0;
    	for (final ItemStack guiItem : items) {
            slot = getSlot(occupiedSlots, slot);
            if (isInvalidSlot(notAddedItems, slot, guiItem)) continue; // incase.

            itemMap.put(slot, MenuItem.of(guiItem));
            slot++;
        }
        
        if (dynamicSizing && !notAddedItems.isEmpty() && this.rows < 6 && this.type == MenuType.CHEST) {
            recreateInventory();
            update();
        	return this.addItem(notAddedItems.toArray(new ItemStack[0]));
        }
        return (M) this;
	}

    public M addItem(@NotNull final MenuItem... items) {
        final List<MenuItem> notAddedItems = new ArrayList<>(54);
        final Set<Integer> occupiedSlots = itemMap.keySet();

        int slot = 0;
        for (final MenuItem guiItem : items) {
            slot = getSlot(occupiedSlots, slot);
            if (isInvalidSlot(notAddedItems, slot, guiItem)) continue; // incase.

            itemMap.put(slot, guiItem);
            slot++;
        }

        if (dynamicSizing && !notAddedItems.isEmpty() && (this.rows < 6 && this.type == MenuType.CHEST)) {
            recreateInventory();
            update();
            return this.addItem(notAddedItems.toArray(new MenuItem[0]));
        }
        return (M) this;
    }

    private boolean isInvalidSlot(List<MenuItem> notAddedItems, int slot, MenuItem guiItem) {
        if (slot >= size) {
            notAddedItems.add(guiItem);
            return true;
        }
        return false;
    }

    private boolean isInvalidSlot(List<ItemStack> notAddedItems, int slot, ItemStack guiItem) {
        if (slot >= size) {
            notAddedItems.add(guiItem);
            return true;
        }
        return false;
    }

    private static int getSlot(Set<Integer> occupiedSlots, int slot) {
        while (true) {
            boolean contains = occupiedSlots.contains(slot);
            if (!contains) break;
            slot++;
        }

        return slot;
    }

    public M setItem(@NotNull Slot slot, ItemStack item) {
        if (!slot.isSlot()) return (M) this;
        itemMap.put(slot.slot, MenuItem.of(item));
        return (M) this;
    }

    public M setItem(@NotNull Slot slot, MenuItem item) {
        if (!slot.isSlot()) return (M) this;
        itemMap.put(slot.slot, item);
        return (M) this;
    }

    public M setItem(int slot, ItemStack item) {
        itemMap.put(slot, MenuItem.of(item));
        return (M) this;
    }

    public M setItem(int slot, MenuItem item) {
        itemMap.put(slot, item);
        return (M) this;
    }

    public @Nullable MenuItem getItem(int i) {
        return itemMap.get(i);
    }

    public Optional<MenuItem> get(int i) {
        return Optional.ofNullable(itemMap.get(i));
    }

    public @Nullable MenuItem getItem(@NotNull Slot slot) {
        if (!slot.isSlot()) return null;
        return itemMap.get(slot.slot);
    }

    public Optional<MenuItem> get(@NotNull Slot slot) {
        if (!slot.isSlot()) return Optional.empty();
        return Optional.ofNullable(itemMap.get(slot.slot));
    }

    public boolean hasItem(@NotNull Slot slot) {
        if (!slot.isSlot()) return false;
        return itemMap.get(slot.slot) != null;
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

    public Optional<MenuItem> get(Predicate<MenuItem> itemDescription) {
        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            val value = itemMap.get(i);
            if (value == null || !itemDescription.test(value)) continue;
            return Optional.of(value);
        }
        return Optional.empty();
    }


    public @Nullable MenuItem getItem(Predicate<MenuItem> itemDescription) {
        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            val value = itemMap.get(i);
            if (value == null || !itemDescription.test(value)) continue;
            return value;
        }
        return null;
    }

    public M removeItem(@NotNull final ItemStack... itemStacks) {
        return removeItemStacks(Arrays.asList(itemStacks));
    }

    public M removeItemStacks(@NotNull final List<ItemStack> itemStacks) {
        Set<ItemStack> set = ImmutableSet.copyOf(itemStacks);
        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            val item = itemMap.get(i);
            if (item == null) continue;

            val itemStack = item.getItemStack();
            if (set.contains(itemStack)) {
                itemMap.remove(i);
                inventory.remove(itemStack);
            }
        }
        return (M) this;
    }

    public M removeItem(@NotNull final MenuItem... items) {
        Set<MenuItem> slots = ImmutableSet.copyOf(items);

        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = itemMap.get(i);
            if (item != null && slots.contains(item)) {
                itemMap.remove(i);
                inventory.remove(item.getItemStack());
            }
        }
        return (M) this;
    }

    @Override
    public M removeItem(@NotNull final List<MenuItem> itemStacks) {
        Set<MenuItem> set = ImmutableSet.copyOf(itemStacks);
        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = itemMap.get(i);
            if (set.contains(item)) {
                itemMap.remove(i);
                inventory.remove(item.getItemStack());
            }
        }
        return (M) this;
    }

    M update(Inventory inventory) {
        this.updating = true;
        recreateItems(inventory);
        List<HumanEntity> entities = new ArrayList<>(inventory.getViewers());
        entities.forEach(e -> ((Player) e).updateInventory());
        this.updating = false;
        return (M) this;
    }

    @Override
    public M update() {
        return this.update(this.inventory);
    }

    public void updatePer(long repeatTime) {
        sch.runTaskTimer(Menus.plugin(), () -> update(), 0, repeatTime);
    }

    public void updatePer(@NotNull Duration repeatTime) {
        sch.runTaskTimer(Menus.plugin(), () -> update(), 0, repeatTime.toMillis() / 50);
    }

    public void updatePer(long delay, long repeatTime) {
        sch.runTaskTimer(Menus.plugin(), () -> update(), delay, repeatTime);
    }

    public void updatePer(@NotNull Duration delay, @NotNull Duration repeatTime) {
        sch.runTaskTimer(Menus.plugin(), () -> update(), delay.toMillis() / 50, repeatTime.toMillis() / 50);
    }

    public M updateTitle(String title) {
        Inventory oldInventory = this.inventory;
        String colorizedTitle = translateAlternateColorCodes('&', title);
        this.updating = true;
        Inventory updatedInventory = type == MenuType.CHEST
                ? Bukkit.createInventory(this, size, colorizedTitle)
                : Bukkit.createInventory(this, type.getType(), colorizedTitle);
        this.title = colorizedTitle;
        this.inventory = updatedInventory;

        List<HumanEntity> entities = ImmutableList.copyOf(oldInventory.getViewers());
        entities.forEach(e -> e.openInventory(updatedInventory));
        this.updating = false;
        return (M) this;
    }

    protected void recreateItems(Inventory inventory) {
        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            MenuItem menuItem = itemMap.get(i);
            inventory.setItem(i, menuItem == null ? null : menuItem.getItemStack());
        }
    }

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
            itemMap.put(slot, MenuItem.of(itemStack));
            return;
        }

        guiItem.setItemStack(itemStack);
        itemMap.put(slot, guiItem);
    }

    public void updateItem(@NotNull Slot slot, @NotNull final ItemStack itemStack) {
        if (!slot.isSlot()) return;
        final int slotNum = slot.slot;

        final MenuItem guiItem = itemMap.get(slotNum);

        if (guiItem == null) {
            itemMap.put(slotNum, MenuItem.of(itemStack));
            return;
        }

        guiItem.setItemStack(itemStack);
        itemMap.put(slotNum, guiItem);
    }

    public void updateItem(@NotNull Slot slot, @NotNull final MenuItem itemStack) {
        if (!slot.isSlot()) return;
        final int slotNum = slot.slot;

        final MenuItem guiItem = itemMap.get(slotNum);

        if (guiItem == null) {
            itemMap.put(slotNum, itemStack);
            return;
        }

        guiItem.setItemStack(itemStack.getItemStack());
        itemMap.put(slotNum, guiItem);
    }

    @NotNull
    public @Unmodifiable Map<Integer, MenuItem> getItemMap() {
        return Collections.unmodifiableMap(itemMap);
    }

    public void clear() {
        itemMap.clear();
    }
}
