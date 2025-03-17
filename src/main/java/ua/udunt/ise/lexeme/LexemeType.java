package ua.udunt.ise.lexeme;

import java.util.regex.Pattern;

/**
 * The {@code LexemeType} enum represents different types of lexemes that can be identified
 * in source code. Each lexeme type is associated with a regular expression pattern
 * used for lexical analysis.
 */
public enum LexemeType {

    /**
     * Represents keywords in the programming language.
     * Matches predefined reserved words such as {@code if}, {@code else}, {@code for}, etc.
     */
    KEYWORD("\\b(abstract|continue|for|new|switch|assert|default|if|package|synchronized|boolean|do|goto|private|this|break|double|implements|protected|throw|byte|else|import|public|throws|case|enum|instanceof|return|transient|catch|extends|int|short|try|char|final|interface|static|void|class|finally|long|strictfp|volatile|const|float|native|super|while)\\b"),
    /**
     * Represents identifiers, which are variable names, method names, and other user-defined names.
     * Matches sequences that start with a letter or underscore, followed by letters, digits, or underscores.
     */
    IDENTIFIER("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b"),
    /**
     * Represents operators such as arithmetic, logical, and relational operators.
     * Matches symbols like {@code +, -, *, /, =, <, >, !, &, |}.
     */
    OPERATOR("[+\\-*/%=<>!&|]+"),
    /**
     * Represents delimiters used in the syntax of a programming language.
     * Matches symbols such as braces, parentheses, semicolons, and commas.
     */
    DELIMITER("[{}();,]"),
    /**
     * Represents literals, including string literals and numeric values.
     * Matches quoted string literals and numeric values.
     */
    LITERAL("\".*?\"|\\d+"),
    /**
     * Represents comments in the code.
     * Matches single-line comments ({@code //}) and multi-line comments ({@code /* ... *\/}).
     */
    COMMENT("//.*|/\\*.*?\\*/");

    private final Pattern pattern;

    /**
     * Constructs a {@code LexemeType} with a specified regular expression pattern.
     *
     * @param regex the regular expression pattern corresponding to the lexeme type
     */
    LexemeType(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    /**
     * Gets the compiled regular expression pattern associated with the lexeme type.
     *
     * @return the regex {@code Pattern} object for the lexeme type
     */
    public Pattern getPattern() {
        return pattern;
    }

}
