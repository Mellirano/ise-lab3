package ua.udunt.ise;

import java.util.Set;

public class IseApplication {

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

    private static final Set<String> SAMPLE_LEXEMES = Set.of(
            "int", "main", "if", "System", "comment"
    );

    public static void main(String[] args) {
        runProbabilisticAnalysis();
    }

    private static void runProbabilisticAnalysis() {
        try {
            ProbabilisticAnalyzer analyzer = new ProbabilisticAnalyzer();
            System.out.println("\nPerforming performance analysis...");
            analyzer.analyzePerformance(SAMPLE_CODE, () -> {
                for (String lexeme : SAMPLE_LEXEMES) {
                    System.out.println("Searching lexeme: " + lexeme);
                    analyzer.searchLexeme(lexeme);

                    System.out.println("Removing lexeme: " + lexeme);
                    analyzer.removeLexeme(lexeme);
                }
            });

        } catch (Exception e) {
            System.err.println("An error occurred during execution: " + e.getMessage());
        }
    }

}
