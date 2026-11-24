package com.zhangshuo.magicnotice.notice;

/** Shared policy for the user choice and the optional listener-owned status window. */
public final class ListenerStatusDotMode {
    public static final int OFF = 0;
    public static final int TRANSPARENT = 1;
    public static final int DOT = 2;

    private ListenerStatusDotMode() { }

    public static boolean isEnabled(int mode) {
        return mode == TRANSPARENT || mode == DOT;
    }

    public static int selected(int choice, boolean permission) {
        return permission && isEnabled(choice) ? choice : OFF;
    }

    static int displayed(int mode, boolean connected, boolean permission) {
        return connected ? selected(mode, permission) : OFF;
    }
}
