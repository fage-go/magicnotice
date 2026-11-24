package com.zhangshuo.magicnotice.notice;

/** Shared policy for the user choice and the optional listener-owned status window. */
public final class ListenerStatusDotMode {
    public static final int OFF = 0;
    public static final int TRANSPARENT = 1;
    public static final int DOT = 2;

    public static final int TOP_LEFT = 0, TOP_RIGHT = 1, BOTTOM_LEFT = 2, BOTTOM_RIGHT = 3;
    private static final int PACKED = Integer.MIN_VALUE;
    private static final int ENABLED = 1 << 30;

    // A single atomic MMKV int: marker, enabled bit, corner, opacity step, RGB.
    public static int configure(boolean enabled, int corner, int rgb, int opacityStep) {
        return PACKED | (enabled ? ENABLED : 0) | ((corner & 3) << 28)
                | (Math.max(0, Math.min(10, opacityStep)) << 24) | (rgb & 0xFFFFFF);
    }

    private static boolean packed(int mode) {
        return (mode & PACKED) != 0 && ((mode >>> 24) & 15) <= 10;
    }

    public static int corner(int mode) { return packed(mode) ? (mode >>> 28) & 3 : TOP_RIGHT; }
    public static int color(int mode) { return packed(mode) ? 0xFF000000 | (mode & 0xFFFFFF) : 0xFF000000; }
    public static int opacity(int mode) { return packed(mode) ? (mode >>> 24) & 15 : mode == TRANSPARENT ? 0 : 5; }

    public static int disable(int mode) {
        return configure(false, corner(mode), color(mode), opacity(mode));
    }

    static float effectiveAlpha(int mode, float limit) {
        return Math.min(opacity(mode) / 10f, Math.max(0f, Math.min(1f, limit)));
    }

    private ListenerStatusDotMode() { }

    public static boolean isEnabled(int mode) {
        return mode == TRANSPARENT || mode == DOT || (packed(mode) && (mode & ENABLED) != 0);
    }

    public static int selected(int choice, boolean permission) {
        return permission && isEnabled(choice) ? choice : OFF;
    }

    static int displayed(int mode, boolean connected, boolean permission) {
        return connected ? selected(mode, permission) : OFF;
    }
}
