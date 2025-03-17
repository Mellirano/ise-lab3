package ua.udunt.ise.lexeme;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LexemeGenerator {

    private static final String[] KEYWORDS = {
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue",
            "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto",
            "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package",
            "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"
    };

    private static final String[] OPERATORS = {
            "+", "-", "*", "/", "%", "++", "--", "==", "!=", "<", ">", "<=", ">=", "&&", "||", "!",
            "&", "|", "^", "~", "<<", ">>", ">>>", "=", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "<<=", ">>=", ">>>="
    };

    private static final String[] DELIMITERS = {";", ",", "(", ")", "{", "}", "[", "]"};

    private static final Random RANDOM = new Random();

    /**
     * Generates a list of random Java lexemes.
     *
     * @param count the number of lexemes to generate
     * @return a list of randomly generated Java lexemes
     */
    public static List<String> generateLexemes(int count) {
        List<String> lexemes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            lexemes.add(generateRandomLexeme());
        }
        return lexemes;
    }

    /**
     * Generates a single random Java lexeme.
     *
     * @return a randomly generated Java lexeme (keyword, operator, or delimiter)
     */
    public static String generateRandomLexeme() {
        int category = RANDOM.nextInt(3);
        return switch (category) {
            case 0 -> KEYWORDS[RANDOM.nextInt(KEYWORDS.length)];
            case 1 -> OPERATORS[RANDOM.nextInt(OPERATORS.length)];
            case 2 -> DELIMITERS[RANDOM.nextInt(DELIMITERS.length)];
            default -> "";
        };
    }

}
