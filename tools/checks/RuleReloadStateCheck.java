package com.zhangshuo.magicnotice.notice.factory;

public final class RuleReloadStateCheck {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        RuleReloadState state = new RuleReloadState();
        int cold = state.begin();
        check(state.isLoading() && !state.canAct(cold), "cold load must not act");
        check(state.finish(cold, true) && state.canAct(cold), "loaded policy acts");
        int reconnect = state.begin();
        check(!state.canAct(cold) && !state.canAct(reconnect), "invalid pending old-rule writes");
        int editing = state.begin();
        check(!state.finish(reconnect, true) && state.isLoading(), "stale query cannot finish latest reload");
        check(state.finish(editing, false) && !state.canAct(editing), "failed read cannot filter");
        int retry = state.begin();
        check(state.finish(retry, true) && state.canAct(retry), "fresh retry can act");
        check(!state.canAct(editing) && !state.canAct(cold), "old callback still cannot act");
        System.out.println("RuleReloadState: cold, reconnect, stale edit, load failure, fresh retry passed");
    }
}
