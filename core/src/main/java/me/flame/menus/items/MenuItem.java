package me.flame.menus.items;

import lombok.Getter;
import lombok.Setter;

import me.flame.menus.components.nbt.ItemNbt;
import me.flame.menus.events.ClickActionEvent;
import me.flame.menus.items.states.State;
import me.flame.menus.menu.ActionResponse;
import me.flame.menus.util.ItemResponse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * A Gui itemStack which was particularly made to have an action.
 * <p>
 * Good example of using "MenuItem":
 * <pre>{@code
 *      var menuItem = ...;
 *      menuItem.setClickAction(event -> {
 *          ...
 *      });
 *
 *      // implementing a new itemStack:
 *      menu.addItem(ItemBuilder.of(itemStack, 2) // 2 is the amount of items you get from this "ItemBuilder"
 *                                  .setName(...).setLore(...)
 *                                  .buildItem(() -> ...); // ItemBuilder#build will give you a normal ItemStack
 *      // the lambda (Consumer) at ItemBuilder#buildItem(Consumer) is optional and you do not have to provide an action, you can use ItemBuilder#buildItem()
 *
 *      // editing the item stack
 *      menuItem.editor() // use methods, such as
 *              .setName("Pumpkin")
 *              .setLore("This is a random item named a Pumpkin")
 *              .done(); // no need to set item again in the menu but you can.
 * }</pre>
 */
@SuppressWarnings("unused")
@SerializableAs("woody-menu")
public final class MenuItem implements Nameable, Cloneable, Serializable, Comparable<MenuItem>, ConfigurationSerializable {
    @NotNull
    CompletableFuture<ItemResponse> clickAction;

    @Getter @Setter
    boolean async = false;

    @NotNull
    ItemStack itemStack;

    @NotNull
    private final UUID uuid;

    private List<State> states;
    private Map<UUID, Long> usageCooldown;

    private MenuItem(ItemStack itemStack, @Nullable ItemResponse action) {
        Objects.requireNonNull(itemStack);
        this.uuid =  UUID.randomUUID();
        this.itemStack = ItemNbt.setString(itemStack, "woody-menu", uuid.toString());

        this.clickAction = CompletableFuture.completedFuture(action == null ? (slot, event) -> ActionResponse.EMPTY : action);
    }

    private MenuItem(ItemStack itemStack, @Nullable ItemResponse action, UUID uuid) {
        Objects.requireNonNull(itemStack);
        this.uuid =  UUID.randomUUID();
        this.itemStack = ItemNbt.setString(itemStack, "woody-menu", uuid.toString());

        this.clickAction = CompletableFuture.completedFuture(action == null ? (slot, event) -> ActionResponse.DONE : action);
    }

    public static @NotNull MenuItem of(ItemStack itemStack, @Nullable ItemResponse action) {
        return new MenuItem(itemStack, action);
    }

    public static @NotNull MenuItem of(ItemStack itemStack) {
        return new MenuItem(itemStack, null);
    }

    @NotNull
    public CompletableFuture<ItemResponse> getClickAction() {
        return clickAction;
    }

    @NotNull
    public ItemResponse getClickActionNow(ItemResponse ifAbsent) {
        return clickAction.getNow(ifAbsent);
    }

    public void setClickAction(@NotNull ItemResponse clickAction) {
        this.clickAction = CompletableFuture.completedFuture(clickAction);
    }

    @Contract(" -> new")
    public @NotNull ItemEditor editor() {
        return new ItemEditor(this);
    }

    @Contract(" -> new")
    public @NotNull SkullItemEditor skullEditor() {
        return new SkullItemEditor(this);
    }

    public @NotNull ItemStack getItemStack() {
        return itemStack;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = ItemNbt.setString(itemStack, "woody-menu", uuid.toString());
    }

    public @NotNull Material getType() {
        return itemStack.getType();
    }

    @Override
    public boolean equals(Object item) {
        if (item == this) return true;
        if (!(item instanceof MenuItem)) return false;
        return uuid.equals(((MenuItem) item).uuid);
    }

    @Override
    public @NotNull MenuItem clone() {
        try {
            return (MenuItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<ActionResponse> click(final int slot, final ClickActionEvent event) {
        return async
                ? clickAction.thenApplyAsync(ca -> ca.apply(slot, event))
                : clickAction.thenApply(ca -> ca.apply(slot, event));
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        final Map<String, Object> result = new LinkedHashMap<>(4);
        result.put("type", getType().name());
        result.put("uuid", uuid);

        final ItemMeta meta = itemStack.getItemMeta();
        final int amount = itemStack.getAmount();
        if (amount != 1) result.put("amount", amount);
        if (meta != null) result.put("meta", meta);

        return result;
    }

    @NotNull
    public static MenuItem deserialize(@NotNull Map<String, Object> serialized) {
        final String type = (String) serialized.get("type");
        final int amount = (int) serialized.getOrDefault("amount", 1);
        final ItemMeta meta = (ItemMeta) serialized.get("meta");
        final UUID uuid = (UUID) serialized.get("uuid");

        final ItemStack result = new ItemStack(Material.valueOf(type), amount);
        if (meta != null) result.setItemMeta(meta);

        return new MenuItem(result, null, uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode(); // they provide a fast hashcode
    }

    @Override
    public int compareTo(@NotNull MenuItem menuItem) {
        return uuid.compareTo(menuItem.uuid);
    }

    private List<State> getStates() {
        if (states == null)
            states = new ArrayList<>(5);
        return states;
    }

    private Map<UUID, Long> getUsageCooldown() {
        if (usageCooldown == null)
            usageCooldown = new HashMap<>(10);
        return usageCooldown;
    }

    @Nullable
    @Override
    public String getCustomName() {
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta == null ? null : itemMeta.getDisplayName();
    }

    @Override
    public void setCustomName(@Nullable String s) {
        editor().setName(spigotify(itemStack, s)).done();
    }

    private static @NotNull String spigotify(ItemStack itemStack, String s) {
        if (s == null || s.isEmpty()) {
            String name = itemStack.getType().name();

            boolean capitalizeNext = false;
            StringBuilder builder = new StringBuilder(name.length());
            for (char character : name.toCharArray()) {
                if (character == '_') {
                    builder.append(' ');
                    capitalizeNext = true;
                }
                builder.append(capitalizeNext ? character : Character.toLowerCase(character));
            }
            return builder.toString();
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public void updateStates() {
        getStates().forEach(State::update);
    }

    public boolean hasStates() {
        return states != null && !states.isEmpty();
    }

    public boolean hasCooldowns() {
        return usageCooldown != null && !usageCooldown.isEmpty();
    }

    public void addState(State state) {
        getStates().add(state);
    }

    public void removeState(State state) {
        if (!hasStates()) return;
        getStates().remove(state);
    }

    public void removeState(int state) {
        if (!hasStates()) return;
        getStates().remove(state);
    }

    public boolean isOnCooldown(Player player) {
        if (!hasCooldowns()) return false;
        Long cooldown = getUsageCooldown().get(player.getUniqueId());
        return cooldown != null && cooldown < System.currentTimeMillis();
    }

    public void addCooldown(@NotNull Player player, long millis) {
        getUsageCooldown().put(player.getUniqueId(), System.currentTimeMillis() + millis);
    }
}
