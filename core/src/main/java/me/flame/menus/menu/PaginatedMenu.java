package me.flame.menus.menu;

import com.google.common.collect.ImmutableList;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import me.flame.menus.adventure.TextHolder;
import me.flame.menus.events.PageChangeEvent;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.fillers.*;
import me.flame.menus.modifiers.Modifier;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Menu that allows you to have multiple pages
 * <p>1.1.0: PaginatedMenu straight out of Triumph-GUIS</p>
 * <p>1.4.0: PaginatedMenu rewritten as List<Page></p>
 * <p>2.0.0: PaginatedMenu rewritten as List<ItemData> instead to improve DRY</p>
 * @since 2.0.0
 * @author FlameyosFlow
 */
@SuppressWarnings("unused")
public final class PaginatedMenu extends Menu implements Pagination {
    @NotNull
    final List<ItemData> pages;

    private int pageNum;

    @Getter
    private int nextItemSlot = -1, previousItemSlot = -1;

    @Setter Consumer<PageChangeEvent> onPageChange = event -> {};

    private final MenuFiller pageDecorator = PageDecoration.create(this);

    @Override
    public void updateTitle(String title) {
        updateTitle(TextHolder.of(title));
    }

    public void updateTitle(TextHolder title) {
        Inventory oldInventory = this.inventory;
        Inventory updatedInventory = type == MenuType.CHEST
                ? title.toInventory(this, size)
                : title.toInventory(this, type.getType());
        this.title = title;
        this.inventory = updatedInventory;

        this.updating = true;
        List<HumanEntity> entities = ImmutableList.copyOf(oldInventory.getViewers());
        entities.forEach(e -> e.openInventory(updatedInventory));
        this.updating = false;
    }

    public <T extends MenuFiller> T getPageDecorator(Class<T> pageClass) {
        return pageClass.cast(pageDecorator);
    }

    public PageDecoration getPageDecorator() {
        return getPageDecorator(PageDecoration.class);
    }

    public void addPage() {
        pages.add(new ItemData(this));
    }

    /**
     * Main constructor to provide a way to create PaginatedMenu
     *
     * @param pageRows The page size.
     */
    private PaginatedMenu(final int pageRows, final int pageCount, TextHolder title, EnumSet<Modifier> modifiers) {
        super(pageRows, title, modifiers, true);
        this.pages = new ArrayList<>(pageCount);

        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
            pages.add(new ItemData(this));
        }
        this.data = pages.get(pageNum);
    }

    /**
     * Main constructor to provide a way to create PaginatedMenu
     */
    private PaginatedMenu(MenuType type, final int pageCount, TextHolder title, EnumSet<Modifier> modifiers) {
        super(type, title, modifiers, true);
        this.pages = new ArrayList<>(pageCount);

        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
            pages.add(new ItemData(this));
        }
        this.data = pages.get(pageNum);
    }

    public ImmutableList<ItemData> pages() {
        return ImmutableList.copyOf(pages);
    }

    @NotNull
    public static PaginatedMenu create(String title, int rows, int pages) {
        return new PaginatedMenu(rows, pages, TextHolder.of(title), EnumSet.noneOf(Modifier.class));
    }

    @NotNull
    public static PaginatedMenu create(String title, MenuType type, int pages) {
        return new PaginatedMenu(type, pages, TextHolder.of(title), EnumSet.noneOf(Modifier.class));
    }

    @NotNull
    public static PaginatedMenu create(String title, int rows, int pages, EnumSet<Modifier> modifiers) {
        return new PaginatedMenu(rows, pages, TextHolder.of(title), modifiers);
    }

    @NotNull
    public static PaginatedMenu create(String title, MenuType type, int pages, EnumSet<Modifier> modifiers) {
        return new PaginatedMenu(type, pages, TextHolder.of(title), modifiers);
    }

    @NotNull
    public static PaginatedMenu create(TextHolder title, int rows, int pages) {
        return new PaginatedMenu(rows, pages, title, EnumSet.noneOf(Modifier.class));
    }

    @NotNull
    public static PaginatedMenu create(TextHolder title, MenuType type, int pages) {
        return new PaginatedMenu(type, pages, title, EnumSet.noneOf(Modifier.class));
    }

    @NotNull
    public static PaginatedMenu create(TextHolder title, int rows, int pages, EnumSet<Modifier> modifiers) {
        return new PaginatedMenu(rows, pages, title, modifiers);
    }

    @NotNull
    public static PaginatedMenu create(TextHolder title, MenuType type, int pages, EnumSet<Modifier> modifiers) {
        return new PaginatedMenu(type, pages, title, modifiers);
    }

    public static @NotNull PaginatedMenu create(MenuData data) {
        PaginatedMenu menu = data.getType() == MenuType.CHEST
                ? create(data.getTitle(), data.getRows(), data.getPages(), data.getModifiers())
                : create(data.getTitle(), data.getType(), data.getPages(), data.getModifiers());
        menu.setContents(data.getItems());
        return menu;
    }

    public void recreateInventory() {
        this.rows++;
        this.size = rows * 9;
        this.inventory = type == MenuType.CHEST ? title.toInventory(this, this.size)
                : title.toInventory(this, type.getType());
        pages.forEach(ItemData::recreateInventory);
    }

    @Override
    public void update() {
        this.updating = true;
        data.recreateItems(inventory);
        List<HumanEntity> entities = ImmutableList.copyOf(inventory.getViewers());
        entities.forEach(e -> ((Player) e).updateInventory());
        this.updating = false;
	}

    @Override
    public void setContents(MenuItem... items) {
        ItemData itemData = new ItemData(this);
        itemData.contents(items);
        pages.set(pageNum, itemData);
        data = itemData;
    }

    /**
     * Sets the next page item for the given slot with the provided item stack.
     *
     * @param  slot        the position of the slot
     * @param  item        the item stack to set
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.1.0")
    public void setNextPageItem(int slot, @NotNull MenuItem item) {
        this.nextItemSlot = slot;
        pages.forEach(page -> page.setItem(slot, item));
    }

    /**
     * Sets the next page item for the given slot with the provided item stack.
     *
     * @param  pos         the position of the slot
     * @param  item        the item stack to set
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.1.0")
    public void setNextPageItem(@NotNull Slot pos, @NotNull MenuItem item) {
        int slot = pos.slot;
        this.nextItemSlot = slot;
        pages.forEach(page -> page.setItem(slot, item));
    }

    /**
     * Sets the previous page item at the given position with the specified menu item.
     *
     * @param  slot  the position of the slot
     * @param  item  the menu item to set
     */
    public void setPreviousPageItem(int slot, @NotNull MenuItem item) {
        this.previousItemSlot = slot;
        pages.forEach(page -> page.setItem(slot, item));
    }

    /**
     * Sets the previous page item at the given position with the specified menu item.
     *
     * @param  pos   the position of the slot
     * @param  item  the menu item to set
     */
    public void setPreviousPageItem(@NotNull Slot pos, @NotNull MenuItem item) {
        int slot = pos.slot;
        this.previousItemSlot = slot;
        pages.forEach(page -> page.setItem(slot, item));
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
        if (!data.hasItem(slot)) return;
        final MenuItem menuItem = data.getItem(slot);

        if (menuItem == null) {
            data.setItem(slot, itemStack);
            return;
        }

        menuItem.setItemStack(itemStack);
        data.setItem(slot, menuItem.getItemStack());
    }

    /**
     * A function to check if an item exists at a given index.
     *
     * @param  index  the index to check
     * @return        true if an item exists at the given index, false otherwise
     */
    public boolean hasItem(int index) {
        return data.getItem(index) != null;
    }

    /**
     * A function to check if an item exists at a given slot.
     *
     * @param  slot   the slot to check
     * @return        true if an item exists at the given slot, false otherwise
     */
    public boolean hasItem(@NotNull Slot slot) {
        return data.getItem(slot) != null;
    }

    /**
     * A function to check if an item exists at a given item.
     *
     * @param  item   the item to check
     * @return        true if an item exists at the given item, false otherwise
     */
    public boolean hasItem(MenuItem item) {
        return getItem(it -> it.equals(item)) != null;
    }

    /**
     * A function to check if an item exists at a given item.
     *
     * @param  item   the item to check
     * @return        true if an item exists at the given item, false otherwise
     */
    public boolean hasItem(ItemStack item) {
        return data.getItem(it -> it.getItemStack().equals(item)) != null;
    }

    /**
     * Alternative {@link #updateItem(int, ItemStack)} that uses <i>ROWS</i> and <i>COLUMNS</i> instead
     *
     * @param position      The slot of the item to update
     * @param itemStack The new {@link ItemStack}
     */
    @Override
    public void updateItem(@NotNull Slot position, @NotNull final ItemStack itemStack) {
        updateItem(position.slot, itemStack);
    }

    /**
     * Alternative {@link #updateItem(int, ItemStack)} that uses {@link MenuItem} instead
     *
     * @param slot The slot of the item to update
     * @param item The new ItemStack
     */
    public void updateItem(final int slot, @NotNull final MenuItem item) {
        if (!data.hasItem(slot)) return;
        data.setItem(slot, item);
    }

    @Override
    public void removeItem(@NotNull final MenuItem... item) {
        data.removeItem(item);
    }

    /**
     * @param itemStacks the items to remove
     */
    @Override
    public void removeItem(@NotNull List<MenuItem> itemStacks) {
        data.removeItem(itemStacks.toArray(new MenuItem[0]));
    }

    @Override
    public void removeItem(@NotNull final ItemStack... item) {
        data.removeItem(item);
    }

    @Override
    public boolean addItem(MenuItem... items) {
        data.addItem(items);
        return false;
    }

    @Override
    public boolean addItem(ItemStack... items) {
        data.addItem(items);
        return false;
    }

    @Override
    public void setItem(@NotNull Slot position, MenuItem item) {
        data.setItem(position, item);
    }

    @Override
    public void setItem(int slot, ItemStack item) {
        data.setItem(slot, item);
    }

    @Override
    public void setItem(@NotNull Slot position, ItemStack item) {
        data.setItem(position, MenuItem.of(item));
    }

    @Override
    public MenuItem getItem(int slot) {
        return data.getItem(slot);
    }

    @Override
    public MenuItem getItem(@NotNull Slot slot) {
        return data.getItem(slot);
    }

    @Override
    public MenuItem getItem(@NotNull Predicate<MenuItem> item) {
        return data.getItem(item);
    }

    @Override
    public Optional<MenuItem> get(@NotNull Slot slot) {
        return data.get(slot);
    }

    @Override
    public Optional<MenuItem> get(@NotNull Predicate<MenuItem> item) {
        return data.get(item);
    }

    @Override
    public Optional<MenuItem> get(int index) {
        return data.get(index);
    }

    /**
     * Opens the GUI to a specific page for the given player
     *
     * @param player   The {@link HumanEntity} to open the GUI to
     * @param openPage The specific page to open at
     */
    public void open(@NotNull final HumanEntity player, final int openPage) {
        if (player.isSleeping()) return;

        int pagesSize = pages.size();

        if (openPage < 0 || openPage >= pagesSize) {
            throw new IllegalArgumentException(
                    "\"openPage\" out of bounds; must be 0-" + (pagesSize - 1) +
                    "\nopenPage: " + openPage +
                    "\nFix: Make sure \"openPage\" is 0-" + (pagesSize - 1)
            );
        }

        this.pageNum = openPage;
        this.data = pages.get(openPage);

        this.update();
        player.openInventory(inventory);
    }

    /**
     * Opens the GUI to a specific page for the given player
     *
     * @param player   The {@link HumanEntity} to open the GUI to
     */
    public void open(@NotNull final HumanEntity player) {
        this.open(player, 0);
    }

    /**
     * Gets the current page number
     *
     * @return The current page number
     */
    @Override
    public int getCurrentPageNumber() {
        return pageNum + 1;
    }

    /**
     * Gets the number of pages the GUI has
     *
     * @return The number of pages
     */
    @Override
    public int getPagesSize() {
        return pages.size();
    }

    /**
     * Goes to the next page
     *
     * @return False if there is no next page.
     */
    @Override
    public boolean next() {
        int size = pages.size();
        if (pageNum + 1 >= size) return false;
        int oldPageNum = pageNum;

        pageNum++;
        this.data = pages.get(pageNum);

        update();
        return true;
    }

    /**
     * Goes to the previous page if possible
     *
     * @return False if there is no previous page.
     */
    @Override
    public boolean previous() {
        if (pageNum - 1 < 0) return false;

        pageNum--;
        this.data = pages.get(pageNum);

        update();
        return true;
    }

    /**
     * Goes to the specified page
     *
     * @return False if there is no next page.
     */
    @Override
    public boolean page(int pageNum) {
        int size = pages.size();
        if (pageNum < 0 || pageNum > size) return false;

        this.pageNum = pageNum;
        this.data = pages.get(pageNum);

        update();
        return true;
    }

    @Override
    public @Nullable ItemData getPage(int index) {
        if (index < 0 || index > pages.size()) return null;
        return pages.get(index);
    }

    @Override
    public Optional<ItemData> getOptionalPage(int index) {
        if (index < 0 || index > pages.size()) return Optional.empty();
        return Optional.ofNullable(getPage(index));
    }

    @Override
    public void addPageItems(MenuItem... items) {
        for (ItemData page : pages) page.addItem(items);
    }

    @Override
    public void addPageItems(ItemStack... items) {
        for (ItemData page : pages) page.addItem(items);
    }

    @Override
    public void setPageItem(Slot slot, MenuItem item) {
        for (ItemData page : pages) page.setItem(slot, item);
    }

    @Override
    public void removePageItem(Slot slot) {
        for (ItemData page : pages) page.removeItem(slot);
    }

    @Override
    public void removePageItem(int slot) {
        for (ItemData page : pages) page.removeItem(slot);
    }

    @Override
    public void removePageItem(ItemStack slot) {
        for (ItemData page : pages) page.removeItem((item) -> item.getItemStack().equals(slot));
    }

    @Override
    public void removePageItem(MenuItem slot) {
        for (ItemData page : pages) page.removeItem((item) -> item.equals(slot));
    }

    @Override
    public void removePageItem(ItemStack... slot) {
        Set<ItemStack> set = ImmutableSet.copyOf(slot);
        for (ItemData page : pages) {
            page.indexed((item, index) -> { if (set.contains(item.getItemStack())) page.removeItem(index); });
        }
    }

    @Override
    public void removePageItem(MenuItem... slot) {
        Set<MenuItem> set = ImmutableSet.copyOf(slot);
        for (ItemData page : pages) {
            page.indexed((item, index) -> { if (set.contains(item)) page.removeItem(index); });
        }
    }

    @Override
    public void setPageItem(int[] slots, MenuItem[] items) {
        int size = slots.length;
        for (ItemData page : pages) {
            setPageItem0(page, size, slots, items);
        }
    }

    private static void setPageItem0(ItemData page, int size, int[] slots, MenuItem[] items) {
        for (int i = 0; i < size; i++) {
            page.setItem(slots[i], items[i]);
        }
    }

    @Override
    public void setPageItem(int slot, ItemStack item) {
        setPageItem(slot, MenuItem.of(item));
    }

    public void setPageItem(int slot, MenuItem item) {
        for (ItemData page : pages) page.setItem(slot, item);
    }

    @Override
    public void setPageItem(Slot slot, ItemStack item) {
        setPageItem(slot, MenuItem.of(item));
    }

    @Override
    public void setPageItem(Slot[] slots, ItemStack... items) {
        for (ItemData page : pages) setPageItem0(page, slots.length, slots, items);
    }

    private static void setPageItem0(ItemData page, int size, Slot[] slots, ItemStack... items) {
        for (int i = 0; i < size; i++) {
            page.setItem(slots[i], MenuItem.of(items[i]));
        }
    }

    @Override
    public void setPageItem(Slot[] slots, ItemStack item) {
        for (ItemData page : pages) {
            setPageItem0(page, slots.length, slots, item);
        }
    }

    private static void setPageItem0(ItemData page, int size, Slot[] slots, ItemStack item) {
        for (Slot slot : slots) {
            page.setItem(slot, MenuItem.of(item));
        }
    }

    @Override
    public @NotNull MenuData getMenuData() {
        return type == MenuType.CHEST
                ? new MenuData(title, rows, pages.size(), modifiers, data.getItems())
                : new MenuData(title, type, pages.size(), modifiers, data.getItems());
    }

    public PaginatedMenu copy() {
        return create(getMenuData());
    }

    public @NotNull ItemData getCurrentPage() {
        return new ItemData(data);
    }

    public void setContents(ItemData data) {
        this.data = data;
    }
}
