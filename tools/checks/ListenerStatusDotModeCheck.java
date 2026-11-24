package com.zhangshuo.magicnotice.notice;

import static com.zhangshuo.magicnotice.notice.ListenerStatusDotMode.*;

public final class ListenerStatusDotModeCheck {
    private static void expect(int expected, int actual) {
        if (expected != actual) throw new AssertionError(expected + " != " + actual);
    }

    public static void main(String[] args) {
        expect(OFF, selected(OFF, true)); // cancel/back is not a valid enable choice
        expect(OFF, selected(99, true));
        expect(OFF, displayed(OFF, true, true)); // default, regardless of connection/permission
        for (int choice : new int[] {TRANSPARENT, DOT}) {
            expect(OFF, selected(choice, false)); // also covers revocation between dialog open and selection
            int saved = selected(choice, true);
            expect(choice, saved);
            expect(OFF, displayed(saved, false, true)); // listener disconnected/destroyed
            expect(choice, displayed(saved, true, true)); // reconnect
            expect(OFF, displayed(saved, true, false)); // permission revoked
            expect(choice, displayed(saved, true, true)); // permission restored while still opted in
            expect(OFF, displayed(OFF, true, true)); // explicit disable
        }
        expect(TOP_RIGHT, corner(TRANSPARENT));
        expect(0, opacity(TRANSPARENT));
        expect(5, opacity(DOT));
        expect(0xFF000000, color(DOT));
        for (int corner = TOP_LEFT; corner <= BOTTOM_RIGHT; corner++) {
            for (int opacity = 0; opacity <= 10; opacity++) {
                int saved = configure(true, corner, 0xE54D42, opacity);
                expect(corner, corner(saved));
                expect(opacity, opacity(saved));
                expect(0xFFE54D42, color(saved));
                expect(saved, selected(saved, true));
                expect(OFF, selected(saved, false));
                expect(OFF, displayed(saved, false, true));
                int disabled = disable(saved);
                expect(OFF, displayed(disabled, true, true));
                expect(corner, corner(disabled));
                expect(opacity, opacity(disabled));
                expect(0xFFE54D42, color(disabled));
                float requested = opacity / 10f;
                if (effectiveAlpha(saved, 0.8f) != Math.min(requested, 0.8f)) {
                    throw new AssertionError("unsafe or incorrect alpha");
                }
                if (effectiveAlpha(saved, 0.6f) != Math.min(requested, 0.6f)) {
                    throw new AssertionError("device-specific cap ignored");
                }
            }
        }
        System.out.println("ListenerStatusDotModeCheck passed");
    }
}
