package me.flame.menus.menu.immutable;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.IMenu;
import me.flame.menus.menu.IterationDirection;
import me.flame.menus.menu.MenuData;
import me.flame.menus.menu.Menu;
import me.flame.menus.menu.Slot;
import me.flame.menus.menu.iterator.MenuIterator;
import me.flame.menus.modifiers.Modifier;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public final class ImmutableMenu {
    /**
     * Creates an immutable copy of the given menu, meaning you can't remove, add or set.
     * <p>
     * Existing items can be changed via {@link MenuItem#editor()} but never removed.
     *
     * @param  menu  the menu to be copied
     * @return       a new instance of ImmutableMenuImpl with the same contents as the original menu
     */
    public static ImmutableMenuImpl copyOf(@NotNull Menu menu) {
        return new ImmutableMenuImpl(menu);
    }

    /**
     * Create an immutable menu, meaning you can't remove, add or set.
     * <p>
     * Existing items can be changed via {@link MenuItem#editor()} but never removed.
     *
     * @param data the menu data; rows, title, modifiers.
     * @return the immutable menu
     */
    @NotNull
    @Contract("_ -> new")
    public static ImmutableMenuImpl of(MenuData data) {
        return new ImmutableMenuImpl(data);
    }

    static final class ImmutableMenuImpl implements IMenu {
        private final Menu menu;
        
        ImmutableMenuImpl(Menu menu) {
            this.menu = menu;
        }

        ImmutableMenuImpl(MenuData data) {
            this.menu = Menu.create(data);
        }

        ImmutableMenuImpl(MenuData data, MenuItem... items) {
            this.menu = Menu.create(data);
        }


        @NotNull
        @Override
        public MenuIterator iterator() {
            return menu.iterator();
        }

        @Override
        public MenuIterator iterator(IterationDirection direction) {
            return menu.iterator(direction);
        }

        @Override
        public MenuIterator iterator(int startingRow, int startingCol, IterationDirection direction) {
            return menu.iterator(startingRow, startingCol, direction);
        }

        @Override
        public Stream<MenuItem> stream() {
            return menu.stream();
        }

        @Override
        public Stream<MenuItem> parallelStream() {
            return menu.parallelStream();
        }

        @Override
        public void recreateInventory() {
            throw new UnsupportedOperationException("recreateInventory");
        }

        @Override
        public List<HumanEntity> getViewers() {
            return menu.getViewers();
        }

        @Override
        public void addItem(@NotNull ItemStack... items) {
            throw new UnsupportedOperationException("addItem");
        }

        @Override
        public void addItem(@NotNull MenuItem... items) {
            throw new UnsupportedOperationException("addItem");
        }

        @Override
        public void setItem(@NotNull Slot slot, ItemStack item) {
            throw new UnsupportedOperationException("setItem");
        }

        @Override
        public void setItem(@NotNull Slot slot, MenuItem item) {
            throw new UnsupportedOperationException("setItem");
        }

        @Override
        public void setItem(int slot, ItemStack item) {
            throw new UnsupportedOperationException("setItem");
        }

        @Override
        public void setItem(int slot, MenuItem item) {
            throw new UnsupportedOperationException("setItem");
        }

        @Override
        public @Nullable MenuItem getItem(int i) {
            return menu.getItem(i);
        }

        @Override
        public Optional<MenuItem> get(int i) {
            return menu.get(i);
        }

        @Override
        public @Nullable MenuItem getItem(@NotNull Slot slot) {
            return menu.getItem(slot);
        }

        @Override
        public Optional<MenuItem> get(@NotNull Slot slot) {
            return menu.get(slot);
        }

        @Override
        public boolean hasItem(@NotNull Slot slot) {
            return menu.hasItem(slot);
        }

        @Override
        public boolean hasItem(int slot) {
            return menu.hasItem(slot);
        }

        @Override
        public boolean hasItem(ItemStack item) {
            return menu.hasItem(item);
        }

        @Override
        public boolean hasItem(MenuItem item) {
            return menu.hasItem(item);
        }

        @Override
        public Optional<MenuItem> get(Predicate<MenuItem> itemDescription) {
            throw new UnsupportedOperationException("removeItems");
        }

        @Override
        public @Nullable MenuItem getItem(Predicate<MenuItem> itemDescription) {
            throw new UnsupportedOperationException("removeItems");
        }

        @Override
        public void removeItem(@NotNull ItemStack... itemStacks) {
            throw new UnsupportedOperationException("removeItems");
        }

        @Override
        public void removeItemStacks(@NotNull List<ItemStack> itemStacks) {
            throw new UnsupportedOperationException("removeItems");
        }

        @Override
        public void removeItem(@NotNull MenuItem... itemStacks) {
            throw new UnsupportedOperationException("removeItems");
        }

        @Override
        public void removeItem(@NotNull List<MenuItem> itemStacks) {
            throw new UnsupportedOperationException("removeItems");
        }

        @Override
        public void update() {
            throw new UnsupportedOperationException("update");
        }

        @Override
        public void updatePer(long repeatTime) {
            throw new UnsupportedOperationException("updatePer");
        }

        @Override
        public void updatePer(Duration repeatTime) {
            throw new UnsupportedOperationException("updatePer");
        }

        @Override
        public void updatePer(long delay, long repeatTime) {
            throw new UnsupportedOperationException("updatePer");

        }

        @Override
        public void updatePer(Duration delay, Duration repeatTime) {
            throw new UnsupportedOperationException("updatePer");
        }

        @Override
        public void updateTitle(String title) {
            menu.updateTitle(title);
        }

        @Override
        public void open(@NotNull HumanEntity entity) {
            menu.open(entity);
        }

        @Override
        public boolean addModifier(Modifier modifier) {
            return menu.addModifier(modifier);
        }

        @Override
        public boolean removeModifier(Modifier modifier) {
            return menu.removeModifier(modifier);
        }

        @Override
        public boolean addAllModifiers() {
            return menu.addAllModifiers();
        }

        @Override
        public void removeAllModifiers() {
            menu.removeAllModifiers();
        }

        @Override
        public boolean areItemsPlaceable() {
            return menu.areItemsPlaceable();
        }

        @Override
        public boolean areItemsRemovable() {
            return menu.areItemsRemovable();
        }

        @Override
        public boolean areItemsSwappable() {
            return menu.areItemsSwappable();
        }

        @Override
        public boolean areItemsCloneable() {
            return menu.areItemsCloneable();
        }

        @Override
        public void updateItem(int slot, @NotNull ItemStack itemStack) {
            throw new UnsupportedOperationException("updateItem");
        }

        @Override
        public void updateItem(@NotNull Slot slot, @NotNull ItemStack itemStack) {
            throw new UnsupportedOperationException("updateItem");
        }

        @NotNull
        public MenuItem[] getItems() {
            return menu.getItems();
        }

        @NotNull
        public @Unmodifiable List<MenuItem> getItemList() {
            return menu.getItemList();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("clear");
        }

        @NotNull
        @Override
        public Inventory getInventory() {
            return menu.getInventory();
        }
    }
}
