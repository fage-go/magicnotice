package com.zhangshuo.magicnotice.notice.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/** Existing visible rule order: enabled-first default, then the saved ID ordering. */
public final class RuleOrdering {
    private RuleOrdering() { }
    public interface Id<T> { int get(T rule); }
    public interface Enabled<T> { boolean get(T rule); }

    public static <T> List<T> displayed(List<T> raw, Enabled<T> enabled, Id<T> id, String saved) {
        return displayed(raw, enabled, id, unused -> false, saved);
    }

    public static <T> List<T> displayed(List<T> raw, Enabled<T> enabled, Id<T> id,
                                         Enabled<T> imported, String saved) {
        Set<Integer> pinned = new HashSet<>();
        if (saved != null && !saved.isEmpty()) {
            for (String token : saved.split(";")) {
                try { pinned.add(Integer.parseInt(token)); }
                catch (NumberFormatException ignored) { }
            }
        }
        List<T> ordered = new ArrayList<>(raw.size());
        List<T> appended = new ArrayList<>();
        List<T> disabled = new ArrayList<>();
        for (T rule : raw) {
            if (imported.get(rule) && !pinned.contains(id.get(rule))) appended.add(rule);
            else (enabled.get(rule) ? ordered : disabled).add(rule);
        }
        ordered.addAll(disabled);
        applySavedOrder(ordered, id, saved);
        Collections.sort(appended, (a, b) -> Integer.compare(id.get(a), id.get(b)));
        ordered.addAll(appended);
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
