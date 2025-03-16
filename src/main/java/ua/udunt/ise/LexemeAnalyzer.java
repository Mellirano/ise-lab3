package ua.udunt.ise;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

public class LexemeAnalyzer {

    public static Map<LexemeType, Set<String>> analyzeCode(String code) {
        return analyzeCode(code, null);
    }

    public static Map<LexemeType, Set<String>> analyzeCode(String code, LexemeType lexemeType) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Code cannot be null or empty");
        }
        Map<LexemeType, Set<String>> categorizedLexemes = new LinkedHashMap<>();
        if (lexemeType == null) {
            for (LexemeType type : LexemeType.values()) {
                categorizedLexemes.put(type, extractLexemes(code, type));
            }
        } else {
            categorizedLexemes.put(lexemeType, extractLexemes(code, lexemeType));
        }
        return categorizedLexemes;
    }

    private static Set<String> extractLexemes(String code, LexemeType lexemeType) {
        Set<String> lexemes = new LinkedHashSet<>();
        Matcher matcher = lexemeType.getPattern().matcher(code);
        while (matcher.find()) {
            lexemes.add(matcher.group());
        }
        return lexemes;
    }

}
