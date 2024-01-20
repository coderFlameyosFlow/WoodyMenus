package me.flame.menus.menu;

import me.flame.menus.items.MenuItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("unused")
public interface Pagination {
    /**
     * Goes to the next page
     *
     * @return False if there is no next page.
     */
    boolean next();

    /**
     * Goes to the previous page if possible
     *
     * @return False if there is no previous page.
     */
    boolean previous();

    /**
     * Goes to the specified page
     *
     * @return False if there is no next page.
     */
    boolean page(int pageNum);

    @Nullable ItemData getPage(int index);

    Optional<ItemData> getOptionalPage(int index);

    void addPageItems(MenuItem... items);

    void addPageItems(ItemStack... items);

    void setPageItem(Slot slot, MenuItem item);

    void removePageItem(Slot slot);

    void removePageItem(int slot);

    void removePageItem(ItemStack slot);

    void removePageItem(MenuItem slot);

    void removePageItem(ItemStack... slot);

    void removePageItem(MenuItem... slot);

    void setPageItem(int[] slots, MenuItem[] items);

    void setPageItem(int slot, ItemStack item);

    void setPageItem(Slot slot, ItemStack item);
    
    void setPageItem(Slot[] slots, ItemStack... items);

    void setPageItem(Slot[] slots, ItemStack item);

    /**
     * Gets the current page number
     *
     * @return The current page number
     */
    int getCurrentPageNumber();

    /**
     * Gets the number of pages the GUI has
     *
     * @return The number of pages
     */
    int getPagesSize();

    @NotNull ItemData getCurrentPage();
}
