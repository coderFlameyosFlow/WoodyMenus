package me.flame.menus.menu;

import lombok.var;
import me.flame.menus.items.MenuItem;
import me.flame.menus.modifiers.Modifier;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class PaginatedMenu extends BaseMenu<PaginatedMenu> {

    // List with all the page items
    private final List<MenuItem> pageItems = new ArrayList<>();
    // Saves the current page items and it's slot
    private final Map<Integer, MenuItem> currentPage;

    private int pageSize;
    private int pageNum = 1;

    public PaginatedMenu(final int rows, final int pageSize, @NotNull final String title, @NotNull final EnumSet<Modifier> modifiers) {
        super(rows, title, modifiers);
        this.pageSize = pageSize;
        int inventorySize = rows * 9;
        this.currentPage = new LinkedHashMap<>(inventorySize);
    }

    /**
     * Get an optional item from the page
     * @return an optional containing an item or empty optional
     */
    public Optional<MenuItem> getOptionalFromPageItems(Predicate<MenuItem> itemPredicate) {
        for (var item : pageItems) {
            if (!itemPredicate.test(item)) continue;
            return Optional.of(item);
        }
        return Optional.empty();
    }

    /**
     * Get an optional item from the page
     * @return an optional containing an item or empty optional
     */
    public Optional<MenuItem> getOptionalFromCurrentPage(Predicate<MenuItem> itemPredicate) {
        for (var entry : currentPage.entrySet()) {
            var item = entry.getValue();
            if (!itemPredicate.test(item)) continue;
            return Optional.of(item);
        }
        return Optional.empty();
    }

    /**
     * Get an optional item from the page
     * @return an optional containing an item or empty optional
     */
    public Optional<MenuItem> getOptionalFromPageItems(int index) {
        try {
            return Optional.of(pageItems.get(index));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    /**
     * Get an optional item from the page
     * @return an optional containing an item or empty optional
     */
    public Optional<MenuItem> getOptionalFromCurrentPage(int index) {
        return Optional.ofNullable(currentPage.get(index));
    }

    /**
     * Get an optional item from the page
     * @return an optional containing an item or empty optional
     */
    public MenuItem getFromPageItems(Predicate<MenuItem> itemPredicate) {
        for (var item : pageItems) {
            if (!itemPredicate.test(item)) continue;
            return item;
        }
        return null;
    }

    /**
     * Get an optional item from the page
     * @return an optional containing an item or empty optional
     */
    public @Nullable MenuItem getFromCurrentPage(Predicate<MenuItem> itemPredicate) {
        for (var entry : currentPage.entrySet()) {
            var item = entry.getValue();
            if (!itemPredicate.test(item)) continue;
            return item;
        }
        return null;
    }

    /**
     * Get an optional item from the page
     * @return an optional containing an item or empty optional
     */
    public @Nullable MenuItem getFromPageItems(int index) {
        try {
            return pageItems.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Get an optional item from the page
     * @return an optional containing an item or empty optional
     */
    public @Nullable MenuItem getFromCurrentPage(int index) {
        return currentPage.get(index);
    }


    /**
     * Sets the page size
     *
     * @param pageSize The new page size
     * @return The GUI for easier use when declaring, works like a builder
     */
    public PaginatedMenu setPageSize(final int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Adds an {@link MenuItem} to the next available slot in the page area
     *
     * @param item The {@link MenuItem} to add to the page
     * @return the object for chaining
     */
    public PaginatedMenu addItem(@NotNull final MenuItem item) {
        pageItems.add(item);
        return this;
    }

    /**
     * Overridden {@link BaseMenu#addItem(MenuItem...)} to add the items to the page instead
     *
     * @param items Varargs for specifying the {@link MenuItem}s
     */
    @Override
    public PaginatedMenu addItem(@NotNull final MenuItem... items) {
        pageItems.addAll(Arrays.asList(items));
        return this;
    }

    /**
     * Overridden {@link BaseMenu#update()} to use the paginated open
     * @return the object for chaining
     */
    @Override
    public PaginatedMenu update() {
        recreateItems();

        updatePage();
        return this;
    }

    /**
     * Updates the page {@link MenuItem} on the slot in the page
     * Can get the slot from {@link InventoryClickEvent#getSlot()}
     *
     * @param slot      The slot of the itemStack to update
     * @param itemStack The new {@link ItemStack}
     */
    public void updatePageItem(final int slot, @NotNull final ItemStack itemStack) {
        if (!currentPage.containsKey(slot)) return;
        final MenuItem guiItem = currentPage.get(slot);
        guiItem.setItemStack(itemStack);
        inventory.setItem(slot, guiItem.getItemStack());
    }

    /**
     * Alternative {@link #updatePageItem(int, ItemStack)} that uses <i>ROWS</i> and <i>COLUMNS</i> instead
     *
     * @param slot      The row and col of the slot
     * @param itemStack The new {@link ItemStack}
     */
    public void updatePageItem(Slot slot, @NotNull final ItemStack itemStack) {
        updateItem(slot, itemStack);
    }

    /**
     * Alternative {@link #updatePageItem(int, ItemStack)} that uses {@link MenuItem} instead
     *
     * @param slot The slot of the itemStack to update
     * @param item The new ItemStack
     */
    public void updatePageItem(final int slot, @NotNull final MenuItem item) {
        if (!currentPage.containsKey(slot)) return;
        // Gets the old itemStack and its index on the main items list
        final MenuItem oldItem = currentPage.get(slot);
        final int index = pageItems.indexOf(currentPage.get(slot));

        // Updates both lists and inventory
        currentPage.put(slot, item);
        pageItems.set(index, item);
        inventory.setItem(slot, item.getItemStack());
    }

    /**
     * Alternative {@link #updatePageItem(int, MenuItem)} that uses <i>ROWS</i> and <i>COLUMNS</i> instead
     *
     * @param slot  The row and col of the slot
     * @param item The new {@link MenuItem}
     */
    public void updatePageItem(Slot slot, @NotNull final MenuItem item) {
        updateItem(slot, item);
    }

    /**
     * Removes a given {@link MenuItem} from the page.
     *
     * @param item The {@link MenuItem} to remove.
     */
    public void removePageItem(@NotNull final MenuItem item) {
        pageItems.remove(item);
        updatePage();
    }

    /**
     * Removes a given {@link ItemStack} from the page.
     *
     * @param item The {@link ItemStack} to remove.
     */
    public void removePageItem(@NotNull final ItemStack item) {
        getOptionalFromPageItems(it -> it.getItemStack().equals(item))
                .ifPresent(this::removePageItem);
    }

    /**
     * Overrides {@link BaseMenu#open(HumanEntity)} to use the paginated populator instead
     *
     * @param player The {@link HumanEntity} to open the GUI to
     * @return the object for chaining
     */
    @Override
    public PaginatedMenu open(@NotNull final HumanEntity player) {
        open(player, 1);
        return this;
    }

    /**
     * Specific open method for the Paginated GUI
     * Uses {@link #populatePage()}
     *
     * @param player   The {@link HumanEntity} to open it to
     * @param openPage The specific page to open at
     */
    public void open(@NotNull final HumanEntity player, final int openPage) {
        if (player.isSleeping()) return;
        if (openPage <= getPagesNum() || openPage > 0) pageNum = openPage;

        inventory.clear();
        currentPage.clear();

        recreateItems();
        if (pageSize == 0) pageSize = calculatePageSize();
        populatePage();

        player.openInventory(inventory);
    }

    /**
     * Overrides {@link BaseMenu#updateTitle(String)} to use the paginated populator instead
     * Updates the title of the GUI
     * <i>This method may cause LAG if used on a loop</i>
     *
     * @param title The title to set
     * @return The GUI for easier use when declaring, works like a builder
     */
    @Override
    public PaginatedMenu updateTitle(@NotNull final String title) {
        this.updating = true;

        final List<HumanEntity> viewers = new ArrayList<>(inventory.getViewers());

        this.inventory = Bukkit.createInventory(this, inventory.getSize(), title);

        for (final HumanEntity player : viewers) {
            open(player, getPageNum());
        }

        this.updating = false;

        return this;
    }

    /**
     * Gets an immutable {@link Map} with all the current pages items
     *
     * @return The {@link Map} with all the {@link #currentPage}
     */
    @NotNull
    public Map<@NotNull Integer, @NotNull MenuItem> getCurrentPageItems() {
        return Collections.unmodifiableMap(currentPage);
    }

    /**
     * Gets an immutable {@link List} with all the page items added to the GUI
     *
     * @return The  {@link List} with all the {@link #pageItems}
     */
    @NotNull
    public List<@NotNull MenuItem> getPageItems() {
        return Collections.unmodifiableList(pageItems);
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
     * Gets the next page number
     *
     * @return The next page number or {@link #pageNum} if no next is present
     */
    public int getNextPageNum() {
        if (pageNum + 1 > getPagesNum()) return pageNum;
        return pageNum + 1;
    }

    /**
     * Gets the previous page number
     *
     * @return The previous page number or {@link #pageNum} if no previous is present
     */
    public int getPrevPageNum() {
        if (pageNum - 1 == 0) return pageNum;
        return pageNum - 1;
    }

    /**
     * Goes to the next page
     *
     * @return False if there is no next page.
     */
    public boolean next() {
        if (pageNum + 1 > getPagesNum()) return false;

        pageNum++;
        updatePage();
        return true;
    }

    /**
     * Goes to the previous page if possible
     *
     * @return False if there is no previous page.
     */
    public boolean previous() {
        if (pageNum - 1 == 0) return false;

        pageNum--;
        updatePage();
        return true;
    }

    /**
     * Gets the items in the page
     *
     * @param givenPage The page to get
     * @return A list with all the page items
     */
    private List<MenuItem> getPageNum(final int givenPage) {
        final int page = givenPage - 1;

        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, pageItems.size());

        return pageItems.subList(startIndex, endIndex);
    }

    /**
     * Gets the number of pages the GUI has
     *
     * @return The pages number
     */
    public int getPagesNum() {
        return (int) Math.ceil((double) pageItems.size() / pageSize);
    }

    /**
     * Populates the inventory with the page items
     */
    private void populatePage() {
        // Adds the paginated items to the page
        for (final MenuItem guiItem : getPageNum(pageNum)) {
            for (int slot = 0; slot < size; slot++) {
                if (itemMap.get(slot) != null || inventory.getItem(slot) != null) continue;
                currentPage.put(slot, guiItem);
                inventory.setItem(slot, guiItem.getItemStack());
                break;
            }
        }
    }

    /**
     * Gets the current page items to be used on other gui types
     *
     * @return The {@link Map} with all the {@link #currentPage}
     */
    Map<Integer, MenuItem> getMutableCurrentPageItems() {
        return currentPage;
    }

    /**
     * Clears the page content
     */
    void clearPage() {
        for (Map.Entry<Integer, MenuItem> entry : currentPage.entrySet()) {
            inventory.setItem(entry.getKey(), null);
        }
    }

    /**
     * Clears all previously added page items
     */
    public void clearPageItems(final boolean update) {
        pageItems.clear();
        if (update) update();
    }

    public void clearPageItems() {
        clearPageItems(false);
    }


    /**
     * Gets the page size
     *
     * @return The page size
     */
    int getPageSize() {
        return pageSize;
    }

    /**
     * Gets the page number
     *
     * @return The current page number
     */
    int getPageNum() {
        return pageNum;
    }

    /**
     * Sets the page number
     *
     * @param pageNum Sets the current page to be the specified number
     */
    void setPageNum(final int pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * Updates the page content
     */
    void updatePage() {
        clearPage();
        populatePage();
    }

    /**
     * Calculates the size of the give page
     *
     * @return The page size
     */
    int calculatePageSize() {
        int counter = 0;
        for (int slot = 0; slot < rows * 9; slot++) {
            if (inventory.getItem(slot) == null) counter++;
        }
        return counter;
    }

}
