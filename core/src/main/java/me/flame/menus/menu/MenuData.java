package me.flame.menus.menu;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import me.flame.menus.adventure.TextHolder;
import me.flame.menus.items.MenuItem;
import me.flame.menus.modifiers.Modifier;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable data class containing of Data to simplify the internal
 * <p>
 * code (parameters)
 */
@Getter
@EqualsAndHashCode
@SuppressWarnings({ "unused", "unchecked" })
public class MenuData implements Serializable, ConfigurationSerializable {
    private int rows, pages;
    private TextHolder title;
    private MenuType type;
    private MenuItem[] items;
    private EnumSet<Modifier> modifiers;

    public MenuData(String title, int rows, int pages, EnumSet<Modifier> modifiers, MenuItem[] items) {
        this.title = TextHolder.of(title);
        this.rows = rows;
        this.pages = pages;
        this.type = MenuType.CHEST;
        this.modifiers = modifiers;
    }

    public MenuData(String title, MenuType type, int pages, EnumSet<Modifier> modifiers, MenuItem[] items) {
        this.title = TextHolder.of(title);
        this.rows = 1;
        this.pages = pages;
        this.type = type;
        this.modifiers = modifiers;
    }

    public MenuData(TextHolder title, int rows, int pages, EnumSet<Modifier> modifiers, MenuItem[] items) {
        this.title = title;
        this.rows = rows;
        this.pages = pages;
        this.type = MenuType.CHEST;
        this.modifiers = modifiers;
    }

    public MenuData(TextHolder title, MenuType type, int pages, EnumSet<Modifier> modifiers, MenuItem[] items) {
        this.title = title;
        this.rows = 1;
        this.pages = pages;
        this.type = type;
        this.modifiers = modifiers;
    }

    public MenuData(String title, int rows, EnumSet<Modifier> modifiers, MenuItem[] items) {
        this(title, rows, 1, modifiers, items);
    }

    public MenuData(String title, MenuType type, EnumSet<Modifier> modifiers, MenuItem[] items) {
        this(title, type, 1, modifiers, items);
    }

    public MenuData(TextHolder title, int rows, EnumSet<Modifier> modifiers, MenuItem[] items) {
        this(title, rows, 1, modifiers, items);
    }

    public MenuData(TextHolder title, MenuType type, EnumSet<Modifier> modifiers, MenuItem[] items) {
        this(title, type, 1, modifiers, items);
    }

    private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.readFields();
        this.title = (TextHolder) s.readObject();
        this.rows = s.readInt();
        this.pages = s.readInt();
        this.type = (MenuType) s.readObject();
        this.items = (MenuItem[]) s.readObject();
        this.modifiers = (EnumSet<Modifier>) s.readObject();
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(title);
        s.writeInt(rows);
        s.writeInt(pages);

        s.writeObject(type);
        s.writeObject(items);
        s.writeObject(modifiers);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>(6);
        map.put("title", title.toString());

        map.put("rows", rows);
        map.put("pages", pages);

        map.put("type", type.name());

        map.put("items", items);
        map.put("modifiers", modifiers);
        return map;
    }

    /**
     * Deserialize the serialized data and create a MenuData object.
     *
     * @param  serializedData  the serialized data to be deserialized
     * @return                 the deserialized MenuData object
     */
    public static MenuData deserialize(Map<String, Object> serializedData) {
        MenuData data;
        MenuType type = MenuType.valueOf((String) serializedData.get("type"));
        if (type == MenuType.CHEST) {
            data = new MenuData(
                (String) serializedData.get("title"),
                (int) serializedData.get("rows"),
                (int) serializedData.get("pages"),
                (EnumSet<Modifier>) serializedData.get("modifiers"),
                (MenuItem[]) serializedData.get("items")
            );
        } else {
            data = new MenuData(
                    (String) serializedData.get("title"),
                    type,
                    (int) serializedData.get("pages"),
                    (EnumSet<Modifier>) serializedData.get("modifiers"),
                    (MenuItem[]) serializedData.get("items")
            );
        }
        return data;
    }

    @NotNull
    Menu intoMenu() {
        Menu menu = type != MenuType.CHEST ? new Menu(type, title, modifiers, true) : new Menu(rows, title, modifiers, true);
        menu.setContents(items);
        return menu;
    }

    @NotNull
    static MenuData intoData(Menu menu) {
        return menu.type == MenuType.CHEST
                ? new MenuData(menu.title, menu.rows, menu.modifiers, menu.data.getItems())
                : new MenuData(menu.title, menu.type, menu.modifiers, menu.data.getItems());
    }
}
