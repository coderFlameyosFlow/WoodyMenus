package me.flame.menus.menu;

@SuppressWarnings("unused")
public enum ActionResponse {
    RETRY,
    DONE;

    public boolean isDone() {
        return this == DONE;
    }

    public boolean isRetry() {
        return this == RETRY;
    }
}
