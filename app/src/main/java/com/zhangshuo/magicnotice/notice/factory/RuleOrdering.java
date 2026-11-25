package com.zhangshuo.magicnotice.notice.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Existing visible rule order: enabled-first default, then the saved ID ordering. */
public final class RuleOrdering {
    private RuleOrdering() { }
    public interface Id<T> { int get(T rule); }
    public interface Enabled<T> { boolean get(T rule); }

    public static <T> List<T> displayed(List<T> raw, Enabled<T> enabled, Id<T> id, String saved) {
        List<T> ordered = new ArrayList<>(raw.size());
        List<T> disabled = new ArrayList<>();
        for (T rule : raw) (enabled.get(rule) ? ordered : disabled).add(rule);
        ordered.addAll(disabled);
        applySavedOrder(ordered, id, saved);
        return ordered;
    }

    public static <T> void applySavedOrder(List<T> rules, Id<T> id, String saved) {
        if (saved == null || saved.isEmpty()) return;
        int next = 0;
        for (String token : saved.split(";")) {
            if (next == rules.size()) break;
            try {
                int wanted = Integer.parseInt(token);
                for (int j = next; j < rules.size(); j++) {
                    if (id.get(rules.get(j)) == wanted) {
                        Collections.swap(rules, next++, j);
                        break;
                    }
                }
            } catch (NumberFormatException ignored) { }
        }
    }
}
