package me.flame.menus.menu;

import me.flame.menus.items.MenuItem;
import me.flame.menus.modifiers.Modifier;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Separated Menu that allows you to have multiple pages
 */
@SuppressWarnings("unused")
public final class SeparatedPagedMenu extends BaseMenu {
    @NotNull
    private final UUID uuid;

    @NotNull
    private final Player player;
    
    @NotNull
    private final PaginatedMenu mainInventory;

    int pageNum = 0, pageRows, pageSize, pages;

    @Contract("_, _ -> new")
    public static @NotNull SeparatedPagedMenu create(UUID uuid, String title) {
        return new SeparatedPagedMenu(uuid, 5, 2, title);
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull SeparatedPagedMenu create(UUID uuid, String title, int rows, int pageCount) {
        return new SeparatedPagedMenu(uuid, rows, pageCount, title);
    }

    @Contract("_, _, _, _, _ -> new")
    public static @NotNull SeparatedPagedMenu create(UUID uuid, String title, int rows, int pageCount, Consumer<SeparatedPagedMenu> menuConsumer) {
        return new SeparatedPagedMenu(uuid, rows, pageCount, title);
    }

    @Contract("_, _ -> new")
    public static @NotNull SeparatedPagedMenu create(Player player, String title) {
        return new SeparatedPagedMenu(player.getUniqueId(), 5, 2, title);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    public static SeparatedPagedMenu create(Player player, String title, int rows, int pageCount) {
        return new SeparatedPagedMenu(player.getUniqueId(), rows, pageCount, title);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    public static SeparatedPagedMenu create(UUID uuid, String title, MenuType type, int pageCount) {
        return new SeparatedPagedMenu(uuid, type, pageCount, title);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    public static SeparatedPagedMenu create(Player player, String title, MenuType type, int pageCount) {
        return new SeparatedPagedMenu(player.getUniqueId(), type, pageCount, title);
    }

    /**
     * Main constructor to provide a way to create SeparatedPagedMenu
     *
     * @param pageRows The page size.
     */
    private SeparatedPagedMenu(@NotNull UUID uuid, final int pageRows, final int pageCount, String title) {
        super(pageRows, title, EnumSet.noneOf(Modifier.class));
        this.uuid = uuid;
        this.player = Objects.requireNonNull(Bukkit.getPlayer(uuid));
        this.mainInventory = PaginatedMenu.create(title, type, pageCount, EnumSet.noneOf(Modifier.class));

        this.pageRows = pageRows;
        this.pageSize = pageRows * 9;
        this.pages = pageCount;
    }

    /**
     * Main constructor to provide a way to create SeparatedPagedMenu
     */
    private SeparatedPagedMenu(@NotNull UUID uuid, MenuType type, final int pageCount, String title) {
        super(type, title, EnumSet.noneOf(Modifier.class));
        this.uuid = uuid;
        this.player = Objects.requireNonNull(Bukkit.getPlayer(uuid));
        this.mainInventory = PaginatedMenu.create(title, type, pageCount, EnumSet.noneOf(Modifier.class));

        this.pages = pageCount;
    }

    public <T extends Decorator> T getPageDecorator(Class<T> pageClass) {
        return mainInventory.getPageDecorator(pageClass);
    }

    public PageDecoration getPageDecorator() {
        return mainInventory.getPageDecorator();
    }

    /**
     * Updates the page {@link MenuItem} on the slot in the page
     * Can get the slot from {@link InventoryClickEvent#getSlot()}
     *
     * @param slot      The slot of the item to update
     * @param itemStack The new {@link ItemStack}
     */
    @Override
    public void updateItem(final int slot, @NotNull final ItemStack itemStack) {
        mainInventory.updateItem(slot, itemStack);
    }

    /**
     * Alternative {@link #updateItem(int, ItemStack)} that uses <i>ROWS</i> and <i>COLUMNS</i> instead
     *
     * @param slot      The slot of the item to update
     * @param itemStack The new {@link ItemStack}
     */
    @Override
    public void updateItem(@NotNull Slot slot, @NotNull final ItemStack itemStack) {
        updateItem(slot.getSlot(), itemStack);
    }

    /**
     * Alternative {@link #updateItem(int, ItemStack)} that uses {@link MenuItem} instead
     *
     * @param slot The slot of the item to update
     * @param item The new ItemStack
     */
    public void updateItem(final int slot, @NotNull final MenuItem item) {
        mainInventory.updateItem(slot, item);
    }

    @Override
    public void removeItem(@NotNull final MenuItem... item) {
        mainInventory.removeItem(item);
    }

    /**
     * @param itemStacks the items to remove 
     */
    @Override
    public void removeItem(@NotNull List<MenuItem> itemStacks) {
        mainInventory.removeItem(itemStacks.toArray(new MenuItem[0]));
    }

    @Override
    public void removeItem(@NotNull final ItemStack... item) {
        mainInventory.removeItem(item);
    }

    @Override
    public void addItem(MenuItem... items) {
        mainInventory.addItem(items);
    }

    @Override
    public void addItem(ItemStack... items) {
        mainInventory.addItem(items);
    }

    @Override
    public void setItem(int slot, MenuItem item) {
        mainInventory.setItem(slot, item);
    }

    @Override
    public void setItem(@NotNull Slot slot, MenuItem item) {
        mainInventory.setItem(slot, item);
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        mainInventory.setItem(slot, item);
    }

    @Override
    public void setItem(@NotNull Slot slot, ItemStack item) {
        mainInventory.setItem(slot, item);
    }

    @Override
    public MenuItem getItem(int slot) {
        return mainInventory.getItem(slot);
    }

    @Override
    public MenuItem getItem(@NotNull Slot slot) {
        return mainInventory.getItem(slot);
    }

    @Override
    public MenuItem getItem(@NotNull Predicate<MenuItem> item) {
        return mainInventory.getItem(item);
    }

    @Override
    public Optional<MenuItem> get(@NotNull Slot slot) {
        return mainInventory.get(slot);
    }

    @Override
    public Optional<MenuItem> get(@NotNull Predicate<MenuItem> item) {
        return mainInventory.get(item);
    }

    @Override
    public Optional<MenuItem> get(int index) {
        return mainInventory.get(index);
    }

    /**
     * Opens the GUI to a specific page for the given player
     *
     * @param openPage The specific page to open at
     */
    public void open(final int openPage) {
        mainInventory.open(player, openPage);
    }

    /**
     * Opens the GUI to a specific page for the given player
     */
    public void open() {
        mainInventory.open(player);
    }

    /**
     * Goes to the next page
     *
     * @return False if there is no next page.
     */
    public boolean next() {
        return mainInventory.next();
    }

    /**
     * Goes to the previous page if possible
     *
     * @return False if there is no previous page.
     */
    public boolean previous() {
        return mainInventory.previous();
    }

    /**
     * Gets the current page number
     *
     * @return The current page number
     */
    public int getCurrentPageNum() {
        return pageNum;
    }

    /**
     * Gets the number of pages the GUI has
     *
     * @return The number of pages
     */
    public int getPagesSize() {
        return pages;
    }

    @Override
    protected void recreateItems(Inventory inventory) {
        mainInventory.recreateItems(inventory);
    }

    @Override
    public void update() {
        mainInventory.update();
    }

    /**
     * Get the unique id currently using this paginated menu for themselves.
     * @return the unique id of the player
     */
    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * Get the literal player themselves
     * @return the player
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }
}
