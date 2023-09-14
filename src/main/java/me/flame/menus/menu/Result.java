package me.flame.menus.menu;

import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({ "unused", "StringEquality" })
public final class Result {
    public String result;
    private static final String DENIED = "denied";
    private static final String ALLOWED = "allowed";

    private Result(String result) {
        this.result = result;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Result)) return false;
        Result r = (Result) o;
        return result == r.result;
    }

    /**
     * Generates a new Result object with the value DENIED.
     *
     * @return         	A new Result object with the value DENIED.
     */
    @NotNull
    @Contract(value = " -> new", pure = true)
    public static Result denied() {
        return new Result(DENIED);
    }

    /**
     * Generates a new Result object with the value ALLOWED.
     *
     * @return         	A new Result object with the value ALLOWED.
     */
    @NotNull
    @Contract(value = " -> new", pure = true)
    public static Result allowed() {
        return new Result(ALLOWED);
    }

    /**
     * Sets the result of the operation.
     *
     * @param  r the result to be set
     */
    public void set(@NonNull Result r) {
        this.result = r.result;
    }

    public boolean isDenied() {
        return result == DENIED;
    }

    public boolean isAllowed() {
        return result == ALLOWED;
    }
}
