package com.zhangshuo.magicnotice.backup;

import com.zhangshuo.magicnotice.notice.factory.RuleOrdering;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class RuleBackupCheck {
    public static void main(String[] args) throws Exception {
        RuleBackup.Rule first = new RuleBackup.Rule("规则甲",
                "[{\"type\":1000,\"filterType\":true,\"txtList\":[\"A-*:值\"]}]",
                "[{\"type\":2005,\"autoOpen\":true}]", true, true, false, true, "#FFFFFF", "3");
        RuleBackup.Rule second = new RuleBackup.Rule("Rule B", "[{\"type\":1006,\"filterType\":false,"
                + "\"dataList\":[{\"name\":\"Synthetic\",\"phone\":\"5550100\"}]}]",
                "[{\"type\":2001,\"name\":\"Fixture\",\"timePattern\":\"1000;200\",\"count\":2}]",
                false, false, true, false, "", "365");
        byte[] bytes = RuleBackup.encode(Arrays.asList(first, second));
        List<RuleBackup.Rule> rules = RuleBackup.read(new ByteArrayInputStream(bytes));
        if (!Arrays.equals(bytes, RuleBackup.encode(rules))) throw new AssertionError("roundtrip changed");
        if (!rules.get(0).wasEnabled || rules.get(1).wasEnabled || !rules.get(1).collect
                || !rules.get(0).retain || !rules.get(0).policy || !rules.get(0).name.equals("规则甲")) {
            throw new AssertionError("rule flags/order changed");
        }
        String valid = new String(bytes, StandardCharsets.UTF_8);
        reject(valid.replace("\"version\":1", "\"version\":2"), RuleBackup.Failure.UNSUPPORTED);
        reject(valid.replace("\"version\":1", "\"version\":1,\"version\":1"), RuleBackup.Failure.INVALID);
        reject(valid.replace("\"name\":\"Rule B\"", "\"id\":5,\"name\":\"Rule B\""), RuleBackup.Failure.INVALID);
        reject(valid.replace("\"phone\":", "\"id\":7,\"phone\":"), RuleBackup.Failure.INVALID);
        reject(valid.replace("\"type\":2005", "\"type\":2999"), RuleBackup.Failure.INVALID);
        reject(valid.replace("\"autoOpen\":true", "\"autoOpen\":\"true\""), RuleBackup.Failure.INVALID);
        reject(valid.replace("\"A-*:值\"", "\"*:-bad\""), RuleBackup.Failure.INVALID);
        reject(valid.replace("\"1000;200\"", "\"-8;word\""), RuleBackup.Failure.INVALID);
        reject(valid + " {}", RuleBackup.Failure.INVALID);
        reject(valid.substring(0, valid.length() - 10), RuleBackup.Failure.INVALID);
        reject("Rulelegacy-fixture", RuleBackup.Failure.LEGACY);
        reject("[[[[[[[[[[0]]]]]]]]]]", RuleBackup.Failure.TOO_LARGE);
        reject(new byte[]{(byte)0xC3, (byte)0x28}, RuleBackup.Failure.INVALID);
        reject(new byte[RuleBackup.MAX_BYTES + 1], RuleBackup.Failure.TOO_LARGE);

        Fixture one = new Fixture(1, true, false);
        Fixture two = new Fixture(2, false, false);
        Fixture three = new Fixture(3, true, false);
        Fixture firstIn = new Fixture(4, false, true);
        Fixture lastIn = new Fixture(5, false, true);
        Fixture firstAgain = new Fixture(6, false, true);
        Fixture lastAgain = new Fixture(7, false, true);
        List<Fixture> raw = Arrays.asList(lastAgain, firstAgain, lastIn, firstIn, three, two, one);
        compare(raw, "", "3,1,2,4,5,6,7");
        compare(raw, "2;3;", "2,3,1,4,5,6,7");
        compare(raw, "2;3;1;5;4;", "2,3,1,5,4,6,7");
        lastIn.enabled = true;
        compare(raw, "2;3;", "2,3,1,4,5,6,7");
        System.out.println("RuleBackup: codec round-trip, flags/actions, rejection and append ordering passed");
    }

    private static void compare(List<Fixture> raw, String order, String wanted) {
        String got = RuleOrdering.displayed(raw, x -> x.enabled, x -> x.id, x -> x.imported, order)
                .stream().map(x -> String.valueOf(x.id)).collect(Collectors.joining(","));
        if (!wanted.equals(got)) throw new AssertionError("expected " + wanted + " got " + got);
    }

    private static void reject(String source, RuleBackup.Failure reason) throws Exception {
        reject(source.getBytes(StandardCharsets.UTF_8), reason);
    }

    private static void reject(byte[] source, RuleBackup.Failure reason) throws Exception {
        try {
            RuleBackup.read(new ByteArrayInputStream(source));
        } catch (RuleBackup.Invalid error) {
            if (error.reason == reason) return;
            throw new AssertionError(reason + " != " + error.reason);
        }
        throw new AssertionError("accepted " + reason + " fixture");
    }

    private static final class Fixture {
        final int id;
        final boolean imported;
        boolean enabled;
        Fixture(int id, boolean enabled, boolean imported) {
            this.id = id;
            this.enabled = enabled;
            this.imported = imported;
        }
    }
}
