package me.flame.menus.util;

import java.util.function.Supplier;

public final class Cleaning {
    public static <E> E ifOrElse(boolean condition, Supplier<E> value, Supplier<E> orElse) {
        E e;
        if (condition) {
            e = value.get();
        } else {
            e = orElse.get();
        }
        return e;
    }

    public static <E> E ifOrElse(boolean condition, E value, E orElse) {
        E e;
        if (condition) {
            e = value;
        } else {
            e = orElse;
        }
        return e;
    }

    public static <E> E ifOrElse(boolean condition, E value, Supplier<E> orElse) {
        E e;
        if (condition) {
            e = value;
        } else {
            e = orElse.get();
        }
        return e;
    }
}
