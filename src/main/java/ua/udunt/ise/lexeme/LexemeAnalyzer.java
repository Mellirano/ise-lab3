package ua.udunt.ise.lexeme;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * The {@code LexemeAnalyzer} class is responsible for analyzing a given source code
 * and categorizing lexemes based on their types such as keywords, identifiers, operators, etc.
 * It supports extracting lexemes of a specific type or all types at once.
 */
public class LexemeAnalyzer {

    /**
     * Analyzes the provided source code and categorizes all lexemes found in it.
     *
     * @param code the source code to analyze
     * @return a map where keys are {@code LexemeType} and values are sets of corresponding lexemes
     * @throws IllegalArgumentException if the provided code is null or empty
     */
    public static Map<LexemeType, Set<String>> analyzeCode(String code) {
        return analyzeCode(code, null);
    }

    /**
     * Analyzes the provided source code and extracts lexemes of a specified type.
     * If {@code lexemeType} is {@code null}, all lexeme types are extracted.
     *
     * @param code       the source code to analyze
     * @param lexemeType the specific type of lexeme to extract, or {@code null} to extract all types
     * @return a map where keys are {@code LexemeType} and values are sets of corresponding lexemes
     * @throws IllegalArgumentException if the provided code is null or empty
     */
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

    /**
     * Extracts lexemes of the specified type from the given source code.
     *
     * @param code       the source code to analyze
     * @param lexemeType the type of lexeme to extract
     * @return a set of lexemes of the specified type
     */
    private static Set<String> extractLexemes(String code, LexemeType lexemeType) {
        Set<String> lexemes = new LinkedHashSet<>();
        Matcher matcher = lexemeType.getPattern().matcher(code);
        while (matcher.find()) {
            lexemes.add(matcher.group());
        }
        return lexemes;
    }

}
