package com.zhangshuo.magicnotice.notice.factory;

/** Side effects are safe only for a successfully loaded, current rule generation. */
final class RuleReloadState {
    private int generation;
    private boolean loading;
    private boolean usable;

    int begin() {
        loading = true;
        usable = false;
        return ++generation;
    }

    boolean finish(int completedGeneration, boolean success) {
        if (generation != completedGeneration || !loading) return false;
        loading = false;
        usable = success;
        return true;
    }

    boolean isLoading() { return loading; }
    int currentGeneration() { return generation; }
    boolean canAct(int selectedGeneration) { return !loading && usable && generation == selectedGeneration; }
}
