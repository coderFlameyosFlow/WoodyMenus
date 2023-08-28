package me.flame.menus.menu;

import lombok.NonNull;

import me.flame.menus.modifiers.Modifier;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.function.Consumer;

//changed

@SuppressWarnings("unused")
public abstract class BaseBuilder<G, B extends BaseBuilder<G, B>> {
    protected String title;
    protected int rows;
    protected MenuType type = MenuType.CHEST;
    protected final EnumSet<Modifier> modifiers;
    protected Consumer<G> menuConsumer;

    BaseBuilder() {
        this.title = "";
        this.rows = 1;
        this.modifiers = EnumSet.noneOf(Modifier.class);
    }

    public B title(@NonNull String title) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        return (B) this;
    }

    public B rows(int rows) {
        checkRows(rows);
        this.rows = rows;
        return (B) this;
    }

    public B type(MenuType type) {
        this.type = type;
        return (B) this;
    }

    public B addModifier(@NonNull Modifier modifier) {
        modifiers.add(modifier);
        return (B) this;
    }

    public B removeModifier(@NonNull Modifier modifier) {
        modifiers.remove(modifier);
        return (B) this;
    }

    public B addAllModifiers() {
        modifiers.addAll(Modifier.ALL);
        return (B) this;
    }

    public @NotNull Consumer<G> getMenuConsumer() {
        return menuConsumer;
    }

    public void setMenuConsumer(@NonNull Consumer<G> menuConsumer) {
        this.menuConsumer = menuConsumer;
    }

    public abstract G create();

    protected void checkRows(int rows) {
        if (rows <= 0) throw new IllegalArgumentException("Rows must be greater than 0");
        if (rows > 6) throw new IllegalArgumentException("Rows must be equal to 6 or less");
    }
}
