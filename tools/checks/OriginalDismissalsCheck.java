package com.zhangshuo.magicnotice.notice;

public final class OriginalDismissalsCheck {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        OriginalDismissals guard = new OriginalDismissals();

        OriginalDismissals.Delivery old = guard.begin("same", 10);
        check(!guard.canDismiss(old) && guard.committedBatch(512).isEmpty(), "must not cancel before insert");
        OriginalDismissals.Delivery newer = guard.begin("same", 10); // even same wall-clock millisecond
        check(!guard.commit(old, () -> true), "late success must not cancel newer same-key callback");
        guard.finish(newer); // retain or write error
        check(!guard.commit(old, () -> true) && guard.committedBatch(512).isEmpty(), "write failure/retain must preserve original");

        OriginalDismissals.Delivery tiedOld = guard.begin("tied", 15);
        OriginalDismissals.Delivery tiedNew = guard.begin("tied", 15);
        guard.removed("tied", 15);
        check(!guard.commit(tiedOld, () -> true) && !guard.commit(tiedNew, () -> true), "ambiguous same-ms removal fails open");

        OriginalDismissals.Delivery locked = guard.begin("same", 20);
        check(guard.commit(locked, () -> true) && guard.committedBatch(512).size() == 1, "defer only committed when locked");
        OriginalDismissals.Delivery replacement = guard.begin("same", 21);
        guard.removed("same", 20);
        check(!guard.canDismiss(locked) && guard.commit(replacement, () -> true), "old removal cannot erase newer write");
        guard.removed("same", 21);
        check(!guard.canDismiss(replacement), "system removal invalidates pending dismissal");

        OriginalDismissals.Delivery disconnected = guard.begin("disconnect", 30);
        guard.reset();
        check(!guard.commit(disconnected, () -> true), "late callback from old connection must do nothing");

        boolean[] fresh = {true};
        OriginalDismissals.Delivery beforeRuleEdit = guard.begin("edited", 50);
        check(guard.commit(beforeRuleEdit, () -> fresh[0]), "commit while rule fresh");
        fresh[0] = false;
        check(!guard.canDismiss(beforeRuleEdit), "rule edit must invalidate after commit, before unlock");
        guard.finish(beforeRuleEdit);

        for (int i = 0; i < 800; i++) guard.commit(guard.begin("burst-" + i, i), () -> true);
        check(guard.committedBatch(512).size() == 512, "tracking must be bounded under burst");
        check(guard.committedBatch(512).get(0).postTime == 288, "shed oldest cancellations, preserve rows");
        int batchCount = 0;
        while (!guard.committedBatch(4).isEmpty()) {
            java.util.List<OriginalDismissals.Delivery> batch = guard.committedBatch(4);
            check(batch.size() <= 4, "bounded unlock batch");
            for (OriginalDismissals.Delivery delivery : batch) guard.finish(delivery);
            batchCount++;
        }
        check(batchCount == 128, "all retained records drained across small turns");
        System.out.println("OriginalDismissals: cold/failure, same-key retain and overwrites, lock, removal, disconnect, burst passed");
    }
}
