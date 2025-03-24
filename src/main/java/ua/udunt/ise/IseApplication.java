package ua.udunt.ise;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import ua.udunt.ise.analyzer.TimingAnalyzer;
import ua.udunt.ise.lexeme.LexemeGenerator;

/**
 * The {@code IseApplication} class serves as the entry point for executing
 * the timing analysis of lexeme operations on various data structures.
 * <p>
 * It loads a sample code file, performs a series of lexeme operations (add, search, remove),
 * and delegates performance evaluation to the {@link TimingAnalyzer}.
 */
@Slf4j
public class IseApplication {

    /**
     * The sample source code loaded from file.
     */
    private static final String SAMPLE_CODE;

    static {
        SAMPLE_CODE = readSampleCode();
    }

    /**
     * The main method. Executes performance analysis either with a default repeat count or
     * with a user-supplied one.
     *
     * @param args Optional command-line argument to specify the number of operation repetitions
     */
    public static void main(String[] args) {
        int repeatCount = 10;
        if (args.length == 0) {
            log.info("No argument provided. Running default timing analysis...");
            runTimingAnalysis(repeatCount);
        } else {
            if (args[0] != null) {
                repeatCount = Integer.parseInt(args[0]);
            }
            runTimingAnalysis(repeatCount);
        }
    }

    /**
     * Reads the sample code from a file in the resources' directory.
     *
     * @return The source code as a String
     * @throws RuntimeException if reading the file fails
     */
    private static String readSampleCode() {
        try {
            return Files.readString(Path.of("src/main/resources/sample_code.txt"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sample code file", e);
        }
    }

    /**
     * Executes the timing analysis using {@link TimingAnalyzer}.
     * <p>
     * Repeats lexeme operations (add, search, remove) for a specified number of iterations.
     *
     * @param repeatCount Number of times to repeat the lexeme operations
     */
    private static void runTimingAnalysis(int repeatCount) {
        TimingAnalyzer analyzer = new TimingAnalyzer();
        log.info("Performing timing analysis...");
        analyzer.analyzePerformance(SAMPLE_CODE, () -> {
            Random random = new Random();
            for (int i = 0; i < repeatCount; i++) {
                String newLexeme = LexemeGenerator.generateRandomLexeme();
                log.debug("Adding lexeme: {}", newLexeme);
                analyzer.addLexeme(newLexeme);

                int searchIndex = random.nextInt(analyzer.getRandomLexemeCount());
                String searchLexeme = analyzer.searchLexemeByIndex(searchIndex);
                if (searchLexeme != null) {
                    log.debug("Searching lexeme: {}", searchLexeme);
                    analyzer.searchLexeme(searchLexeme);
                }
                int removeIndex = random.nextInt(analyzer.getRandomLexemeCount());
                String removeLexeme = analyzer.searchLexemeByIndex(removeIndex);
                if (removeLexeme != null) {
                    log.debug("Removing lexeme: {}", removeLexeme);
                    analyzer.removeLexeme(removeLexeme);
                }
            }
        });
    }

}
