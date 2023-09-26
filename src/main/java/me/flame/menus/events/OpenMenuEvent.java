package me.flame.menus.events;

import me.flame.menus.menu.animation.Animation;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryView;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("unused")
public class OpenMenuEvent extends InventoryOpenEvent {
    private Animation animation;

    public OpenMenuEvent(InventoryView view) {
        super(view);
    }

    @Nullable
    public Animation getAnimation() {
        return animation;
    }

    public Optional<Animation> getOptionalAnimation() {
        return Optional.ofNullable(animation);
    }

    @Nullable
    public <T extends Animation> T getAnimation(Class<T> clazz) {
        return clazz.cast(animation);
    }

    public <T extends Animation> Optional<T> getOptionalAnimation(Class<T> clazz) {
        return Optional.ofNullable(clazz.cast(animation));
    }

    public void setAnimation(@Nullable Animation animation) {
        this.animation = animation;
    }

    public Player getOpener() {
        return (Player) transaction.getPlayer();
    }
}
