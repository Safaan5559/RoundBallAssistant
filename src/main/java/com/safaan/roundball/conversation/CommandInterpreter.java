package com.safaan.roundball.conversation;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Small deterministic interpreter used by both text and voice front ends. */
public final class CommandInterpreter {
    private static final Pattern AMOUNT = Pattern.compile("\\b(\\d+)\\b");

    public Intent interpret(String raw) {
        if (raw == null || raw.isBlank()) return Intent.unknown();
        String s = raw.toLowerCase(Locale.ROOT).trim();
        if (s.matches("(hi|hello|hey|hello ball).*")) return new Intent(Intent.Type.GREETING, "", 0);
        if (s.contains("help") || s.contains("what can you do")) return new Intent(Intent.Type.HELP, "", 0);
        if (s.matches(".*\\b(stop|cancel|wait)\\b.*")) return new Intent(Intent.Type.STOP, "", 0);
        if (s.contains("follow me") || s.contains("come with me")) return new Intent(Intent.Type.FOLLOW, "player", 0);
        if (s.matches(".*\\b(find|locate|search for)\\b.*")) return new Intent(Intent.Type.FIND, extractAfter(s, "find", "locate", "search for"), 0);
        if (s.matches(".*\\b(attack|hit|fight)\\b.*")) return new Intent(Intent.Type.ATTACK, extractAfter(s, "attack", "hit", "fight"), 0);
        if (s.matches(".*\\b(give|get|bring|spawn|make)\\b.*")) {
            return new Intent(Intent.Type.GIVE_ITEM, extractItem(s), amount(s));
        }
        return Intent.unknown();
    }

    private int amount(String s) {
        Matcher m = AMOUNT.matcher(s);
        return m.find() ? Math.max(1, Math.min(64, Integer.parseInt(m.group(1)))) : 1;
    }

    private String extractItem(String s) {
        String[] words = s.split("\\s+");
        for (String word : words) {
            String w = word.replaceAll("[^a-z0-9_]", "");
            if (w.endsWith("s") && w.length() > 3) w = w.substring(0, w.length() - 1);
            if (w.equals("diamond") || w.equals("iron_ingot") || w.equals("gold_ingot") || w.equals("bread") || w.equals("torch") || w.equals("apple")) return w;
        }
        return "";
    }

    private String extractAfter(String s, String... triggers) {
        for (String t : triggers) {
            int i = s.indexOf(t);
            if (i >= 0) return s.substring(i + t.length()).trim();
        }
        return "";
    }
}
