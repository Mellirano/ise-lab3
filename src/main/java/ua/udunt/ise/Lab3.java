package ua.udunt.ise;

import java.util.Arrays;

public class Lab3 {

    private static final String SAMPLE_CODE = """
            public class Test {
                public static void main(String[] args) {
                    int x = 10;
                    if (x > 5) {
                        System.out.println(\"Hello World!\");
                    }
                    // This is a comment
                }
            }
        """;

    public static void main(String[] args) {
        ProbabilisticAnalyzer analyzer = new ProbabilisticAnalyzer();
        analyzer.extractLexemes(SAMPLE_CODE);
        for (String lexeme : Arrays.asList("int", "main", "if", "System", "comment")) {
            analyzer.searchLexeme(lexeme);
            analyzer.removeLexeme(lexeme);
        }
        analyzer.analyzePerformance();
    }

}
