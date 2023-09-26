package me.flame.menus.menu.animation;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.BaseMenu;

import org.bukkit.Bukkit;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.primitives.Ints;

import java.util.Collection;
import java.util.List;
import java.util.Arrays;

/**
 * Animations in Woody are highly dependent on bukkit scheduler. shout out to bukkit
 */
@SuppressWarnings("unused")
public abstract class Animation {
    protected static Plugin plugin;

    public Animation(BaseMenu menu, List<MenuItem> items) {
        this.menu = menu;
        this.items = items;
    }

    public static void init(Plugin p) {
        plugin = p;
    }

    private int currentStep = 0;
    private BukkitTask task;
    private final BaseMenu menu;
    private final List<MenuItem> items;

    /**
     * The amount of ticks per step
     */
    protected long ticksPerStep = 1;

    /**
     * Which slots will be set on which ticks?
     */
    protected int[][] steps = new int[10][];

    /**
     * Size of "steps" variable
     */
    protected int size = 0;

    /**
     * Set how many ticks it will take to execute one step.
     * By default, 1.
     *
     * @param ticksPerStep The ticks required to perform one step
     */
    public void ticksPerStep(int ticksPerStep) {
        if (ticksPerStep < 1) {
            throw new IllegalArgumentException("Ticks cannot be under 1");
        }
        this.ticksPerStep = ticksPerStep;
    }

    /**
     * Add which slots will be set during which tick
     * If two of the same keys are registered they will be added together.
     *
     * @param tick  The tick. This must start from 0 and increment by one each time.
     * @param slots The slots which will be set during this time period
     */
    protected void add(int tick, int... slots) {
        if (tick >= steps.length) {
            steps = Arrays.copyOf(steps, tick + 3);
        }

        int[] tickStep = steps[tick];
        int slotsLength = slots.length;
        int tickStepLength = tickStep != null ? tickStep.length : 0;

        if (tickStepLength < slotsLength) {
            tickStep = tickStep == null
                    ? new int[slotsLength]
                    : Arrays.copyOf(tickStep, slotsLength);
            steps[tick] = tickStep;
        }

        for (int i = 0; i < slotsLength; i++) {
            tickStep[i] += slots[i];
        }
    }



    /**
     * Add which slots will be set during which tick.
     * If two of the same keys are registered they will be added together.
     *
     * @param tick  The tick. This must start from 0 and increment by one each time.
     * @param slotsList The slots which will be set during this time period
     */
    protected void add(int tick, Collection<Integer> slotsList) {
        this.add(tick, Ints.toArray(slotsList));
    }

    /**
     * Initializes this animation, settings all slots
     *
     * @param rows The amount of rows the eventual inventory will have.
     */
    public abstract void init(int rows);

    /**
     * Starts the animation
     */
    public void animate() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (currentStep == steps.length) {
                task.cancel();
                return;
            }

            int[] slots = steps[currentStep];
            if (slots == null || slots.length == 0) {
                task.cancel();
                return;
            }

            for (int slot : slots) {
                MenuItem item = items.get(slot);
                if (item != null) menu.setItem(slot, item);
            }

            currentStep++;
        }, 0, ticksPerStep);
    }

    public void stop() {
        task.cancel();
    }

    /**
     * Gets all slots from a beginning slot to delta amount extra.
     * The default direction is always right (east). Turn it left by entering a negative delta.
     *
     * @param begin The beginning slot (inclusive)
     * @param delta The difference between the two, where the last slot is excluded. A delta of 9 will result in 9 elements, but only go up to slot 8.
     * @return the slots between begin and delta amount away
     */
    protected static int[] getHorizontal(int begin, int delta) {
        int direction = delta < 0 ? -1 : 1;
        int newDelta = Math.abs(delta) + 1;

        int[] result = new int[newDelta];
        for (int i = 0; i <= newDelta; i++) {
            result[i] = begin + (i * direction);
        }
        return result;
    }

    /**
     * Gathers slots vertically down from the beginning slot.
     * The default direction is always down (south). Turn it left by entering a negative delta.
     *
     * @param begin The beginning slot
     * @param delta How many rows the vertical selection goes down
     * @return the vertical slots between the beginning slots
     */
    protected static int[] getVertical(int begin, int delta) {
        int direction = delta < 0 ? -9 : 9;
        int deltaAbs = Math.abs(delta);

        int[] result = new int[deltaAbs];
        for (int i = 0; i <= deltaAbs; i++)
            result[i] = begin + (i * direction);
        return result;
    }
}