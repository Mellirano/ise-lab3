package ua.udunt.ise;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import ua.udunt.ise.analyzer.ProbabilisticAnalyzer;
import ua.udunt.ise.analyzer.TimingAnalyzer;
import ua.udunt.ise.lexeme.LexemeGenerator;

/**
 * The {@code IseApplication} class serves as the main entry point for executing different types of lexeme analysis.
 * It provides command-line control to select between timing analysis and probabilistic analysis.
 */
public class IseApplication {

    private static final String SAMPLE_CODE;

    static {
        SAMPLE_CODE = readSampleCode();
    }

    /**
     * The main method that processes command-line arguments and initiates the selected analysis.
     *
     * @param args command-line arguments to specify which analysis to run ("timing" or "probabilistic").
     *             If no argument is provided, timing analysis is executed by default.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No argument provided. Running default timing analysis...");
            runTimingAnalysis();
        } else {
            switch (args[0].toLowerCase()) {
                case "timing" -> {
                    System.out.println("Running timing analysis...");
                    runTimingAnalysis();
                }
                case "probabilistic" -> {
                    System.out.println("Running probabilistic analysis...");
                    runProbabilisticAnalysis();
                }
                default -> {
                    System.out.println("Invalid argument. Use 'timing' or 'probabilistic'. Running default timing analysis...");
                    runTimingAnalysis();
                }
            }
        }
    }

    /**
     * Reads the sample Java code from an external file.
     *
     * @return a string containing the Java code.
     */
    private static String readSampleCode() {
        try {
            return Files.readString(Path.of("src/main/resources/sample_code.txt"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sample code file", e);
        }
    }

    /**
     * Executes the timing analysis, measuring the performance of lexeme operations such as addition,
     * searching, and removal.
     */
    private static void runTimingAnalysis() {
        TimingAnalyzer analyzer = new TimingAnalyzer();
        System.out.println("\nPerforming timing analysis...");
        analyzer.analyzePerformance(SAMPLE_CODE, () -> {
            Random random = new Random();

            String newLexeme = LexemeGenerator.generateRandomLexeme();
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

    /**
     * Executes the probabilistic analysis, performing operations on randomly generated lexemes.
     */
    private static void runProbabilisticAnalysis() {
        try {
            ProbabilisticAnalyzer analyzer = new ProbabilisticAnalyzer();
            System.out.println("\nPerforming performance analysis...");
            analyzer.analyzePerformance(SAMPLE_CODE, () -> {
                for (String lexeme : LexemeGenerator.generateLexemes(15)) {
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
