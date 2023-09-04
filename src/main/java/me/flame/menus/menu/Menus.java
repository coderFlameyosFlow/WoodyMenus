package me.flame.menus.menu;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import me.flame.menus.listeners.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class Menus {
    private static final MenuFactory menuFactory = new MenuFactory();

    public static void init(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new MenuListeners(plugin), plugin);
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
     * @since 1.0.0
     * @deprecated Use menu constructors like {@link Menu} or use builders like {@link Menus#menu()}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.4.0")
    public static MenuFactory getFactory() {
        return menuFactory;
    }
}
