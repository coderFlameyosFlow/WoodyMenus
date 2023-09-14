/**
 * MIT License
 * <p>
 * Copyright (c) 2021 TriumphTeam
 * Copyright (c) 2023 FlameyosFlow
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.flame.menus.menu;

import com.google.common.collect.ImmutableList;

import me.flame.menus.items.MenuItem;
import me.flame.menus.modifiers.Modifier;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

/**
 * GUI that allows you to have multiple pages
 */
@SuppressWarnings({ "unused", "CodeBlock2Expr" })
public final class PaginatedMenu extends BaseMenu<PaginatedMenu> {
    @NotNull
    final List<Page> pageList;

    @NotNull
    private Page currentPage;

    private int pageNum;

    private final PageDecoration pageDecorator = PageDecoration.create(this);
    private final Pages pages;

    private static final BukkitScheduler sch = Bukkit.getScheduler();

    public PaginatedMenu updateTitle(String title) {
        String colorizedTitle = translateAlternateColorCodes('&', title);
        Inventory updatedInventory = type == MenuType.CHEST
                ? Bukkit.createInventory(this, size, colorizedTitle)
                : Bukkit.createInventory(this, type.getType(), colorizedTitle);
        this.title = colorizedTitle;
        pages.setTitle(title);

        this.inventory = updatedInventory;

        this.updating = true;
        List<HumanEntity> entities = ImmutableList.copyOf(inventory.getViewers());
        entities.forEach(e -> e.openInventory(updatedInventory));
        this.updating = false;
        return this;
    }

    public PageDecoration getPageDecorator() {
        return pageDecorator;
    }

    /**
     * Retrieves Pages in the PaginatedMenu and the ability to set/add/remove items from every page.
     *
     * @return  the Pages object
     */
    public Pages getPages() {
        return pages;
    }

    private PaginatedMenu(@NotNull Pages pages, EnumSet<Modifier> modifiers) {
        super(pages.rows, pages.getTitle(), modifiers);
        this.pages = pages;
        this.pageList = pages.getPages();
        for (int i = 0; i < size; i++) {
            pages.add(Page.of(this));
        }
        pages.setCurrentPage(pages.get(pageNum));
        this.currentPage = pages.getCurrentPage();
    }

    /**
     * Main constructor to provide a way to create PaginatedMenu
     *
     * @param pageRows The page size.
     */
    public PaginatedMenu(final int pageRows, final int pageCount, String title, EnumSet<Modifier> modifiers) {
        super(pageRows, title, modifiers);
        this.pageList = new ArrayList<>(pageCount);
        for (int i = 0; i < pageCount; i++) {
            pageList.add(Page.of(this));
        }
        this.currentPage = pageList.get(pageNum);
        this.pages = Pages.create(pageList, title, pageRows, pageCount, (pages) -> {
            pages.setCurrentPage(pageList.get(pageNum));
        });
    }
    /**
     * Main constructor to provide a way to create PaginatedMenu
     */
    public PaginatedMenu(MenuType type, final int pageCount, String title, EnumSet<Modifier> modifiers) {
        super(type, title, modifiers);
        this.pageList = new ArrayList<>(pageCount);
        for (int i = 0; i < pageCount; i++) {
            pageList.add(Page.of(this));
        }
        this.currentPage = pageList.get(pageNum);
        this.pages = Pages.create(pageList, title, rows, pageCount, (pages) -> {
            pages.setCurrentPage(pageList.get(pageNum));
        });
    }

    public Iterator<Page> pages() {
        return pageList.iterator();
    }

    @NotNull
    public static PaginatedMenu create(Pages pages) {
        return new PaginatedMenu(pages, EnumSet.noneOf(Modifier.class));
    }

    @NotNull
    public static PaginatedMenu create(Pages pages, EnumSet<Modifier> modifiers) {
        return new PaginatedMenu(pages, modifiers);
    }

    @NotNull
    public static PaginatedMenu create(String title, int rows, int pages) {
        return new PaginatedMenu(rows, pages, title, EnumSet.noneOf(Modifier.class));
    }

    @NotNull
    public static PaginatedMenu create(String title, MenuType type, int pages) {
        return new PaginatedMenu(type, pages, title, EnumSet.noneOf(Modifier.class));
    }

    @NotNull
    public static PaginatedMenu create(String title, int rows, int pages, EnumSet<Modifier> modifiers) {
        return new PaginatedMenu(rows, pages, title, modifiers);
    }

    @NotNull
    public static PaginatedMenu create(String title, MenuType type, int pages, EnumSet<Modifier> modifiers) {
        return new PaginatedMenu(type, pages, title, modifiers);
    }

    public void recreateInventory() {
        this.rows++;
        this.size = rows * 9;

        pages.rows = rows;
        pages.size = size;
        inventory = Bukkit.createInventory(this, size, title);
    }

    /**
     * Sets the next page item for the given slot with the provided item stack.
     *
     * @param  slot        the position of the slot
     * @param  item        the item stack to set
     */
    public void setNextPageItem(int slot, @NotNull MenuItem item) {
        item.setClickAction(e -> this.next());
        for (Page page : pageList) {
            page.setItem(slot, item);
        }
    }

    /**
     * Sets the next page item for the given slot with the provided item stack.
     *
     * @param  pos         the position of the slot
     * @param  item        the item stack to set
     */
    public void setNextPageItem(@NotNull Slot pos, @NotNull MenuItem item) {
        int slot = pos.slot;
        item.setClickAction(e -> this.next());
        for (Page page : pageList) {
            page.setItem(slot, item);
        }
    }

    /**
     * Sets the previous page item at the given position with the specified menu item.
     *
     * @param  slot  the position of the slot
     * @param  item  the menu item to set
     */
    public void setPreviousPageItem(int slot, @NotNull MenuItem item) {
        item.setClickAction(e -> this.previous());
        for (Page page : pageList) {
            page.setItem(slot, item);
        }
    }

    /**
     * Sets the previous page item at the given position with the specified menu item.
     *
     * @param  pos   the position of the slot
     * @param  item  the menu item to set
     */
    public void setPreviousPageItem(@NotNull Slot pos, @NotNull MenuItem item) {
        int slot = pos.slot;
        item.setClickAction(e -> this.previous());
        for (Page page : pageList) {
            page.setItem(slot, item);
        }
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
        if (!currentPage.hasItem(slot)) return;
        final MenuItem menuItem = currentPage.getItem(slot);

        if (menuItem == null) {
            currentPage.setItem(slot, itemStack);
            return;
        }

        menuItem.setItemStack(itemStack);
        currentPage.setItem(slot, menuItem.getItemStack());
    }

    /**
     * A function to check if an item exists at a given index.
     *
     * @param  index  the index to check
     * @return        true if an item exists at the given index, false otherwise
     */
    public boolean hasItem(int index) {
        return currentPage.getItem(index) != null;
    }

    /**
     * A function to check if an item exists at a given slot.
     *
     * @param  slot   the slot to check
     * @return        true if an item exists at the given slot, false otherwise
     */
    public boolean hasItem(@NotNull Slot slot) {
        return currentPage.getItem(slot) != null;
    }

    /**
     * A function to check if an item exists at a given item.
     *
     * @param  item   the item to check
     * @return        true if an item exists at the given item, false otherwise
     */
    public boolean hasItem(MenuItem item) {
        return currentPage.getItem(it -> it.equals(item)) != null;
    }

    /**
     * A function to check if an item exists at a given item.
     *
     * @param  item   the item to check
     * @return        true if an item exists at the given item, false otherwise
     */
    public boolean hasItem(ItemStack item) {
        return currentPage.getItem(it -> it.getItemStack().equals(item)) != null;
    }

    /**
     * Alternative {@link #updateItem(int, ItemStack)} that uses <i>ROWS</i> and <i>COLUMNS</i> instead
     *
     * @param slot      The slot of the item to update
     * @param itemStack The new {@link ItemStack}
     */
    @Override
    public void updateItem(@NotNull Slot slot, @NotNull final ItemStack itemStack) {
        updateItem(slot.slot, itemStack);
    }

    /**
     * Alternative {@link #updateItem(int, ItemStack)} that uses {@link MenuItem} instead
     *
     * @param slot The slot of the item to update
     * @param item The new ItemStack
     */
    public void updateItem(final int slot, @NotNull final MenuItem item) {
        if (!currentPage.hasItem(slot)) return;
        currentPage.setItem(slot, item);
    }

    @Override
    public void updateItem(@NotNull Slot slot, @NotNull final MenuItem item) {
        updateItem(slot.slot, item);
    }

    @Override
    public PaginatedMenu removeItems(@NotNull final MenuItem... item) {
        currentPage.removeItem(item);
        return this;
    }

    @Override
    public PaginatedMenu removeItems(@NotNull final ItemStack... item) {
        currentPage.removeItem(item);
        return this;
    }

    @Override
    public PaginatedMenu removeItem(@NotNull final ItemStack item) {
        currentPage.removeItem(item);
        return this;
    }

    @Override
    public PaginatedMenu removeItem(@NotNull final MenuItem item) {
        currentPage.removeItem(item);
        return this;
    }

    @Override
    public PaginatedMenu addItem(MenuItem... items) {
        currentPage.addItem(items);
        return this;
    }

    @Override
    public PaginatedMenu addItem(ItemStack... items) {
        currentPage.addItem(items);
        return this;
    }

    @Override
    public PaginatedMenu setItem(int slot, MenuItem item) {
        currentPage.setItem(slot, item);
        return this;
    }

    @Override
    public PaginatedMenu setItem(@NotNull Slot slot, MenuItem item) {
        currentPage.setItem(slot, item);
        return this;
    }

    @Override
    public PaginatedMenu setItem(int slot, ItemStack item) {
        currentPage.setItem(slot, item);
        return this;
    }

    @Override
    public PaginatedMenu setItem(@NotNull Slot slot, ItemStack item) {
        currentPage.setItem(slot, item);
        return this;
    }

    @Override
    public MenuItem getItem(int slot) {
        return currentPage.getItem(slot);
    }

    @Override
    public MenuItem getItem(@NotNull Slot slot) {
        return currentPage.getItem(slot);
    }

    @Override
    public MenuItem getItem(@NotNull Predicate<MenuItem> item) {
        return currentPage.getItem(item);
    }

    @Override
    public Optional<MenuItem> get(@NotNull Slot slot) {
        return currentPage.get(slot);
    }

    @Override
    public Optional<MenuItem> get(@NotNull Predicate<MenuItem> item) {
        return currentPage.get(item);
    }

    @Override
    public Optional<MenuItem> get(int index) {
        return currentPage.get(index);
    }

    /**
     * Opens the GUI to a specific page for the given player
     *
     * @param player   The {@link HumanEntity} to open the GUI to
     * @param openPage The specific page to open at
     */
    public void open(@NotNull final HumanEntity player, final int openPage) {
        if (player.isSleeping()) return;

        int pagesSize = pageList.size();

        if (openPage < 0 || openPage >= pagesSize) {
            throw new IllegalArgumentException(
                    "\"openPage\" out of bounds; must be 0-" + (pagesSize - 1) +
                    "\nopenPage: " + openPage +
                    "\nFix: Make sure \"openPage\" is 0-" + (pagesSize - 1)
            );
        }

        pageNum = openPage;
        currentPage = pageList.get(openPage);

        recreateItems();
        player.openInventory(inventory);
    }

    /**
     * Gets the current page number
     *
     * @return The current page number
     */
    public int getCurrentPageNumber() {
        return pageNum;
    }

    /**
     * Gets the number of pages the GUI has
     *
     * @return The number of pages
     */
    public int getPagesSize() {
        return pageList.size();
    }

    @Override
    protected void recreateItems() {
        final int endIndex = currentPage.size();

        for (int i = 0; i < endIndex; i++) {
            final MenuItem menuItem = currentPage.getItem(i);
            inventory.setItem(i, menuItem == null ? null : menuItem.getItemStack());
        }
    }

    /**
     * Goes to the next page
     *
     * @return False if there is no next page.
     */
    public boolean next() {
        if (pageNum + 1 >= pageList.size()) return false;

        pageNum++;

        Page page = pages.get(pageNum);
        pages.setCurrentPage(page);
        this.currentPage = page;

        recreateItems();
        return true;
    }

    /**
     * Goes to the previous page if possible
     *
     * @return False if there is no previous page.
     */
    public boolean previous() {
        if (pageNum - 1 < 0) return false;

        pageNum--;

        Page page = pages.get(pageNum);
        pages.setCurrentPage(page);
        this.currentPage = page;

        recreateItems();
        return true;
    }

    /**
     * Goes to the specified page
     *
     * @return False if there is no next page.
     */
    public boolean page(int pageNum) {
        if (pageNum > size || pageNum < 0) return false;

        this.pageNum = pageNum;

        Page page = pages.get(pageNum);
        pages.setCurrentPage(page);
        this.currentPage = page;

        recreateItems();
        return true;
    }

    @NotNull
    public Page getCurrentPage() {
        return currentPage;
    }
}
