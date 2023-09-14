package me.flame.menus.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.flame.menus.items.MenuItem;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Getter
@SuppressWarnings("unused")
public final class Pages {
    private @Setter(AccessLevel.PACKAGE) List<Page> pages;
    private @Setter Page currentPage;
    private String title;
    int size, rows;

    private Pages(String title, int rows, int pageCount) {
        this.rows = rows;
        this.title = title;
        this.size = rows * 9;
        this.pages = new ArrayList<>(pageCount);
    }

    private Pages(List<Page> pages, String title, int rows, int pageCount, Consumer<Pages> applier) {
        this.rows = rows;
        this.title = title;
        this.size = rows * 9;
        this.pages = new ArrayList<>(pageCount);
        applier.accept(this);
    }

    @NotNull
    public static Pages create(String title, int rows, int pageCount) {
        return new Pages(title, rows, pageCount);
    }

    @NotNull
    public static Pages create(List<Page> pages, String title, int rows, int pageCount, Consumer<Pages> applier) {
        return new Pages(pages, title, rows, pageCount, applier);
    }

    public void add(Page page) {
        pages.add(page);
    }

    public Page get(int index) {
        return pages.get(index);
    }

    public Optional<Page> getOptional(int index) {
        try {
            return Optional.ofNullable(pages.get(index));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    public void addPageItems(MenuItem... items) {
        for (Page page : pages) {
            page.addItem(items);
        }
    }
    
    public void addPageItems(ItemStack... items) {
        for (Page page : pages) {
            page.addItem(items);
        }
    }

    public void setPageItem(Slot slot, MenuItem item) {
        for (Page page : pages) {
            page.setItem(slot, item);
        }
    }

    public void setPageItem(int[] slots, MenuItem item) {
        for (Page page : pages) {
            for (int slot : slots)
                page.setItem(slot, item);
        }
    }

    public void setPageItem(Slot[] slots, MenuItem item) {
        for (Page page : pages) {
            for (Slot slot : slots)
                page.setItem(slot, item);
        }
    }

    public void removePageItem(Slot slot) {
        for (Page page : pages) {
            page.removeItem(slot);
        }
    }

    public void removePageItem(int slot) {
        for (Page page : pages) {
            page.removeItem(slot);
        }
    }

    public void removePageItem(ItemStack slot) {
        for (Page page : pages) {
            page.removeItem(slot);
        }
    }

    public void removePageItem(MenuItem slot) {
        for (Page page : pages) {
            page.removeItem(slot);
        }
    }

    public void removePageItem(ItemStack... slot) {
        for (Page page : pages) {
            page.removeItem(slot);
        }
    }

    public void removePageItem(MenuItem... slot) {
        for (Page page : pages) {
            page.removeItem(slot);
        }
    }

    public void setPageItem(int startingIndex, int[] slots, MenuItem items) {
        int size = slots.length;
        for (Page page : pages) {
            for (int i = startingIndex; i < size; i++) {
                page.setItem(slots[i], items);
            }
        }
    }

    public void setPageItem(int startingIndex, Slot[] slots, MenuItem items) {
        for (Page page : pages) {
            for (int i = startingIndex; i < size; i++) {
                page.setItem(slots[i], items);
            }
        }
    }

    public void setPageItem(int slot, ItemStack item) {
        for (Page page : pages) {
            page.setItem(slot, item);
        }
    }

    public void setPageItem(Slot slot, ItemStack item) {
        for (Page page : pages) {
            page.setItem(slot, item);
        }
    }
    
    public void setPageItem(int startingIndex, int[] slots, ItemStack items) {
        
        for (Page page : pages) {
            for (int i = startingIndex; i < size; i++) {
                page.setItem(slots[i], items);
            }
        }
    }

    public void setPageItem(int startingIndex, Slot[] slots, ItemStack... items) {
        
        for (Page page : pages) {
            for (int i = startingIndex; i < size; i++) {
                page.setItem(slots[i], items[i]);
            }
        }
    }

    public void setPageItem(Slot[] slots, ItemStack item) {
        for (Page page : pages) {
            for (Slot slot : slots) {
                page.setItem(slot, item);
            }
        }
    }

    public void setPageItem(int[] slots, ItemStack item) {
        for (Page page : pages) {
            for (int slot : slots) {
                page.setItem(slot, item);
            }
        }
    }

    void setTitle(String title) {
        this.title = title;
    }
}
