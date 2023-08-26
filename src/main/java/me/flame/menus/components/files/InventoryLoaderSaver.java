package me.flame.menus.components.files;

/*import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;*/

// NOTE
// Future release, future feature.

@SuppressWarnings("unused")

public final class InventoryLoaderSaver {
    /*private static final Pattern LINE_PATTERN = Pattern.compile(":", Pattern.UNIX_LINES | Pattern.CASE_INSENSITIVE | Pattern.COMMENTS);

    public static void saveInventoryToFile(Inventory inventory, String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.notExists(path)) {
                Files.createFile(path);
                Files.createDirectories(path.getParent());
            }

            List<String> lines = new ArrayList<>();
            for (ItemStack item : inventory.getContents()) {
                if (item != null) {
                    StringBuilder sb = new StringBuilder(1024);
                    sb.append(item.getType().name()).append(":").append(item.getAmount());

                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        String serializedMeta = serializeItemMeta(meta);
                        sb.append(":").append(serializedMeta);
                    }
                    lines.add(sb.toString());
                }
            }

            Files.write(path, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Inventory loadInventoryFromFile(Inventory toLoadTo, String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                Files.createFile(path);
                return null;
            }

            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] parts = LINE_PATTERN.split(line, 3);
                if (parts.length >= 2) {
                    Material material = Material.getMaterial(parts[0]);
                    int amount = Integer.parseInt(parts[1]);
                    ItemStack item = new ItemStack(material, amount);

                    if (parts.length >= 3) {
                        ItemMeta meta = deserializeItemMeta(parts[2]);
                        item.setItemMeta(meta);
                    }
                    toLoadTo.addItem(item);
                }
            }

            return toLoadTo;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }



    private static String serializeItemMeta(ItemMeta meta) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
        dataOutput.writeObject(meta);
        dataOutput.close();
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    private static ItemMeta deserializeItemMeta(String serializedMeta) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(serializedMeta);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
        ItemMeta meta = (ItemMeta) dataInput.readObject();
        dataInput.close();
        return meta;
    }*/
}
