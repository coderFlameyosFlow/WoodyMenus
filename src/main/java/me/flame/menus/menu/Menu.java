package me.flame.menus.menu;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import me.flame.menus.adventure.TextHolder;
import me.flame.menus.components.nbt.*;
import me.flame.menus.events.ClickActionEvent;
import me.flame.menus.events.BeforeAnimatingEvent;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.animation.Animation;
import me.flame.menus.menu.fillers.*;
import me.flame.menus.menu.iterator.MenuIterator;
import me.flame.menus.modifiers.Modifier;
import me.flame.menus.util.ItemResponse;
import me.flame.menus.util.VersionHelper;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import org.jetbrains.annotations.*;

import java.time.Duration;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * Most commonly used normal Menu
 * <p>
 *
 */
@SuppressWarnings({ "unused", "UnusedReturnValue" })
public class Menu implements IMenu, RandomAccess, Serializable {
    @Getter @NotNull
    protected Inventory inventory;

    @Getter @NotNull
    protected final MenuType type;

    @Getter @NotNull
    protected final EnumSet<Modifier> modifiers;

    @NotNull
    protected TextHolder title;

    @NotNull
    static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    @Getter @Setter
    protected MenuFiller defaultFiller = Filler.from(this);

    @Setter @Getter
    protected boolean dynamicSizing = false, updating = false, updateStatesOnUpdate = false;

    boolean hasAnimationsStarted = false;

    @Getter
    protected boolean changed = false;

    protected int rows = 1, size;

    protected ItemData data;
    protected ItemResponse[] slotActions;

    @Getter
    final List<Animation> animations = new ArrayList<>(5);

    protected static final Plugin plugin;

    static {
        plugin = JavaPlugin.getProvidingPlugin(Menu.class);
        ItemNbt.wrapper(VersionHelper.IS_PDC_VERSION ? new Pdc(plugin) : new LegacyNbt());
        Bukkit.getPluginManager().registerEvents(new MenuListeners(plugin), plugin);
    }

    @Setter Consumer<ClickActionEvent> outsideClickAction = event -> {}, bottomClickAction = event -> {}, topClickAction = event -> {}, clickAction = event -> {};
    @Setter BiConsumer<InventoryCloseEvent, Result> closeAction = (event, result) -> {};
    @Setter Consumer<InventoryOpenEvent> openAction = event -> {};
    @Setter Consumer<InventoryDragEvent> dragAction = event -> {};
    @Setter Consumer<BeforeAnimatingEvent> onAnimate = event -> {};

    @Override
    public int rows() { return rows; }

    @Override
    public int size() { return size; }

    Menu(int rows, @NotNull TextHolder title, @NotNull EnumSet<Modifier> modifiers, boolean colorize) {
        this.modifiers = modifiers;
        this.rows = rows;
        this.type = MenuType.CHEST;
        this.title = title;
        this.size = rows * 9;
        this.data = new ItemData(this);
        this.slotActions = new ItemResponse[size];
        this.inventory = this.title.toInventory(this, size);
    }

    Menu(@NotNull MenuType type, @NotNull TextHolder title, @NotNull EnumSet<Modifier> modifiers, boolean colorize) {
        this.type = type;
        this.modifiers = modifiers;
        this.title = title;
        this.size = type.getLimit();
        this.data = new ItemData(this);
        this.slotActions = new ItemResponse[size];
        this.inventory = this.title.toInventory(this, type.getType());
    }

    public MenuFiller getFiller() { return defaultFiller; }

    public <T extends MenuFiller> T getFiller(@NotNull Class<T> value) { return value.cast(defaultFiller); }

    @NotNull
    public MenuIterator iterator() { return new MenuIterator(IterationDirection.HORIZONTAL, this); }

    public MenuIterator iterator(IterationDirection direction) {
        return new MenuIterator(direction, this);
    }

    public MenuIterator iterator(int startingRow, int startingCol, IterationDirection direction) {
        return new MenuIterator(startingRow, startingCol, direction, this);
    }

    public Stream<MenuItem> stream() { return Arrays.stream(data.getItems()); }

    public Stream<MenuItem> parallelStream() { return stream().parallel(); }

    public void forEach(Consumer<? super MenuItem> action) { data.forEach(action); }

    public List<HumanEntity> getViewers() {
        return inventory.getViewers();
    }

    public boolean addItem(@NotNull final ItemStack... items) {
        return (changed = data.addItem(items));
    }

    public boolean addItem(@NotNull final MenuItem... items) {
        return (changed = data.addItem(items));
    }

    public boolean addItem(@NotNull final List<MenuItem> items) {
        return (changed = addItem(items.toArray(new MenuItem[0])));
    }

    public void setItem(@NonNull Slot position, ItemStack item) {
        this.data.setItem(position, MenuItem.of(item));
        changed = true;
    }

    public void setItem(@NonNull Slot position, MenuItem item) {
        this.data.setItem(position, item);
        changed = true;
    }

    public void setItem(int slot, ItemStack item) {
        this.data.setItem(slot, MenuItem.of(item));
        changed = true;
    }

    public void setItem(int slot, MenuItem item) {
        this.data.setItem(slot, item);
        changed = true;
    }

    public @Nullable MenuItem getItem(int i) {
        return this.data.getItem(i);
    }

    public Optional<MenuItem> get(int i) {
        return Optional.ofNullable(getItem(i));
    }

    public @Nullable MenuItem getItem(@NonNull Slot position) {
        return data.getItem(position);
    }

    public Optional<MenuItem> get(@NonNull Slot position) {
        return Optional.ofNullable(getItem(position));
    }

    public boolean hasItem(@NonNull Slot position) {
        return position.isValid() && hasItem(position.slot);
    }

    public boolean hasItem(int slot) {
        return this.data.hasItem(slot);
    }

    public boolean hasItem(ItemStack item) {
        return data.getItem(itemOne -> itemOne.getItemStack().equals(item)) != null;
    }

    public boolean hasItem(MenuItem item) {
        return this.getItem(itemOne -> itemOne.equals(item)) != null;
    }

    public Optional<MenuItem> get(Predicate<MenuItem> itemDescription) {
        return Optional.ofNullable(getItem(itemDescription));
    }

    public void addSlotAction(int slot, ItemResponse response) {
        slotActions[slot] = response;
    }

    public void addSlotAction(@NotNull Slot slot, ItemResponse response) {
        if (slot.isValid()) slotActions[slot.slot] = response;
    }

    public void removeSlotAction(int slot, ItemResponse response) {
        slotActions[slot] = response;
    }

    public void removeSlotAction(@NotNull Slot slot, ItemResponse response) {
        if (slot.isValid()) slotActions[slot.slot] = response;
    }

    public @Nullable MenuItem getItem(Predicate<MenuItem> itemDescription) {
        return data.findFirst(itemDescription);
    }

    public void removeItem(@NotNull final ItemStack... itemStacks) {
        removeItemStacks(List.of(itemStacks));
    }

    public void removeItemStacks(@NotNull final List<ItemStack> itemStacks) {
        Set<ItemStack> set = ImmutableSet.copyOf(itemStacks);
        data.indexed((item, index) -> { if (set.contains(item.getItemStack())) this.data.removeItem(index); });
        changed = true;
    }

    public void removeItem(@NotNull final MenuItem... items) {
        Set<MenuItem> set = ImmutableSet.copyOf(items);
        data.indexed((item, index) -> { if (set.contains(item)) this.data.removeItem(index); });
        changed = true;
    }

    @Override
    public void removeItem(@NotNull final List<MenuItem> itemStacks) {
        Set<MenuItem> set = ImmutableSet.copyOf(itemStacks);
        data.indexed((item, index) -> { if (set.contains(item)) this.data.removeItem(index); });
        changed = true;
    }

    @Contract("_ -> new")
    @CanIgnoreReturnValue
    public MenuItem removeItem(int index) {
        MenuItem item = this.data.removeItem(index);
        if (item != null) changed = true;
        return item;
    }

    @Contract("_ -> new")
    @CanIgnoreReturnValue
    public MenuItem removeItem(@NotNull Slot position) {
        MenuItem item = (position.isValid()) ? this.data.removeItem(position.slot) : null;
        if (item != null) changed = true;
        return item;
    }

    @Override
    public void update() {
        if (!changed) return;
        this.updating = true;
        updatePlayerInventories(inventory, player -> ((Player) player).updateInventory());
        this.updating = false;
    }

    public void updatePer(long repeatTime) {
        SCHEDULER.runTaskTimer(plugin, this::update, 0, repeatTime);
    }

    public void updatePer(@NotNull Duration repeatTime) {
        SCHEDULER.runTaskTimer(plugin, this::update, 0, repeatTime.toMillis() / 50);
    }

    public void updatePer(long delay, long repeatTime) {
        SCHEDULER.runTaskTimer(plugin, this::update, delay, repeatTime);
    }

    public void updatePer(@NotNull Duration delay, @NotNull Duration repeatTime) {
        SCHEDULER.runTaskTimer(plugin, this::update, delay.toMillis() / 50, repeatTime.toMillis() / 50);
    }

    public void updateTitle(String title) {
        updateTitle(TextHolder.of(title));
    }

    public void updateTitle(TextHolder title) {
        Inventory oldInventory = inventory, updatedInventory = copyInventory(type, title, this, rows);
        this.updating = true;
        this.inventory = updatedInventory;
        updatePlayerInventories(oldInventory, player -> player.openInventory(updatedInventory));
        this.updating = false;
    }

    private void updatePlayerInventories(@NotNull Inventory oldInventory, Consumer<HumanEntity> entityPredicate) {
        data.recreateItems(inventory);
        oldInventory.getViewers().forEach(entityPredicate);
    }

    public void open(@NotNull HumanEntity entity) {
        if (!entity.isSleeping()) entity.openInventory(inventory);
    }

    public void close(@NotNull final HumanEntity player) {
         if (!inventory.equals(player.getOpenInventory().getTopInventory())) return;
        SCHEDULER.runTaskLater(plugin, player::closeInventory, 2L);
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
        data.updateItem(slot, itemStack, this.data.getItem(slot));
    }

    public void updateItem(@NotNull Slot position, @NotNull final ItemStack itemStack) {
        if (position.isValid()) updateItem(position.slot, itemStack);
    }

    public void setContents(MenuItem... items) {
        changed = true;
        this.data.contents(items);
    }

    @NotNull
    public MenuItem[] getItems() { return this.data.getItems(); }

    public @NotNull ItemData getData() { return new ItemData(this.data); }

    public @NotNull @Unmodifiable List<MenuItem> getItemList() { return ImmutableList.copyOf(getItems()); }

    public @NotNull @Unmodifiable Map<Integer, MenuItem> getItemMap() { return copyItems(new HashMap<>(size)); }

    public @NotNull @Unmodifiable Map<Integer, MenuItem> getLinkedItemMap() { return copyItems(new LinkedHashMap<>(size)); }

    public @NotNull @Unmodifiable Map<Integer, MenuItem> getConcurrentItemMap() { return copyItems(new ConcurrentHashMap<>(size)); }

    @Override
    public boolean hasAnimations() { return !animations.isEmpty(); }

    @Override
    public void addAnimation(Animation animation) { animations.add(animation); }

    @Override
    public void removeAnimation(Animation animation) { animations.remove(animation); }

    @Override
    public void removeAnimation(int animationIndex) { animations.remove(animationIndex); }

    @Override
    public boolean hasAnimationsStarted() { return this.hasAnimationsStarted; }

    public void clear() { data = new ItemData(this); }

    public MenuData getMenuData() { return MenuData.intoData(this); }

    public @NotNull @Contract("_, _ -> new") static Menu create(String title, int rows) {
        return create(TextHolder.of(title), rows, EnumSet.noneOf(Modifier.class));
    }

    public static @NotNull @Contract("_, _, _ -> new") Menu create(String title, int rows, EnumSet<Modifier> modifiers) {
        return create(TextHolder.of(title), rows, modifiers);
    }

    public static @NotNull @Contract("_, _ -> new") Menu create(String title, MenuType type) {
        return create(TextHolder.of(title), type, EnumSet.noneOf(Modifier.class));
    }

    public static @NotNull @Contract("_, _, _ -> new") Menu create(String title, MenuType type, EnumSet<Modifier> modifiers) {
        return create(TextHolder.of(title), type, modifiers);
    }

    @NotNull @Contract("_, _ -> new")
    public static Menu create(TextHolder title, int rows) {
        return new Menu(rows, title, EnumSet.noneOf(Modifier.class), true);
    }

    public static @NotNull @Contract("_, _, _ -> new") Menu create(TextHolder title, int rows, EnumSet<Modifier> modifiers) {
        return new Menu(rows, title, modifiers, true);
    }

    public static @NotNull @Contract("_, _ -> new") Menu create(TextHolder title, MenuType type) {
        return new Menu(type, title, EnumSet.noneOf(Modifier.class), true);
    }

    public static @NotNull @Contract("_, _, _ -> new") Menu create(TextHolder title, MenuType type, EnumSet<Modifier> modifiers) {
        return new Menu(type, title, modifiers, true);
    }

    public static @NotNull Menu create(@NotNull MenuData menuData) {
        return menuData.intoMenu();
    }

    public @NotNull @Override PaginatedMenu pagination(int pages) {
        PaginatedMenu menu = PaginatedMenu.create(title, rows, pages, modifiers);
        menu.setContents(data);
        return menu;
    }

    @Override
    public boolean allModifiersAdded() { return modifiers.size() == 4; }

    public void recreateInventory() {
        rows++;
        size = rows * 9;
        inventory = copyInventory(type, title, this, size);
        data.recreateInventory();
    }

    private static @NotNull Inventory copyInventory(@NotNull MenuType type, @NotNull TextHolder title, Menu menu, int size) {
        return type == MenuType.CHEST ? title.toInventory(menu, size) : title.toInventory(menu, type.getType());
    }

    public Menu copy() { return create(getMenuData()); }

    public @NotNull @Contract(" -> new") static MenuBuilder builder() { return new MenuBuilder(); }

    private @NotNull @UnmodifiableView Map<Integer, MenuItem> copyItems(Map<Integer, MenuItem> items) {
        for (int i = 0; i < size; i++) items.put(i, data.getItem(i));
        return Collections.unmodifiableMap(items);
    }

    @Override
    public String getTitle() { return title.toString(); }

    @Override
    public TextHolder title() { return title; }
}
