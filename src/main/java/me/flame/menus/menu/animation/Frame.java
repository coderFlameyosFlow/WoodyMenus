package me.flame.menus.menu.animation;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.ItemData;
import me.flame.menus.menu.Menu;

import me.flame.menus.menu.Slot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * To be used in building Frames.
 * @since 2.0.0
 */
public class Frame {
    private final MenuItem[] items;
    private MenuItem[] defaultItems;
    private final Menu menu;

    private boolean started = false;

    @Contract(pure = true)
    private Frame(MenuItem @NotNull [] items, Menu menu) {
        this.menu = menu;
        this.items = items;
        this.defaultItems = new MenuItem[items.length];
    }

    @NotNull
    @CanIgnoreReturnValue
    public Frame start() {
        if (!started) {
            this.defaultItems = items;
            started = true;
        }
        menu.setContents(Arrays.copyOf(items, items.length));
        return this;
    }

    public void reset() {
        menu.setContents(defaultItems);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Builder builder(@NotNull Menu menu) {
        return new Builder(menu);
    }

    public static class Builder {
        private final ItemData data;

        private final Menu menu;

        @Contract(pure = true)
        Builder(@NotNull Menu menu) {
            this.menu = menu;
            this.data = new ItemData(menu);
        }

        @NotNull
        public Builder addItem(@NotNull MenuItem... items) {
            this.data.addItem(items);
            return this;
        }

        @NotNull
        public Builder setItem(int slot, MenuItem item) {
            this.data.setItem(slot, item);
            return this;
        }

        @NotNull
        public Builder setItem(Slot slot, MenuItem item) {
            this.data.setItem(slot, item);
            return this;
        }

        @NotNull
        public Frame build() {
            return new Frame(this.data.getItems(), this.menu);
        }
    }
}
