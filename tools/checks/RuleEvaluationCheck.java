package com.zhangshuo.magicnotice.notice.factory;

import java.util.Arrays;
import java.util.List;
import static com.zhangshuo.magicnotice.notice.factory.RuleEvaluation.Truth.*;
import static com.zhangshuo.magicnotice.notice.factory.RuleEvaluation.Status.*;

public final class RuleEvaluationCheck {
    static final class Rule {
        final boolean enabled, dnd;
        final RuleEvaluation.Truth[] conditions;
        Rule(boolean enabled, boolean dnd, RuleEvaluation.Truth... conditions) {
            this.enabled = enabled; this.dnd = dnd; this.conditions = conditions;
        }
    }
    static class Fixture implements RuleEvaluation.Input<Rule, RuleEvaluation.Truth> {
        final RuleEvaluation.Truth dnd;
        int tests;
        Fixture(RuleEvaluation.Truth dnd) { this.dnd = dnd; }
        public boolean enabled(Rule r) { return r.enabled; }
        public boolean respectsDnd(Rule r) { return r.dnd; }
        public RuleEvaluation.Truth dnd() { return dnd; }
        public List<RuleEvaluation.Truth> conditions(Rule r) { return Arrays.asList(r.conditions); }
        public RuleEvaluation.Truth test(RuleEvaluation.Truth truth) { ++tests; return truth; }
    }
    static void check(boolean value) { if (!value) throw new AssertionError(); }
    public static void main(String[] args) {
        Rule off = new Rule(false, false, YES);
        Rule respectsDnd = new Rule(true, true, YES);
        Rule no = new Rule(true, false, NO);
        Rule winner = new Rule(true, false, YES);
        Rule tail = new Rule(true, false, YES);
        Fixture live = new Fixture(YES);
        List<Rule> ordered = Arrays.asList(off, respectsDnd, no, winner, tail);
        RuleEvaluation.Result<Rule,RuleEvaluation.Truth> r = RuleEvaluation.evaluate(ordered, live, true);
        check(r.winner == winner && live.tests == 2);
        check(r.rows.stream().map(row -> row.status).collect(java.util.stream.Collectors.toList())
                .equals(Arrays.asList(DISABLED, DND, NO_MATCH, MATCH, NOT_CHECKED)));
        check(RuleEvaluation.evaluate(ordered, new Fixture(YES), false).winner == winner);
        // Replay the exact same candidates with the current DND state changed.
        r = RuleEvaluation.evaluate(ordered, new Fixture(NO), true);
        check(r.winner == respectsDnd && r.firstPossible == null && r.rows.get(1).status == MATCH);
        r = RuleEvaluation.evaluate(ordered, new Fixture(YES), true);
        check(r.winner == winner && r.firstPossible == null && r.rows.get(1).status == DND);
        Rule unknownClock = new Rule(true, false, UNKNOWN, NO);
        r = RuleEvaluation.evaluate(Arrays.asList(unknownClock, winner, tail), new Fixture(UNKNOWN), true);
        check(r.rows.get(0).status == NO_MATCH && r.winner == winner);
        r = RuleEvaluation.evaluate(Arrays.asList(off, respectsDnd, winner, tail), new Fixture(UNKNOWN), true);
        check(r.winner == null && r.firstPossible == respectsDnd && r.conditionalWinner == winner);
        check(r.rows.get(2).status == CONDITIONAL_MATCH && r.rows.get(3).status == NOT_CHECKED);
        r = RuleEvaluation.evaluate(Arrays.asList(no, new Rule(true, false, UNKNOWN)), new Fixture(UNKNOWN), true);
        check(r.winner == null && r.firstPossible != null && r.conditionalWinner == null);
        r = RuleEvaluation.evaluate(Arrays.asList(no, new Rule(true, false)), new Fixture(NO), true);
        check(r.winner != null && r.rows.get(1).status == MATCH);
        // A partial saved order was previously applied after different initial groupings.
        // This is a regression example where identical rules had different live and displayed precedence.
        List<Integer> raw = Arrays.asList(100, 1, 2, 3);
        List<Integer> displayed = RuleOrdering.displayed(raw, id -> id != 100, id -> id, "3;");
        check(displayed.equals(Arrays.asList(3, 2, 1, 100)));
        check(RuleOrdering.displayed(raw, id -> id != 100, id -> id, "")
                .equals(Arrays.asList(1, 2, 3, 100)));
        check(RuleOrdering.displayed(raw, id -> id != 100, id -> id, "3;bad;3;")
                .equals(displayed));
        System.out.println("RuleEvaluationCheck passed");
    }
}
