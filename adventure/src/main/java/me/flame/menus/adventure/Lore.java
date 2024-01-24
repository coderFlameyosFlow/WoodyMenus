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
import java.util.NoSuchElementException;

@SuppressWarnings({ "deprecation", "ForLoopReplaceableByForEach" })
public class Lore implements Iterable<TextHolder> {
    private final ItemMeta meta;
    private TextHolder[] lore;

    private static final Lore EMPTY = new Lore((ItemMeta) null);
    private static final TextHolder[] EMPTY_LORE = new TextHolder[0];

    public Lore(ItemMeta meta) {
        this.meta = meta;
        this.lore = (CompHolder.isNativeAdventureSupport()) ? lore(meta) : getLore(meta);
    }
    @Contract(pure = true)
    public Lore(@NotNull Lore lore) {
        this.meta = lore.meta;
        this.lore = lore.lore;
    }

    private static @NotNull TextHolder[] lore(ItemMeta meta) {
        if (meta == null || !meta.hasLore()) return EMPTY_LORE;
        List<Component> components = meta.lore();
        Validate.notNull(components);

        int size = components.size();
        List<TextHolder> lore = new ArrayList<>(size);
        for (int componentIndex = 0; componentIndex < size; componentIndex++) {
            lore.add(CompHolder.of(components.get(componentIndex)));
        }
        return lore.toArray(TextHolder[]::new);
    }

    private static @NotNull TextHolder[] getLore(ItemMeta meta) {
        if (meta == null || !meta.hasLore()) return EMPTY_LORE;
        List<String> components = meta.getLore();
        Validate.notNull(components);

        int size = components.size();
        List<TextHolder> lore = new ArrayList<>(size);
        for (int componentIndex = 0; componentIndex < size; componentIndex++) {
            lore.add(CompHolder.of(components.get(componentIndex)));
        }
        return lore.toArray(TextHolder[]::new);
    }

    public static Lore empty() {
        return EMPTY;
    }

    public int size() {
        return lore.length;
    }

    public TextHolder get(int stringIndex) {
        if (stringIndex >= lore.length) return null;
        return lore[stringIndex];
    }

    @NotNull
    @Override
    public Iterator<TextHolder> iterator() {
        return new TextHolderIterator(this);
    }

    public void set(int stringIndex, TextHolder of) {
        if (stringIndex >= lore.length) return;
        lore[stringIndex] = of;
    }

    public void toItemLore(ItemStack itemStack, boolean setMeta) {
        for (TextHolder textHolder : lore) textHolder.asItemLoreAtEnd(meta);
        if (setMeta) itemStack.setItemMeta(meta);
    }

    public void copyFrom(TextHolder[] newLore) {
        this.lore = newLore;
    }

    public static class TextHolderIterator implements Iterator<TextHolder> {
        private final Lore lore;
        private int index;
        private final int length;

        @Contract(pure = true)
        public TextHolderIterator(@NotNull Lore lore) {
            this.lore = lore;
            this.length = lore.lore.length;
        }


        @Override
        public boolean hasNext() {
            return index < length;
        }

        @Override
        public TextHolder next() {
            if (index >= length) throw new NoSuchElementException();
            TextHolder holder = lore.get(index);
            index++;
            return holder;
        }
    }
}
