package ua.udunt.ise;

/**
 * The {@code AbstractAnalyzer} class defines an abstract structure for performing lexeme analysis.
 * It provides methods for analyzing lexemes, adding, searching, and removing them from a data structure.
 */
public abstract class AbstractAnalyzer {

    /**
     * Analyzes the performance of lexeme operations.
     *
     * @param code       the source code to analyze
     * @param operation  a runnable operation to execute before analysis
     * @param lexemeType the specific lexeme type to analyze (or null for all types)
     */
    public abstract void analyzePerformance(String code, Runnable operation, LexemeType lexemeType);

    /**
     * Adds a lexeme to the data structure.
     *
     * @param lexeme the lexeme to add
     */
    public abstract void addLexeme(String lexeme);

    /**
     * Removes a lexeme from the data structure.
     *
     * @param lexeme the lexeme to remove
     */
    public abstract void removeLexeme(String lexeme);

    /**
     * Searches for a lexeme in the data structure.
     *
     * @param lexeme the lexeme to search for
     */
    public abstract void searchLexeme(String lexeme);

    /**
     * Measures the execution time of a given operation.
     *
     * @param operation the operation to measure
     * @return execution time in nanoseconds
     */
    protected long measureTime(Runnable operation) {
        long start = System.nanoTime();
        operation.run();
        return System.nanoTime() - start;
    }

}
