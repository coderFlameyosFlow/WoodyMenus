package me.flame.menus.menu;

import lombok.NonNull;

import me.flame.menus.modifiers.Modifier;

import org.bukkit.ChatColor;

import java.util.EnumSet;

@SuppressWarnings("unused")
public abstract class BaseBuilder<G, B extends BaseBuilder<G, B>> {
    protected String title;
    protected int rows;
    protected final EnumSet<Modifier> modifiers;

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

    protected void checkRows(int rows) {
        if (rows <= 0) throw new IllegalArgumentException("Rows must be greater than 0");
        if (rows > 6) throw new IllegalArgumentException("Rows must be equal to 6 or less");
    }
}
