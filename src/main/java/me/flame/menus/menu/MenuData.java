package me.flame.menus.menu;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import me.flame.menus.items.MenuItem;
import me.flame.menus.modifiers.Modifier;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
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
    private String title;
    private int rows;
    private int pages;
    private MenuType type;
    private List<MenuItem> items;
    private EnumSet<Modifier> modifiers;

    public MenuData(String title, int rows, int pages, EnumSet<Modifier> modifiers, List<MenuItem> items) {
        this.title = title;
        this.rows = rows;
        this.pages = pages;
        this.type = MenuType.CHEST;
        this.modifiers = modifiers;
    }

    public MenuData(String title, MenuType type, int pages, EnumSet<Modifier> modifiers, List<MenuItem> items) {
        this.title = title;
        this.rows = 1;
        this.pages = pages;
        this.type = type;
        this.modifiers = modifiers;
    }

    public MenuData(String title, int rows, EnumSet<Modifier> modifiers, List<MenuItem> items) {
        this(title, rows, 1, modifiers, items);
    }

    public MenuData(String title, MenuType type, EnumSet<Modifier> modifiers, List<MenuItem> items) {
        this(title, type, 1, modifiers, items);
    }

    private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.readFields();
        this.title = (String) s.readObject();
        this.rows = s.readInt();
        this.pages = s.readInt();
        this.type = (MenuType) s.readObject();
        this.items = (List<MenuItem>) s.readObject();
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
        Map<String, Object> map = new LinkedHashMap<>(4);
        map.put("title", title);

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
        if (serializedData.get("type").equals(MenuType.CHEST.name())) {
            data = new MenuData(
                (String) serializedData.get("title"),
                (int) serializedData.get("rows"),
                (int) serializedData.get("pages"),
                (EnumSet<Modifier>) serializedData.get("modifiers"),
                (List<MenuItem>) serializedData.get("items")
            );
        } else {
            data = new MenuData(
                    (String) serializedData.get("title"),
                    MenuType.valueOf((String) serializedData.get("type")),
                    (int) serializedData.get("pages"),
                    (EnumSet<Modifier>) serializedData.get("modifiers"),
                    (List<MenuItem>) serializedData.get("items")
            );
        }
        return data;
    }
}
