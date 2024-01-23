package me.flame.menus.items.states;

import com.google.common.base.Preconditions;

import me.flame.menus.adventure.Lore;
import me.flame.menus.adventure.TextHolder;
import me.flame.menus.items.MenuItem;

import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * States of automatically changing values of lore in {@link MenuItem}s.
 * <p>
 * Best usage: <pre>{@code
 *     MenuItem item = ...
 *
 *     // it doesn't have to be %%, it just needs to start with a symbol to avoid developer conflicts
 *     State state = State.of("%player-size%", () -> Bukkit.getOnlinePlayers().size(), item);
 *     item.addState(state);
 * }</pre>
 * @author FlameyosFlow
 * @since 2.0.0
 */
@ApiStatus.Experimental
public class State {
    private final TextHolder key;
    private final MenuItem item;
    private final Supplier<TextHolder> value;

    private State(TextHolder key, Supplier<TextHolder> value, @NotNull MenuItem item) {
        this.item = item;

        ItemStack stack = item.getItemStack();
        if (!stack.hasItemMeta()) {
            throw new IllegalArgumentException(
                "Item must have item meta to set state. \nMaterial Name: " + stack.getType().name() +
                "\nFix: Some Materials can't have an ItemMeta, like Material.AIR"
            );
        }

        this.key = key;
        this.value = value;
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull State of(String key, String value, MenuItem item) {
        return new State(TextHolder.of(key), () -> TextHolder.of(value), item);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull State of(String key, IntSupplier value, MenuItem item) {
        return new State(TextHolder.of(key), () -> TextHolder.of(String.valueOf(value.getAsInt())), item);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull State of(String key, DoubleSupplier value, MenuItem item) {
        return new State(TextHolder.of(key), () -> TextHolder.of(String.valueOf(value.getAsDouble())), item);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull State of(String key, LongSupplier value, MenuItem item) {
        return new State(TextHolder.of(key), () -> TextHolder.of(String.valueOf(value.getAsLong())), item);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull State of(TextHolder key, String value, MenuItem item) {
        return new State(key, () -> TextHolder.of(value), item);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull State of(TextHolder key, IntSupplier value, MenuItem item) {
        return new State(key, () -> TextHolder.of(String.valueOf(value.getAsInt())), item);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull State of(TextHolder key, DoubleSupplier value, MenuItem item) {
        return new State(key, () -> TextHolder.of(String.valueOf(value.getAsDouble())), item);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull State of(TextHolder key, LongSupplier value, MenuItem item) {
        return new State(key, () -> TextHolder.of(String.valueOf(value.getAsLong())), item);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull State of(TextHolder key, TextHolder value, MenuItem item) {
        return new State(key, () -> value, item);
    }

    @Contract(pure = true)
    public void update() {
        ItemStack otherItem = item.getItemStack();
        ItemMeta meta = Preconditions.checkNotNull(otherItem.getItemMeta());
        Lore lore = new Lore(meta);

        int size = lore.size();
        for (int stringIndex = 0; stringIndex < size; stringIndex++) replaceWithKey(stringIndex, lore, key, value);
        lore.toItemLore(item.getItemStack());
    }

    private static void replaceWithKey(int stringIndex, @NotNull Lore list, TextHolder key, Supplier<TextHolder> value) {
        TextHolder currentString = list.get(stringIndex);
        if (!currentString.contains(key)) return;
        list.set(stringIndex, value.get());
    }
}
