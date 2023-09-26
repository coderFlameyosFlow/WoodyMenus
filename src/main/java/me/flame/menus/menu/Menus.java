package me.flame.menus.menu;

import me.flame.menus.components.nbt.ItemNbt;
import me.flame.menus.components.nbt.LegacyNbt;
import me.flame.menus.components.nbt.Pdc;
import me.flame.menus.menu.animation.Animation;
import me.flame.menus.util.VersionHelper;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import me.flame.menus.listeners.*;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class Menus {
    private static Plugin plugin;

    /**
     * Initializes the all the menus with the given Plugin instance.
     *
     * @param  p  the Plugin instance to be initialized
     */
    public static void init(Plugin p) {
        plugin = p;
        Animation.init(p);
        BaseMenu.init(p);
        ItemNbt.wrapper(VersionHelper.IS_PDC_VERSION ? new Pdc(p) : new LegacyNbt());
        Bukkit.getPluginManager().registerEvents(new MenuListeners(p), p);
    }

    /**
     * Returns the Plugin instance.
     *
     * @return  the Plugin instance
     */
    public static Plugin plugin() {
        assert plugin != null :
                "Menus#plugin() called before Menus#init(Plugin)" +
                "\nFix: Call Menus.init(Plugin) at JavaPlugin#onEnable()";
        return plugin;
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    public static SimpleBuilder menu() {
        return new SimpleBuilder();
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    public static PaginatedBuilder paginated() {
        return new PaginatedBuilder();
    }
}
