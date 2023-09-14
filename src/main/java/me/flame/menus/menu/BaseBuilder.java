package me.flame.menus.menu;

import lombok.NonNull;

import me.flame.menus.modifiers.Modifier;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class BaseBuilder<G, B extends BaseBuilder<G, B>> {
    @NotNull
    protected String title = "";

    @NotNull
    protected MenuType type = MenuType.CHEST;

    @NotNull
    protected final EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);

    @NotNull
    protected Consumer<G> menuConsumer = (event -> {});

    protected int rows = 1;

    /**
     * Sets the title of the menu.
     *
     * @param  title  the title to be set
     * @return        the builder for chaining
     */
    public B title(@NonNull String title) {
        this.title = title;
        return (B) this;
    }

    /**
     * Sets the amount of rows of the menu.
     *
     * @param  rows  the amount of rows to be set
     * @return        the builder for chaining
     */
    public B rows(int rows) {
        checkRows(rows);
        this.rows = rows;
        return (B) this;
    }

    /**
     * Sets the type of the menu.
     * @param type the type, ex. {@link MenuType#HOPPER}, {@link MenuType#FURNACE}, etc.
     * @apiNote By default, it is {@link MenuType#CHEST}.
     * @return the builder for chaining
     */
    public B type(MenuType type) {
        this.type = type;
        return (B) this;
    }

    /**
     * Adds a modifier to the list of modifiers.
     *
     * @param  modifier  the modifier to be added
     * @return           the builder for chaining
     */
    public B addModifier(@NonNull Modifier modifier) {
        modifiers.add(modifier);
        return (B) this;
    }

    /**
     * Remove a modifier from the list of modifiers.
     *
     * @param  modifier  the modifier to be removed
     * @return           the builder for chaining
     */
    public B removeModifier(@NonNull Modifier modifier) {
        modifiers.remove(modifier);
        return (B) this;
    }

    /**
     * Add all the modifiers of {@link Modifier} to the list of modifiers.
     * @return the builder for chaining
     */
    public B addAllModifiers() {
        modifiers.addAll(Modifier.ALL);
        return (B) this;
    }

    public @NotNull Consumer<G> getMenuConsumer() {
        return menuConsumer;
    }

    /**
     * Sets the menu consumer for this function.
     * @apiNote This consumer will be called when the menu is created via {@link #create()}.
     * @param  menuConsumer  the consumer to set
     */
    public void setMenuConsumer(@NonNull Consumer<G> menuConsumer) {
        this.menuConsumer = menuConsumer;
    }

    /**
     * Creates and returns an instance of G.
     * <p>
     * It can be a Menu, a PaginatedMenu, etc. (depending on the builder)
     *
     * @return  an instance of G
     */
    public abstract G create();

    protected static void checkRows(int rows) {
        if (rows <= 0 || rows > 6) throw new IllegalArgumentException(
            "Rows must be more than 1 or 6 and less" +
            "\nRows: " + rows +
            "\nFix: Rows must be 1-6"
        );
    }
}
