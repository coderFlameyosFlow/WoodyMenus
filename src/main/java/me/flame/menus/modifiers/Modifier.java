package me.flame.menus.modifiers;

import java.util.Arrays;
import java.util.List;

public enum Modifier {
    DISABLE_ITEM_SWAP,
    DISABLE_ITEM_REMOVAL,
    DISABLE_ITEM_ADD,
    DISABLE_ITEM_CLONE;

    public static final List<Modifier> ALL = Arrays.asList(
            Modifier.DISABLE_ITEM_ADD,
            Modifier.DISABLE_ITEM_CLONE,
            Modifier.DISABLE_ITEM_REMOVAL,
            Modifier.DISABLE_ITEM_SWAP
    );
}
