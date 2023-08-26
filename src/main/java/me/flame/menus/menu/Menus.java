package me.flame.menus.menu;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import me.flame.menus.listeners.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class Menus {
    public static final Plugin PLUGIN = JavaPlugin.getProvidingPlugin(Menus.class);

    static {
        Bukkit.getPluginManager().registerEvents(new MenuListeners(), PLUGIN);
    }

    private static final MenuFactory menuFactory = new MenuFactory();

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
