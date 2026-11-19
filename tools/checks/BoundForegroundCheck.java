package com.zhangshuo.magicnotice.notice;

public final class BoundForegroundCheck {
    private static final class Fake implements BoundForeground.Host {
        int promotions, demotions, errors;
        boolean foreground, deny, failPromote, failDemote, unknown;
        Runnable onInspect = () -> {};

        @Override public Boolean isForeground() {
            onInspect.run();
            return unknown ? null : foreground;
        }

        @Override public void promote() {
            promotions++;
            if (failPromote) throw new IllegalStateException("promote");
            if (!deny) foreground = true;
        }

        @Override public void demote() {
            demotions++;
            if (failDemote) throw new IllegalStateException("demote");
            foreground = false;
        }

        @Override public void failed(String action, RuntimeException error) {
            errors++;
        }
    }

    private static void check(boolean condition, String reason) {
        if (!condition) throw new AssertionError(reason);
    }

    public static void main(String[] args) {
        BoundForeground controller = new BoundForeground();
        Fake host = new Fake();
        // The production lambda reads the live stored flag/permission and lifecycle at run time.
        boolean[] bound = {false}, enabled = {false}, permitted = {true}, destroyed = {false};
        BoundForeground.Eligibility eligible = () -> bound[0] && enabled[0] && permitted[0] && !destroyed[0];
        Runnable refresh = () -> controller.reconcile(eligible, false, host);
        Runnable foregroundResume = () -> controller.reconcile(eligible, true, host);

        refresh.run();
        bound[0] = true;
        refresh.run();
        foregroundResume.run();
        check(host.promotions == 0 && host.demotions == 0, "default off: bind and resume do nothing");

        enabled[0] = true;
        refresh.run();
        foregroundResume.run();
        check(host.foreground && host.promotions == 1, "one promotion when bound; actual active does not re-promote");

        enabled[0] = false;
        refresh.run();
        check(!host.foreground && bound[0] && host.demotions == 1, "off demotes without touching binding");
        bound[0] = false;
        enabled[0] = true;
        refresh.run();
        check(host.promotions == 1, "enabled while unbound does not start anything");

        bound[0] = true;
        refresh.run();
        bound[0] = false;
        refresh.run();
        check(!host.foreground && host.promotions == 2 && host.demotions == 2, "disconnect removes foreground");
        bound[0] = true;
        refresh.run();
        check(host.foreground && host.promotions == 3, "reconnect retries only while still enabled");

        permitted[0] = false;
        foregroundResume.run();
        check(!host.foreground && host.demotions == 3, "permission revoke may not promote");
        permitted[0] = true;
        host.failPromote = true;
        refresh.run();
        check(!host.foreground && host.errors == 1, "explicit denial does not kill listener");
        host.failPromote = false;
        host.deny = true;
        foregroundResume.run();
        check(!host.foreground, "normal return may still be a silent platform denial");
        host.deny = false;
        foregroundResume.run();
        check(host.foreground && host.promotions == 6, "visible activity retries after denial");

        host.onInspect = () -> enabled[0] = false;
        foregroundResume.run();
        check(!host.foreground && host.promotions == 6, "stale on request cannot survive switch changing to off");
        host.onInspect = () -> {};
        refresh.run(); // a delayed stale callback reads the live switch again
        check(host.promotions == 6, "late callback after off must not promote");

        enabled[0] = true;
        refresh.run();
        host.unknown = true;
        refresh.run();
        check(host.promotions == 7, "unknown state does not induce background retry loops");
        foregroundResume.run();
        check(host.promotions == 8, "visible foreground recovery can retry an unknown state");

        host.failDemote = true;
        destroyed[0] = true;
        refresh.run();
        check(host.errors == 2 && host.foreground, "report failed demotion and retain cleanup intent");
        host.failDemote = false;
        refresh.run();
        foregroundResume.run();
        check(!host.foreground && host.promotions == 8, "destruction cleans up and never re-promotes");
        System.out.println("BoundForeground: default off, opt-in, reconnect, rapid switch, denial and cleanup passed.");
    }
}
