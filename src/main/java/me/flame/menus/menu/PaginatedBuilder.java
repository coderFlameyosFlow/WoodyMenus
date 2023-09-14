package me.flame.menus.menu;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Menu builder for creating a {@link PaginatedMenu}
 */
@SuppressWarnings("unused")
public class PaginatedBuilder extends BaseBuilder<PaginatedMenu, PaginatedBuilder> {
    private int pages = 2;

    /**
     * Set the number of pages for the paginated builder.
     *
     * @param  pages  the number of pages to set
     * @return        the builder for chaining
     */
    @NotNull
    @Contract("_ -> this")
    public PaginatedBuilder pages(final int pages) {
        this.pages = pages;
        return this;
    }

    @NotNull
    @Override
    @Contract(" -> new")
    public PaginatedMenu create() {
        checkRows(rows);
        final PaginatedMenu menu = type == MenuType.CHEST
                ? PaginatedMenu.create(title, rows, pages, modifiers)
                : PaginatedMenu.create(title, type, pages, modifiers);
        menuConsumer.accept(menu);
        return menu;
    }
}
