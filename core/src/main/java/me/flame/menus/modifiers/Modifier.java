package me.flame.menus.modifiers;

import java.util.EnumSet;

public enum Modifier {
    DISABLE_ITEM_SWAP,
    DISABLE_ITEM_REMOVAL,
    DISABLE_ITEM_ADD,
    DISABLE_ITEM_CLONE;

    public static final EnumSet<Modifier> ALL = EnumSet.allOf(Modifier.class);
}
