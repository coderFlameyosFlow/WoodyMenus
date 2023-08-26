package me.flame.menus.menu;

import lombok.NonNull;

import me.flame.menus.modifiers.Modifier;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

@SuppressWarnings("unused")
public final class MenuFactory {
    /**
     * Create a new regular Menu with the provided title and rows.
     * <p>
     * This also creates a simple hash set of modifiers which by default is empty.
     * <p>
     * A good example of creation using a method like this:
     * <pre>{@code
     * var plugin = ...;
     * Menu menu = Menus.getFactory().createMenu("Title", 3);
     * }</pre>
     * @param title the title to name the menu
     * @param rows the rows to add to the menu
     * @return a new Menu with the provided rows and title.
     */
    @Contract("_, _ -> new")
    public @NotNull Menu createMenu(@NonNull String title, int rows) {
        checkRows(rows);
        return new Menu(rows, title, EnumSet.noneOf(Modifier.class));
    }

    /**
     * Create a new regular Menu with the provided title and rows.
     * <p>
     * This also creates a simple hash set of modifiers which by default is empty.
     * <p>
     * A good example of creation using a method like this:
     * <pre>{@code
     * var plugin = ...;
     * Menu menu = Menus.getFactory().createMenu("Title", 3, true);
     * }</pre>
     * @param title the title to name the menu
     * @param rows the rows to add to the menu
     * @return a new Menu with the provided rows and title.
     */
    @Contract("_, _, _ -> new")
    public @NotNull Menu createMenu(@NonNull String title, int rows, boolean colorize) {
        checkRows(rows);
        return new Menu(rows, title, EnumSet.noneOf(Modifier.class), colorize);
    }

    /**
     * Create a new regular Menu with the provided title and rows with the option to colorize it
     * <p>
     * This also creates a simple hash set of modifiers which by default is empty.
     * <p>
     * A good example of creation using a method like this:
     * <pre>{@code
     * var plugin = ...;
     * Menu menu = Menus.getFactory().createMenu("Title", 3, true, EnumSet.of(Modifier.PREVENT_ITEM_REMOVAL));
     * }</pre>
     * @param title the title to name the menu
     * @param rows the rows to add to the menu
     * @param modifiers a hashset of pre-defined modifiers
     * @param colorize colorize the title or not.
     * @return a new Menu with the provided rows and title.
     */
    @Contract("_, _, _, _ -> new;")
    public @NotNull Menu createMenu(@NonNull String title, int rows, boolean colorize, EnumSet<Modifier> modifiers) {
        checkRows(rows);
        return new Menu(rows, title, modifiers, colorize);
    }

    /**
     * Create a new regular Menu with the provided title and rows with the option to colorize it
     * <p>
     * This also creates a simple hash set of modifiers which by default is empty.
     * <p>
     * A good example of creation using a method like this:
     * <pre>{@code
     * var plugin = ...;
     * Menu menu = Menus.getFactory().createMenu("Title", 3, EnumSet.of(Modifier.PREVENT_ITEM_REMOVAL));
     * }</pre>
     * @param title the title to name the menu
     * @param rows the rows to add to the menu
     * @param modifiers a hashset of pre-defined modifiers
     * @return a new Menu with the provided rows and title.
     */
    @Contract("_, _, _ -> new;")
    public @NotNull Menu createMenu(@NonNull String title, int rows, EnumSet<Modifier> modifiers) {
        checkRows(rows);
        return new Menu(rows, title, modifiers, true);
    }

    private void checkRows(int rows) {
        if (rows <= 0) throw new IllegalArgumentException("Rows must be greater than 0");
        else if (rows > 6) throw new IllegalArgumentException("Rows must be equal to 6 or less");
    }
}
