package me.flame.menus.menu;

import lombok.NonNull;

public class Result {
    public String result;
    private static final String DENIED = "denied";
    private static final String ALLOWED = "allowed";

    private Result(String result) {
        this.result = result;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Result)) return false;
        Result r = (Result) o;
        return result.equals(r.result);
    }

    public static Result denied() {
        return new Result(DENIED);
    }

    public static Result allowed() {
        return new Result(ALLOWED);
    }

    public void set(@NonNull Result r) {
        String result = r.result;
        if (!result.equals(DENIED) && !result.equals(ALLOWED))
            throw new IllegalArgumentException(
                    "Must be 'allowed' or 'denied'" +
                    "\nResult = " + result +
                    "\nFix: Change the result to 'allowed' or 'denied'"
            );
        this.result = result;
    }

    public boolean isDenied() {
        return result.equals(DENIED);
    }
}
