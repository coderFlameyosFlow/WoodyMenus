package me.flame.menus.menu;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import me.flame.menus.listeners.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class Menus {
    private static final MenuFactory menuFactory = new MenuFactory();

    /**
     * Register the events required for Click events AND Modifier Checking
     * <p>
     * This includes: Checking if Placing/Removing/Swapping/Cloning items is allowed
     * <p>
     * or any click/drag/open/close event that happens, it's pretty important unless you have some sort of dummy inventory.
     */
    public static void installMenus(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new MenuListeners(), plugin);
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull SimpleBuilder menu() {
        return new SimpleBuilder();
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull PaginatedBuilder paginated() {
        return new PaginatedBuilder();
    }

    /**
     * Get the Menu Factory to use instead of making Menus using Builders.
     * <p>
     * This is a much less verbose way to create menus, and much less "Java-eey"
     * @return a less verbose way of creating menus; The MenuFactory
     */
    public static MenuFactory getFactory() {
        return menuFactory;
    }
}
