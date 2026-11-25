package com.zhangshuo.magicnotice.notice.factory;

import java.util.ArrayList;
import java.util.List;

/** Shared ordered rule evaluation. Does not execute actions or write records. */
public final class RuleEvaluation {
    private RuleEvaluation() { }

    public enum Truth { YES, NO, UNKNOWN }
    public enum Status { DISABLED, DND, NO_MATCH, POSSIBLE, MATCH, CONDITIONAL_MATCH, NOT_CHECKED }

    public interface Input<R, C> {
        boolean enabled(R rule);
        boolean respectsDnd(R rule);
        Truth dnd();
        List<C> conditions(R rule);
        Truth test(C condition);
    }

    public static final class Detail<C> {
        public final C condition;
        public final Truth value;
        Detail(C condition, Truth value) { this.condition = condition; this.value = value; }
    }

    public static final class Row<R, C> {
        public final R rule;
        public final Status status;
        public final List<Detail<C>> details;
        Row(R rule, Status status, List<Detail<C>> details) {
            this.rule = rule; this.status = status; this.details = details;
        }
    }

    public static final class Result<R, C> {
        public final List<Row<R, C>> rows = new ArrayList<>();
        public R winner;
        public R firstPossible;
        public R conditionalWinner;
    }

    public static <R, C> Result<R, C> evaluate(List<R> ordered, Input<R, C> input, boolean explain) {
        Result<R, C> result = new Result<>();
        for (R rule : ordered) {
            Status status;
            List<Detail<C>> details = explain ? new ArrayList<>() : null;
            if (result.winner != null || result.conditionalWinner != null) {
                status = Status.NOT_CHECKED;
            } else if (!input.enabled(rule)) {
                status = Status.DISABLED;
            } else {
                Truth dnd = input.respectsDnd(rule) ? input.dnd() : Truth.NO;
                if (dnd == Truth.YES) {
                    status = Status.DND;
                } else {
                    boolean possible = dnd == Truth.UNKNOWN;
                    boolean no = false;
                    List<C> conditions = input.conditions(rule);
                    if (conditions != null) {
                        for (C condition : conditions) {
                            Truth value = input.test(condition);
                            if (explain) details.add(new Detail<>(condition, value));
                            if (value == Truth.NO) { no = true; break; }
                            if (value == Truth.UNKNOWN) possible = true;
                        }
                    }
                    if (no) {
                        status = Status.NO_MATCH;
                    } else if (possible) {
                        status = Status.POSSIBLE;
                        if (result.firstPossible == null) result.firstPossible = rule;
                    } else if (result.firstPossible != null) {
                        status = Status.CONDITIONAL_MATCH;
                        result.conditionalWinner = rule;
                    } else {
                        status = Status.MATCH;
                        result.winner = rule;
                        if (!explain) break;
                    }
                }
            }
            if (explain) result.rows.add(new Row<>(rule, status, details));
        }
        return result;
    }
}
