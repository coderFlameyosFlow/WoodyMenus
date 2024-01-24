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
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.Consumer;

/**
 * Menu that allows you to have multiple pages
 * <p>1.1.0: PaginatedMenu straight out of Triumph-GUIS</p>
 * <p>1.4.0: PaginatedMenu rewritten as List<Page></p>
 * <p>2.0.0: PaginatedMenu rewritten as List<ItemData> instead to improve DRY and reduce it by about 250+ lines</p>
 * @since 2.0.0
 * @author FlameyosFlow
 */
@SuppressWarnings("unused")
public final class PaginatedMenu extends Menu implements Pagination {
    @NotNull
    final List<ItemData> pages;

    private int pageNumber;

    private @Getter int nextItemSlot = -1, previousItemSlot = -1;

    @Setter Consumer<PageChangeEvent> onPageChange = event -> {};

    private final MenuFiller pageDecorator = PageDecoration.create(this);

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

        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++)
            pages.add(new ItemData(this));
        this.data = pages.get(pageNumber);
    }

    /**
     * Main constructor to provide a way to create PaginatedMenu
     */
    private PaginatedMenu(MenuType type, final int pageCount, TextHolder title, EnumSet<Modifier> modifiers) {
        super(type, title, modifiers, true);
        this.pages = new ArrayList<>(pageCount);

        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++)
            pages.add(new ItemData(this));
        this.data = pages.get(pageNumber);
    }

    public ImmutableList<ItemData> pages() { return ImmutableList.copyOf(pages); }

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
        Menu menu = data.intoMenu();
        try { return (PaginatedMenu) menu; } catch (ClassCastException error) {
            throw new IllegalArgumentException(
                "Attempted to create a PaginatedMenu from an incompatible MenuData object." +
                "\nExpected PaginatedMenu, but got " + data.getClass().getSimpleName() +
                "\nFix: MenuData must include the size of the pages, or it'll default to 1."
            );
        }
    }

    public void recreateInventory() {
        super.recreateInventory();
        pages.forEach((data) -> {
            if (data != this.data) data.recreateInventory();
        });
    }

    @Override
    public void setContents(MenuItem... items) {
        ItemData itemData = new ItemData(this);
        itemData.contents(items);
        pages.set(pageNumber, itemData);
        data = itemData;
    }

    /**
     * Sets the next page item for the given slot with the provided item stack.
     *
     * @param  slot        the position of the slot
     * @param  item        the item stack to set
     * @deprecated Use {@link MenuBuilder#nextPageItem(int, MenuItem)}
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
     * @deprecated Use {@link MenuBuilder#nextPageItem(Slot, MenuItem)}
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
     * @deprecated Use {@link MenuBuilder#previousPageItem(int, MenuItem)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.1.0")
    public void setPreviousPageItem(int slot, @NotNull MenuItem item) {
        this.previousItemSlot = slot;
        pages.forEach(page -> page.setItem(slot, item));
    }

    /**
     * Sets the previous page item at the given position with the specified menu item.
     *
     * @param  pos   the position of the slot
     * @param  item  the menu item to set
     * @deprecated Use {@link MenuBuilder#previousPageItem(Slot, MenuItem)}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.1.0")
    public void setPreviousPageItem(@NotNull Slot pos, @NotNull MenuItem item) {
        int slot = pos.slot;
        this.previousItemSlot = slot;
        pages.forEach(page -> page.setItem(slot, item));
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

        this.pageNumber = openPage;
        this.data = pages.get(openPage);
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
        return pageNumber + 1;
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
        if (pageNumber + 1 >= size) return false;
        int oldPageNum = pageNumber;

        pageNumber++;
        this.data = pages.get(pageNumber);

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
        if (pageNumber - 1 < 0) return false;

        pageNumber--;
        this.data = pages.get(pageNumber);

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

        this.pageNumber = pageNum;
        this.data = pages.get(pageNum);
        update();
        return true;
    }

    @Override
    public @Nullable ItemData getPage(int index) {
        return (index < 0 || index > pages.size()) ? null : pages.get(index);
    }

    @Override
    public Optional<ItemData> getOptionalPage(int index) {
        return (index < 0 || index > pages.size()) ? Optional.empty() : Optional.ofNullable(getPage(index));
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
        for (ItemData page : pages) page.indexed((item, index) -> { if (set.contains(item.getItemStack())) page.removeItem(index); });
    }

    @Override
    public void removePageItem(MenuItem... slot) {
        Set<MenuItem> set = ImmutableSet.copyOf(slot);
        for (ItemData page : pages) page.indexed((item, index) -> { if (set.contains(item)) page.removeItem(index); });
    }

    @Override
    public void setPageItem(int[] slots, MenuItem[] items) {
        int size = slots.length;
        for (ItemData page : pages) setPageItem0(page, size, slots, items);
    }

    private static void setPageItem0(ItemData page, int size, int[] slots, MenuItem[] items) {
        for (int i = 0; i < size; i++) page.setItem(slots[i], items[i]);
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
        for (int i = 0; i < size; i++) page.setItem(slots[i], MenuItem.of(items[i]));
    }

    @Override
    public void setPageItem(Slot[] slots, ItemStack item) {
        for (ItemData page : pages) setPageItem0(page, slots.length, slots, item);
    }

    private static void setPageItem0(ItemData page, int size, Slot[] slots, ItemStack item) {
        for (Slot slot : slots) page.setItem(slot, MenuItem.of(item));
    }

    @Override
    public @NotNull MenuData getMenuData() { return MenuData.intoData(this); }

    public PaginatedMenu copy() {
        return create(getMenuData());
    }

    public void setContents(ItemData data) {
        this.data = data;
    }
}
