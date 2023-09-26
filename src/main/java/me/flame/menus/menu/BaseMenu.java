package me.flame.menus.menu;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import lombok.Getter;
import lombok.Setter;

import me.flame.menus.events.ClickActionEvent;
import me.flame.menus.events.OpenMenuEvent;
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

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

/**
 * BaseMenu; an abstraction for all other Menus.
 * @author flameyosflow
 * @since 1.0.0
 */
@Getter
@ApiStatus.NonExtendable
@SuppressWarnings({ "unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue" })
public abstract class BaseMenu implements IMenu, RandomAccess {
    @NotNull
    protected Inventory inventory;

    @NotNull
    protected MenuType type = MenuType.CHEST;

    @NotNull
    protected final EnumSet<Modifier> modifiers;

    @NotNull
    private final MenuIterator iterator = new MenuIterator(IterationDirection.HORIZONTAL, this);

    @NotNull
    protected String title;

    @NotNull
    private static final BukkitScheduler sch = Bukkit.getScheduler();

    @Setter
    protected MenuFiller defaultFiller = Filler.from(this);

    @Setter
    protected boolean dynamicSizing = false, updating = false;

    protected int rows = 1, size;

    protected MenuItem[] itemMap;

    protected static Plugin plugin;

    public static void init(Plugin p) {
        plugin = p;
    }

    private @Setter Consumer<ClickActionEvent> outsideClickAction = event -> {};
    private @Setter Consumer<ClickActionEvent> bottomClickAction = event -> {};
    private @Setter Consumer<ClickActionEvent> topClickAction = event -> {};
    private @Setter Consumer<ClickActionEvent> clickAction = event -> {};
    private @Setter BiConsumer<InventoryCloseEvent, Result> closeAction = (event, result) -> {};
    private @Setter Consumer<OpenMenuEvent> openAction = event -> {};
    private @Setter Consumer<InventoryDragEvent> dragAction = event -> {};

    private void remove(int index) {
        int length = this.itemMap.length;
        if (index <= 0 || index >= length) return;

        itemMap[index] = null;
    }

    public void click(ClickActionEvent event, Click click) {
        switch (click) {
            case OUTSIDE: outsideClickAction.accept(event);
            case BOTTOM: bottomClickAction.accept(event);
            case TOP: topClickAction.accept(event);
            default: clickAction.accept(event);
        }
    }

    public void click(InventoryDragEvent event) {
        dragAction.accept(event);
    }

    public void click(OpenMenuEvent event) {
        if (!updating) openAction.accept(event);
    }

    public void click(InventoryCloseEvent event, Result result) {
        if (!updating) closeAction.accept(event, result);
    }

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
        this.itemMap = new MenuItem[size];
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    BaseMenu(@NotNull MenuType type, String title, @NotNull EnumSet<Modifier> modifiers, boolean colorize) {
        this.type = type;
        this.modifiers = modifiers;
        this.title = colorize ? translateAlternateColorCodes('&', title) : title;
        this.size = type.getLimit();
        this.itemMap = new MenuItem[size];
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
        return Arrays.stream(itemMap);
    }

    public Stream<MenuItem> parallelStream() {
        return Arrays.stream(itemMap).parallel();
    }

    public void recreateInventory() {
        this.rows++;
        this.size = rows * 9;
        this.inventory = Bukkit.createInventory(this, this.size, this.title);

        MenuItem[] newItemArray = new MenuItem[size];
        System.arraycopy(itemMap, 0, newItemArray, 0, size);
        this.itemMap = newItemArray;
    }

    public List<HumanEntity> getViewers() {
        return inventory.getViewers();
    }

    public void addItem(@NotNull final ItemStack... items) {
    	final List<ItemStack> notAddedItems = new ArrayList<>(items.length);

        int slot = 0;
    	for (final ItemStack guiItem : items) {
            if (slot >= size) {
                if (rows == 6) break; // save some performance
                notAddedItems.add(guiItem);
                continue;
            }

            slot = getSlot(itemMap, slot);

            this.itemMap[slot] = MenuItem.of(guiItem);
            slot++;
        }
        
        if (this.dynamicSizing && !notAddedItems.isEmpty() && this.rows < 6 && this.type == MenuType.CHEST) {
            this.updating = true;
            this.recreateInventory();
            this.update();
            this.updating = true;
        	this.addItem(notAddedItems.toArray(new ItemStack[0]));
        }
	}

    public void addItem(@NotNull final MenuItem... items) {
        final List<MenuItem> notAddedItems = new ArrayList<>(items.length);

        int slot = 0;
        for (final MenuItem guiItem : items) {
            if (slot >= size && rows == 6) break; // save some performance

            if (slot >= size) {
                notAddedItems.add(guiItem);
                continue;
            }

            slot = getSlot(itemMap, slot);

            this.itemMap[slot] = guiItem;
            slot++;
        }

        if (this.dynamicSizing && !notAddedItems.isEmpty() && (this.rows < 6 && this.type == MenuType.CHEST)) {
            this.updating = true;
            this.recreateInventory();
            this.update();
            this.updating = true;
            this.addItem(notAddedItems.toArray(new MenuItem[0]));
        }
    }


    private static boolean isInvalidSlot(Collection<ItemStack> notAddedItems, int slot, int size, ItemStack guiItem) {
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

    public void setItem(@NotNull Slot slot, ItemStack item) {
        if (!slot.isSlot()) return;
        this.itemMap[slot.slot] = MenuItem.of(item);
    }

    public void setItem(@NotNull Slot slot, MenuItem item) {
        if (!slot.isSlot()) return;
        this.itemMap[slot.slot] = item;
    }

    public void setItem(int slot, ItemStack item) {
        this.itemMap[slot] = MenuItem.of(item);
    }

    public void setItem(int slot, MenuItem item) {
        this.itemMap[slot] = item;
    }

    public @Nullable MenuItem getItem(int i) {
        return this.itemMap[i];
    }

    public Optional<MenuItem> get(int i) {
        return Optional.ofNullable(this.itemMap[i]);
    }

    public @Nullable MenuItem getItem(@NotNull Slot slot) {
        if (!slot.isSlot()) return null;
        return this.itemMap[slot.slot];
    }

    public Optional<MenuItem> get(@NotNull Slot slot) {
        if (!slot.isSlot()) return Optional.empty();
        return Optional.ofNullable(this.itemMap[slot.slot]);
    }

    public boolean hasItem(@NotNull Slot slot) {
        if (!slot.isSlot()) return false;
        return this.itemMap[slot.slot] != null;
    }

    public boolean hasItem(int slot) {
        return this.itemMap[slot] != null;
    }

    public boolean hasItem(ItemStack item) {
        return this.getItem(itemOne -> itemOne.getItemStack().equals(item)) != null;
    }

    public boolean hasItem(MenuItem item) {
        return this.getItem(itemOne -> itemOne.equals(item)) != null;
    }

    public Optional<MenuItem> get(Predicate<MenuItem> itemDescription) {
        for (MenuItem value : this.itemMap) {
            if (value == null || !itemDescription.test(value)) continue;
            return Optional.of(value);
        }
        return Optional.empty();
    }


    public @Nullable MenuItem getItem(Predicate<MenuItem> itemDescription) {
        for (MenuItem value : this.itemMap) {
            if (value == null || !itemDescription.test(value)) continue;
            return value;
        }
        return null;
    }

    public void removeItem(@NotNull final ItemStack... itemStacks) {
        removeItemStacks(Arrays.asList(itemStacks));
    }

    public void removeItemStacks(@NotNull final List<ItemStack> itemStacks) {
        Set<ItemStack> set = ImmutableSet.copyOf(itemStacks);

        int length = itemMap.length;
        for (int i = 0; i < length; i++) {
            MenuItem item = this.itemMap[i];
            if (item != null && set.contains(item.getItemStack())) {
                this.remove(i);
            }
        }
    }

    public void removeItem(@NotNull final MenuItem... items) {
        Set<MenuItem> slots = ImmutableSet.copyOf(items);

        int length = itemMap.length;
        for (int i = 0; i < length; i++) {
            MenuItem item = this.itemMap[i];
            if (item != null && slots.contains(item)) {
                this.remove(i);
            }
        }
    }

    @Override
    public void removeItem(@NotNull final List<MenuItem> itemStacks) {
        Set<MenuItem> set = ImmutableSet.copyOf(itemStacks);
        int length = itemMap.length;
        for (int i = 0; i < length; i++) {
            MenuItem item = this.itemMap[i];
            if (item != null && set.contains(item)) {
                this.remove(i);
            }
        }
    }

    void update(Inventory inventory) {
        this.updating = true;
        recreateItems(inventory);
        List<HumanEntity> entities = ImmutableList.copyOf(inventory.getViewers());
        entities.forEach(e -> ((Player) e).updateInventory());
        this.updating = false;
    }

    @Override
    public void update() {
        this.update(this.inventory);
    }

    public void updatePer(long repeatTime) {
        sch.runTaskTimer(plugin, () -> {
            this.updating = true;
            this.update();
            this.updating = true;
        }, 0, repeatTime);
    }

    public void updatePer(@NotNull Duration repeatTime) {
        sch.runTaskTimer(plugin, () -> {
            this.updating = true;
            this.update();
            this.updating = true;
        }, 0, repeatTime.toMillis() / 50);
    }

    public void updatePer(long delay, long repeatTime) {
        sch.runTaskTimer(plugin, () -> {
            this.updating = true;
            this.update();
            this.updating = true;
        }, delay, repeatTime);
    }

    public void updatePer(@NotNull Duration delay, @NotNull Duration repeatTime) {
        sch.runTaskTimer(plugin, () -> {
            this.updating = true;
            this.update();
            this.updating = true;
        }, delay.toMillis() / 50, repeatTime.toMillis() / 50);
    }

    public void updateTitle(String title) {
        Inventory oldInventory = this.inventory;
        String colorizedTitle = translateAlternateColorCodes('&', title);
        Inventory updatedInventory = this.type == MenuType.CHEST
                ? Bukkit.createInventory(this, this.size, colorizedTitle)
                : Bukkit.createInventory(this, this.type.getType(), colorizedTitle);
        this.updating = true;
        this.title = colorizedTitle;
        this.inventory = updatedInventory;

        List<HumanEntity> entities = ImmutableList.copyOf(oldInventory.getViewers());
        entities.forEach(e -> e.openInventory(updatedInventory));
        this.updating = false;
    }

    protected void recreateItems(Inventory inventory) {
        int size = this.itemMap.length;
        for (int i = 0; i < size; i++) {
            MenuItem menuItem = this.itemMap[i];
            inventory.setItem(i, menuItem == null ? null : menuItem.getItemStack());
        }
    }

    public void open(@NotNull HumanEntity entity) {
        if (entity.isSleeping()) return;

        this.updating = true;
        this.update();
        this.updating = false;

        entity.openInventory(inventory);
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
        final MenuItem guiItem = this.itemMap[slot];

        if (guiItem == null) {
            itemMap[slot] = MenuItem.of(itemStack);
            return;
        }

        guiItem.setItemStack(itemStack);
        itemMap[slot] = guiItem;
    }

    public void updateItem(@NotNull Slot pos, @NotNull final ItemStack itemStack) {
        if (!pos.isSlot()) return;
        final int slot = pos.slot;
        final MenuItem guiItem = this.itemMap[slot];

        if (guiItem == null) {
            itemMap[slot] = MenuItem.of(itemStack);
            return;
        }

        guiItem.setItemStack(itemStack);
        itemMap[slot] = guiItem;
    }

    public void setContents(MenuItem... items) {
        this.itemMap = items;

        this.updating = true;
        this.update();
        this.updating = false;
    }

    @NotNull
    public MenuItem[] getItems() {
        return Arrays.copyOf(this.itemMap, this.itemMap.length);
    }

    @NotNull
    public @Unmodifiable List<MenuItem> getItemList() {
        return ImmutableList.copyOf(itemMap);
    }

    @NotNull
    public @Unmodifiable Map<Integer, MenuItem> getItemMap() {
        Map<Integer, MenuItem> items = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            items.put(i, itemMap[i]);
        }
        return Collections.unmodifiableMap(items);
    }

    @NotNull
    public @Unmodifiable Map<Integer, MenuItem> getLinkedItemMap() {
        Map<Integer, MenuItem> items = new LinkedHashMap<>(size);
        for (int i = 0; i < size; i++) {
            items.put(i, itemMap[i]);
        }
        return Collections.unmodifiableMap(items);
    }

    @NotNull
    public @Unmodifiable Map<Integer, MenuItem> getConcurrentItemMap() {
        Map<Integer, MenuItem> items = new ConcurrentHashMap<>(size);
        for (int i = 0; i < size; i++) {
            items.put(i, itemMap[i]);
        }
        return Collections.unmodifiableMap(items);
    }

    public void clear() {
        Arrays.fill(itemMap, null);
    }

    public MenuData getMenuData() {
        return type == MenuType.CHEST
                ? new MenuData(title, rows, modifiers, Arrays.asList(itemMap))
                : new MenuData(title, type, modifiers, Arrays.asList(itemMap));
    }

    public enum Click {
        OUTSIDE,
        BOTTOM,
        TOP,
        DEFAULT
    }
}
