package me.flame.menus.adventure;

import net.kyori.adventure.text.Component;

import org.apache.commons.lang.Validate;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings({ "deprecation", "ForLoopReplaceableByForEach" })
public class Lore implements Iterable<TextHolder> {
    private final ItemMeta meta;
    private final List<TextHolder> lore;

    private static final Lore EMPTY = new Lore((ItemMeta) null);
    private static final List<TextHolder> EMPTY_LORE = new ArrayList<>(0);

    public Lore(ItemMeta meta) {
        this.meta = meta;
        this.lore = (CompHolder.isNativeAdventureSupport()) ? lore(meta) : getLore(meta);
    }
    @Contract(pure = true)
    public Lore(@NotNull Lore lore) {
        this.meta = lore.meta;
        this.lore = lore.lore;
    }

    private static @NotNull List<TextHolder> lore(ItemMeta meta) {
        if (meta == null || !meta.hasLore()) return EMPTY_LORE;
        List<Component> components = meta.lore();
        Validate.notNull(components);

        int size = components.size();
        List<TextHolder> lore = new ArrayList<>(size);
        for (int componentIndex = 0; componentIndex < size; componentIndex++) {
            lore.add(CompHolder.of(components.get(componentIndex)));
        }
        return lore;
    }

    private static @NotNull List<TextHolder> getLore(ItemMeta meta) {
        if (meta == null || !meta.hasLore()) return EMPTY_LORE;
        List<String> components = meta.getLore();
        Validate.notNull(components);

        int size = components.size();
        List<TextHolder> lore = new ArrayList<>(size);
        for (int componentIndex = 0; componentIndex < size; componentIndex++) {
            lore.add(CompHolder.of(components.get(componentIndex)));
        }
        return lore;
    }

    public static Lore empty() {
        return EMPTY;
    }

    public int size() {
        return lore.size();
    }

    public TextHolder get(int stringIndex) {
        return lore.get(stringIndex);
    }

    @NotNull
    @Override
    public Iterator<TextHolder> iterator() {
        return lore.listIterator();
    }

    @Override
    public void forEach(Consumer<? super TextHolder> action) {
        lore.forEach(action);
    }

    public void set(int stringIndex, TextHolder of) {
        lore.set(stringIndex, of);
    }

    public void toItemLore(ItemStack itemStack) {
        for (TextHolder textHolder : lore) textHolder.asItemLoreAtEnd(meta);
        itemStack.setItemMeta(meta);
    }
}
