package me.flame.menus.menu.animation;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import me.flame.menus.menu.Menu;
import me.flame.menus.menu.animation.variants.NormalAnimation;
import me.flame.menus.menu.animation.variants.RepeatedAnimation;
import org.bukkit.plugin.Plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Animations in Woody are highly dependent on bukkit scheduler. shout out to bukkit
 * <p>
 * Example usage:
 * <pre>{@code
 *     Menu menu = ...;
 *     menu.addAnimation(Animation.builder(menu)
 *                                .frames(
 *                                      Frame.builder().addItems(ItemBuilder.of(Material.STONE).buildItem()).build(),
 *                                      Frame.builder().addItems(ItemBuilder.of(Material.WOODEN_SWORD).buildItem()).build(),
 *                                      Frame.builder().addItems(ItemBuilder.of(Material.CARROT).buildItem()).build(),
 *                                      ...
 *                                )
 *                                .type(Type.REPEATED)
 *                                .build());
 * }</pre>
 * @author FlameyosFlow
 * @since 1.5.0, 100% Stabilized at 2.0.0
 */
@SuppressWarnings("unused")
public abstract class Animation {
    protected int frameIndex;
    protected final int delay;
    protected final Menu menu;
    protected List<Frame> frames;
    protected AnimationScheduler scheduler;
    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(Animation.class);

    public Animation(int delay, Frame[] frames, Menu menu) {
        this.menu = menu;
        this.delay = delay;
        this.frameIndex = 0;
        this.frames = new ArrayList<>(List.of(frames));
    }


    public void reset() {
        frameIndex = 0;
    }

    @Nullable
    public Frame next() {
        if (frames.size() == frameIndex) return this.onFinish();
        Frame frame = frames.get(frameIndex);
        if (frame != null) {
            frameIndex++;
            frame.start();
        }
        return frame;
    }

    @CanIgnoreReturnValue
    @Contract(pure = true)
    public Frame start() {
        frameIndex = 0;
        this.scheduler = new AnimationScheduler(this, delay);
        return frames.get(frameIndex);
    }

    public void stop() {
        if (this.scheduler != null && !this.scheduler.isCancelled()) this.scheduler.cancel();
        Optional.of(frames.get(0)).ifPresent(Frame::reset);
    }

    @CanIgnoreReturnValue
    public abstract Frame onFinish();

    /*
     * Builders
     */

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Animation.Builder builder(Menu menu) {
        return new Animation.Builder(menu);
    }

    public enum Type {
        NORMAL, REPEATED
    }

    public static class Builder {
        private Frame[] frames;
        private int delay;
        private final Menu menu;
        private Type type;

        Builder(Menu menu) {
            this.menu = menu;
        }

        public Builder frames(Frame... frames) {
            this.frames = frames;
            return this;
        }

        public Builder delay(int delay) {
            this.delay = delay;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Animation build() {
            return type == Type.NORMAL ? new NormalAnimation(delay, frames, menu) : new RepeatedAnimation(delay, frames, menu);
        }
    }

    public static class AnimationScheduler extends BukkitRunnable {
        private final Animation scheduledAnimation;

        public AnimationScheduler(@NotNull Animation scheduledAnimation, int delay) {
            this.scheduledAnimation = scheduledAnimation;
            this.runTaskTimer(plugin, delay, delay);
        }

        @Override
        public void run() {
            Frame frame = scheduledAnimation.next();
            if (frame == null) this.cancel();
        }
    }
}