package me.flame.menus.menu;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * GUI builder for creating a {@link PaginatedMenu}
 */
@SuppressWarnings("unused")
public class PaginatedBuilder extends BaseBuilder<PaginatedMenu, PaginatedBuilder> {

    private int pageSize = 0;

    /**
     * Sets the desirable page size, most of the time this isn't needed
     *
     * @param pageSize The amount of free slots that page items should occupy
     * @return The current builder
     */
    @NotNull
    @Contract("_ -> this")
    public PaginatedBuilder pageSize(final int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Creates a new {@link PaginatedMenu}
     *
     * @return A new {@link PaginatedMenu}
     */
    @NotNull
    @Override
    @Contract(" -> new")
    public PaginatedMenu create() {
        checkRows(rows);
        final PaginatedMenu menu = type == MenuType.CHEST
                ? new PaginatedMenu(rows, pageSize, title, modifiers)
                : new PaginatedMenu(type, pageSize, title, modifiers);
        if (menuConsumer != null) menuConsumer.accept(menu);
        return menu;
    }
}
