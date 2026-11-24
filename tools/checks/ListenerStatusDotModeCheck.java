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
        System.out.println("ListenerStatusDotModeCheck passed");
    }
}
