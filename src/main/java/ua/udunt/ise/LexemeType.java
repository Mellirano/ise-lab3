package ua.udunt.ise;

import java.util.regex.Pattern;

public enum LexemeType {

    KEYWORD("\\b(abstract|continue|for|new|switch|assert|default|if|package|synchronized|boolean|do|goto|private|this|break|double|implements|protected|throw|byte|else|import|public|throws|case|enum|instanceof|return|transient|catch|extends|int|short|try|char|final|interface|static|void|class|finally|long|strictfp|volatile|const|float|native|super|while)\\b"),
    IDENTIFIER("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b"),
    OPERATOR("[+\\-*/%=<>!&|]+"),
    DELIMITER("[{}();,]"),
    LITERAL("\".*?\"|\\d+"),
    COMMENT("//.*|/\\*.*?\\*/");

    private final Pattern pattern;

    LexemeType(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public Pattern getPattern() {
        return pattern;
    }

}
