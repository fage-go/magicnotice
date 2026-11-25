package com.zhangshuo.magicnotice.backup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** Versioned rule-only interchange. Deliberately independent of IDs and local encryption. */
public final class RuleBackup {
    public static final String EXTENSION = ".cicada-rules.json";
    private static final String FORMAT = "magicnotice.rules";
    private static final int VERSION = 1;
    static final int MAX_BYTES = 4 * 1024 * 1024;
    static final int MAX_RULES = 500;
    private static final int MAX_ITEMS = 64;
    private static final int MAX_ARRAY = 1024;
    private static final int MAX_TEXT = 8192;
    private static final int MAX_DEPTH = 8;

    public enum Failure { INVALID, TOO_LARGE, UNSUPPORTED, LEGACY }
    public static final class Invalid extends IOException {
        public final Failure reason;
        Invalid(Failure reason) {
            super(reason.name());
            this.reason = reason;
        }
    }

    public static final class Rule {
        public final String name, conditions, behaviors, tagColor, cleanTime;
        /** Original switch from the backup; never used to activate imported rules. */
        public final boolean wasEnabled, retain, collect, policy;

        public Rule(String name, String conditions, String behaviors, boolean wasEnabled,
                    boolean retain, boolean collect, boolean policy, String tagColor, String cleanTime) {
            this.name = name;
            this.conditions = conditions;
            this.behaviors = behaviors;
            this.wasEnabled = wasEnabled;
            this.retain = retain;
            this.collect = collect;
            this.policy = policy;
            this.tagColor = tagColor;
            this.cleanTime = cleanTime;
        }
    }

    private RuleBackup() { }

    public static byte[] encode(List<Rule> rules) throws Invalid {
        require(!rules.isEmpty());
        if (rules.size() > MAX_RULES) throw new Invalid(Failure.TOO_LARGE);
        JsonObject doc = new JsonObject();
        doc.addProperty("format", FORMAT);
        doc.addProperty("version", VERSION);
        JsonArray entries = new JsonArray();
        doc.add("rules", entries);
        int approxBytes = 0;
        for (Rule rule : rules) {
            JsonObject row = new JsonObject();
            row.addProperty("name", rule.name);
            row.add("conditions", parse(rule.conditions == null ? "[]" : rule.conditions));
            row.add("behaviors", parse(rule.behaviors == null ? "[]" : rule.behaviors));
            row.addProperty("enabled", rule.wasEnabled);
            row.addProperty("retain", rule.retain);
            row.addProperty("collect", rule.collect);
            row.addProperty("policy", rule.policy);
            row.addProperty("tagColor", rule.tagColor == null ? "" : rule.tagColor);
            row.addProperty("cleanTime", rule.cleanTime == null || rule.cleanTime.isEmpty() ? "0" : rule.cleanTime);
            validateRule(row);
            approxBytes += row.toString().getBytes(StandardCharsets.UTF_8).length;
            if (approxBytes > MAX_BYTES) throw new Invalid(Failure.TOO_LARGE);
            entries.add(row);
        }
        byte[] result = doc.toString().getBytes(StandardCharsets.UTF_8);
        if (result.length > MAX_BYTES) throw new Invalid(Failure.TOO_LARGE);
        return result;
    }

    public static List<Rule> read(InputStream source) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int count;
        while ((count = source.read(buffer)) != -1) {
            // A broken provider must not spin continuously.
            if (count == 0) throw new Invalid(Failure.INVALID);
            if (bytes.size() > MAX_BYTES - count) throw new Invalid(Failure.TOO_LARGE);
            bytes.write(buffer, 0, count);
        }
        final String text;
        try {
            text = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes.toByteArray())).toString();
        } catch (CharacterCodingException ex) {
            throw new Invalid(Failure.INVALID);
        }
        if (text.startsWith("Rule")) throw new Invalid(Failure.LEGACY);
        JsonObject doc = object(parse(text));
        if (!doc.has("format") || !FORMAT.equals(string(doc.get("format")))) {
            throw new Invalid(Failure.UNSUPPORTED);
        }
        if (!doc.has("version") || integer(doc.get("version"), 0, Integer.MAX_VALUE) != VERSION) {
            throw new Invalid(Failure.UNSUPPORTED);
        }
        exact(doc, "format version rules");
        JsonArray entries = array(doc.get("rules"), MAX_RULES);
        require(entries.size() > 0);
        List<Rule> result = new ArrayList<>(entries.size());
        for (JsonElement entry : entries) result.add(validateRule(object(entry)));
        return result;
    }

    private static Rule validateRule(JsonObject row) throws Invalid {
        exact(row, "name conditions behaviors enabled retain collect policy tagColor cleanTime");
        String name = string(row.get("name"));
        require(!name.trim().isEmpty() && name.length() <= 512);
        JsonArray conditions = array(row.get("conditions"), MAX_ITEMS);
        JsonArray behaviors = array(row.get("behaviors"), MAX_ITEMS);
        for (JsonElement item : conditions) validateCondition(object(item));
        for (JsonElement item : behaviors) validateBehavior(object(item));
        String tagColor = string(row.get("tagColor"));
        require(tagColor.isEmpty() || color(tagColor));
        String cleanTime = string(row.get("cleanTime"));
        require(cleanTime.matches("0|1|3|7|15|30|90|180|365"));
        return new Rule(name, conditions.toString(), behaviors.toString(), bool(row.get("enabled")),
                bool(row.get("retain")), bool(row.get("collect")), bool(row.get("policy")), tagColor, cleanTime);
    }

    private static void validateCondition(JsonObject data) throws Invalid {
        int type = (int) integer(data.get("type"), 1000, 1006);
        bool(data.get("filterType"));
        String fields = "";
        switch (type) {
            case 1000: fields = "T:txtList"; break;
            case 1001: fields = "T:packageNameList"; break;
            case 1003: fields = "L:startTime L:endTime"; break;
            case 1005: fields = "H:dataList"; break;
            case 1006: fields = "P:dataList"; break;
            default: break; // No payload beyond type/filterType for orientation and screen lock.
        }
        payload(data, "type filterType", fields);
    }

    private static void validateBehavior(JsonObject data) throws Invalid {
        int type = (int) integer(data.get("type"), 2001, 2012);
        String fields;
        switch (type) {
            case 2001: case 2008: fields = "S:name S:timePattern N:count"; break;
            case 2002: fields = "S:ringtoneTitle S:ringtoneUri N:count"; break;
            case 2003: fields = "B:notice S:languageName F:speed F:voice N:volume N:contentType S:prefix S:suffix B:autoPlay"; break;
            case 2004: fields = "N:contentType N:txtSizeSP C:txtColorHex F:fontAlpha N:bgRadius C:bgColorHex F:bgAlpha N:enterEffectType N:stayEffectTime N:exitEffectType N:clickType N:doubleClickType"; break;
            case 2005: fields = "B:autoOpen"; break;
            case 2006: fields = "S:name S:regular"; break;
            case 2007: fields = "N:txtSizeSP C:txtColorHex F:fontAlpha N:bgType C:bgColorHex F:bgAlpha N:showType N:showTime N:clickType"; break;
            case 2012: fields = "N:viewType N:animType N:strokeWidth S:colorArray N:speed C:colorShadow N:repeatCount"; break;
            default: fields = "";
        }
        payload(data, "type", fields);
    }

    private static void payload(JsonObject object, String required, String optional) throws Invalid {
        if (!required.isEmpty()) for (String key : required.split(" ")) require(object.has(key));
        for (Map.Entry<String, JsonElement> field : object.entrySet()) {
            String key = field.getKey();
            if ((" " + required + " ").contains(" " + key + " ") && !key.contains(" ")) continue;
            int fieldType = -1;
            for (String token : optional.split(" ")) {
                if (token.length() > 2 && token.substring(2).equals(key)) {
                    fieldType = token.charAt(0);
                    break;
                }
            }
            JsonElement value = field.getValue();
            switch (fieldType) {
                case 'S':
                    if (!value.isJsonNull()) validateStringField(key, string(value));
                    break;
                case 'C': if (!value.isJsonNull()) { String c = string(value); require(c.isEmpty() || color(c)); } break;
                case 'B': bool(value); break;
                case 'L':
                    long time = integer(value, 0, 2359);
                    require(time % 100 < 60);
                    break;
                case 'N': integer(value, 0, maxInteger(key)); break;
                case 'F':
                    BigDecimal num = number(value);
                    int max = key.endsWith("Alpha") ? 1 : ("speed".equals(key) ? 10 : 200);
                    require(num.signum() >= 0 && num.compareTo(BigDecimal.valueOf(max)) <= 0);
                    break;
                case 'T':
                    for (JsonElement item : array(value, MAX_ARRAY)) {
                        // A built-in SMS sample can contain a null fallback package on a
                        // device without a default SMS app. No other string arrays accept null.
                        if ("packageNameList".equals(key) && item.isJsonNull()) continue;
                        String text = string(item);
                        if ("txtList".equals(key) && text.contains("-") && text.contains("*")
                                && text.contains(":")) {
                            require(text.matches("(?s)[ATC]-(?:[!]?[\\*]|\\*~|~\\*):.*"));
                        }
                    }
                    break;
                case 'H': case 'P':
                    for (JsonElement item : array(value, MAX_ARRAY)) {
                        JsonObject nested = object(item);
                        payload(nested, "", fieldType == 'H' ? "S:name S:value B:selected" : "S:name S:phone");
                        string(nested.get(fieldType == 'H' ? "value" : "phone"));
                    }
                    break;
                default: throw new Invalid(Failure.INVALID);
            }
        }
    }

    private static int maxInteger(String key) {
        switch (key) {
            case "count": case "repeatCount": case "volume": return 100;
            case "contentType": return 7; // Three bit flags in both barrage and TTS.
            case "viewType": case "showType": return 3;
            case "animType": case "bgType": return 4;
            case "enterEffectType": case "exitEffectType":
            case "clickType": case "doubleClickType": return 4;
            case "txtSizeSP": case "strokeWidth": case "bgRadius": case "speed": return 256;
            case "stayEffectTime": case "showTime": return 86_400_000;
            default: throw new IllegalArgumentException("Unexpected rule backup field");
        }
    }

    private static void validateStringField(String key, String text) throws Invalid {
        if ("timePattern".equals(key) && !text.isEmpty()) {
            require(text.matches("[0-9]{1,5}(;[0-9]{1,5}){0,31}"));
        } else if ("colorArray".equals(key) && !text.isEmpty()) {
            String[] colors = text.split(",", -1);
            require(colors.length <= 32);
            for (String c : colors) require(color(c));
        } else if ("regular".equals(key) && !text.isEmpty()) {
            require(text.length() <= 1024);
            try { Pattern.compile(text); }
            catch (PatternSyntaxException | StackOverflowError ex) { throw new Invalid(Failure.INVALID); }
        } else if ("ringtoneUri".equals(key) && !text.isEmpty()) {
            // Device-local sound assets may be absent after restore; never accept a remote URI.
            require(text.startsWith("content://") || text.startsWith("android.resource://")
                    || text.startsWith("file:///"));
        }
    }

    private static boolean color(String s) {
        return s.matches("#(?:[0-9a-fA-F]{6}|[0-9a-fA-F]{8})");
    }

    private static void exact(JsonObject object, String keys) throws Invalid {
        String[] names = keys.split(" ");
        require(object.entrySet().size() == names.length);
        for (String name : names) require(object.has(name));
    }

    private static JsonObject object(JsonElement value) throws Invalid {
        require(value != null && value.isJsonObject());
        return value.getAsJsonObject();
    }

    private static JsonArray array(JsonElement value, int limit) throws Invalid {
        require(value != null && value.isJsonArray());
        JsonArray result = value.getAsJsonArray();
        if (result.size() > limit) throw new Invalid(Failure.TOO_LARGE);
        return result;
    }

    private static boolean bool(JsonElement value) throws Invalid {
        require(value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean());
        return value.getAsBoolean();
    }

    private static String string(JsonElement value) throws Invalid {
        require(value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString());
        return value.getAsString();
    }

    private static BigDecimal number(JsonElement value) throws Invalid {
        require(value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber());
        return value.getAsBigDecimal();
    }

    private static long integer(JsonElement value, long min, long max) throws Invalid {
        BigDecimal n = number(value);
        require(n.compareTo(BigDecimal.valueOf(min)) >= 0 && n.compareTo(BigDecimal.valueOf(max)) <= 0);
        try { return n.longValueExact(); }
        catch (ArithmeticException ex) { throw new Invalid(Failure.INVALID); }
    }

    private static void require(boolean condition) throws Invalid {
        if (!condition) throw new Invalid(Failure.INVALID);
    }

    /** Strict, bounded, duplicate-key rejecting parser; does not log the input. */
    private static JsonElement parse(String text) throws Invalid {
        if (text.length() > MAX_BYTES) throw new Invalid(Failure.TOO_LARGE);
        try (JsonReader reader = new JsonReader(new StringReader(text))) {
            reader.setLenient(false);
            JsonElement node = parseNode(reader, 0);
            require(reader.peek() == JsonToken.END_DOCUMENT);
            return node;
        } catch (Invalid ex) {
            throw ex;
        } catch (IOException | IllegalArgumentException | IllegalStateException ex) {
            throw new Invalid(Failure.INVALID);
        }
    }

    private static JsonElement parseNode(JsonReader reader, int depth) throws IOException {
        if (depth > MAX_DEPTH) throw new Invalid(Failure.TOO_LARGE);
        switch (reader.peek()) {
            case BEGIN_OBJECT:
                reader.beginObject();
                JsonObject object = new JsonObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    require(name.length() <= 64 && !object.has(name));
                    if (object.entrySet().size() >= 40) throw new Invalid(Failure.TOO_LARGE);
                    object.add(name, parseNode(reader, depth + 1));
                }
                reader.endObject();
                return object;
            case BEGIN_ARRAY:
                reader.beginArray();
                JsonArray list = new JsonArray();
                while (reader.hasNext()) {
                    if (list.size() >= MAX_ARRAY) throw new Invalid(Failure.TOO_LARGE);
                    list.add(parseNode(reader, depth + 1));
                }
                reader.endArray();
                return list;
            case BOOLEAN: return new JsonPrimitive(reader.nextBoolean());
            case NULL: reader.nextNull(); return JsonNull.INSTANCE;
            case STRING:
                String text = reader.nextString();
                if (text.length() > MAX_TEXT) throw new Invalid(Failure.TOO_LARGE);
                require(StandardCharsets.UTF_8.newEncoder().canEncode(text));
                return new JsonPrimitive(text);
            case NUMBER:
                String literal = reader.nextString();
                require(literal.length() <= 40);
                BigDecimal n = new BigDecimal(literal);
                require(n.scale() >= -18 && n.scale() <= 18);
                return new JsonPrimitive(n);
            default: throw new Invalid(Failure.INVALID);
        }
    }
}
