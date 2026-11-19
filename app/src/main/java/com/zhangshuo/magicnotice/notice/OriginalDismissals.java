package com.zhangshuo.magicnotice.notice;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Tracks only current, cancellable deliveries on the listener main thread. */
final class OriginalDismissals {
    interface Guard {
        boolean allowed();
    }

    static final class Delivery {
        final String key;
        final long postTime;
        Guard guard;
        Delivery(String key, long postTime) {
            this.key = key;
            this.postTime = postTime;
        }
    }

    private static final int MAX_TRACKED = 512;
    private final Map<String, Delivery> current = new LinkedHashMap<>();

    Delivery begin(String key, long postTime) {
        current.remove(key);
        if (current.size() >= MAX_TRACKED) current.remove(current.keySet().iterator().next());
        Delivery delivery = new Delivery(key, postTime);
        current.put(key, delivery);
        return delivery;
    }

    boolean commit(Delivery delivery, Guard guard) {
        if (current.get(delivery.key) != delivery || !guard.allowed()) return false;
        delivery.guard = guard;
        return true;
    }

    boolean canDismiss(Delivery delivery) {
        return current.get(delivery.key) == delivery && delivery.guard != null && delivery.guard.allowed();
    }

    void finish(Delivery delivery) {
        if (current.get(delivery.key) == delivery) current.remove(delivery.key);
    }

    void removed(String key, long postTime) {
        Delivery delivery = current.get(key);
        // Removal has no callback generation. If timestamps tie, this may suppress a
        // valid dismissal, but must never authorize cancelling an ambiguous newer post.
        if (delivery != null && delivery.postTime == postTime) finish(delivery);
    }

    List<Delivery> committedBatch(int limit) {
        List<Delivery> ready = new ArrayList<>();
        for (Delivery delivery : current.values()) {
            if (delivery.guard == null) continue;
            ready.add(delivery);
            if (ready.size() >= limit) break;
        }
        return ready;
    }

    void reset() {
        current.clear();
    }
}
