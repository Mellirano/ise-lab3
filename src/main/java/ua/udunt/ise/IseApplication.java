package ua.udunt.ise;

import java.util.List;
import java.util.Random;
import ua.udunt.ise.analyzer.ProbabilisticAnalyzer;
import ua.udunt.ise.analyzer.TimingAnalyzer;

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

    private static final List<String> SAMPLE_LEXEMES = List.of(
            "int", "main", "if", "System", "comment"
    );

    public static void main(String[] args) {
        //TODO Add command line control
        runTimingAnalysis();
    }

    private static void runTimingAnalysis() {
        TimingAnalyzer analyzer = new TimingAnalyzer();
        System.out.println("\nPerforming timing analysis...");
        analyzer.analyzePerformance(SAMPLE_CODE, () -> {
            Random random = new Random();

            String newLexeme = SAMPLE_LEXEMES.get(random.nextInt(SAMPLE_LEXEMES.size()));
            System.out.println("Adding lexeme: " + newLexeme);
            analyzer.addLexeme(newLexeme);

            int removeIndex = random.nextInt(analyzer.getRandomLexemeCount());
            String removeLexeme = analyzer.searchLexemeByIndex(removeIndex);
            if (removeLexeme != null) {
                System.out.println("Removing lexeme: " + removeLexeme);
                analyzer.removeLexeme(removeLexeme);
            }
            int searchIndex = random.nextInt(analyzer.getRandomLexemeCount());
            String searchLexeme = analyzer.searchLexemeByIndex(searchIndex);
            if (searchLexeme != null) {
                System.out.println("Searching lexeme: " + searchLexeme);
                analyzer.searchLexeme(searchLexeme);
            }
        });
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
