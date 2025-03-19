package ua.udunt.ise.analyzer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import ua.udunt.ise.lexeme.LexemeAnalyzer;
import ua.udunt.ise.lexeme.LexemeType;

/**
 * The {@code TimingAnalyzer} class extends {@code AbstractAnalyzer} to perform timing analysis
 * on lexeme operations using a Deque and LinkedList data structure. It measures the performance of
 * lexeme addition, searching, and removal.
 */
public class TimingAnalyzer extends AbstractAnalyzer {

    private final Deque<String> deque = new ArrayDeque<>();
    private final List<String> linkedList = new LinkedList<>();
    private final Random random = new Random();
    private final List<DataStructure> supportedDataStructures = Arrays.asList(
            DataStructure.DEQUE,
            DataStructure.LINKED_LIST
    );

    /**
     * Initializes the timing analyzer and sets up operation statistics for each data structure.
     */
    public TimingAnalyzer() {
        for (DataStructure ds : supportedDataStructures) {
            stats.put(ds, new OperationStats());
        }
    }

    /**
     * Analyzes the performance of lexeme operations and executes a specified operation before analysis.
     *
     * @param code       the source code to analyze
     * @param operation  a runnable operation to execute before analysis
     * @param lexemeType the specific lexeme type to analyze (or null for all types)
     */
    @Override
    public void analyzePerformance(String code, Runnable operation, LexemeType lexemeType) {
        extractLexemes(code, lexemeType);
        boolean hasLexemes = !deque.isEmpty() || !linkedList.isEmpty();
        if (!hasLexemes) {
            throw new IllegalArgumentException("No lexemes available for performance analysis");
        }
        System.out.println("\nExtracted lexemes in each data structure:");
        System.out.println("Deque: " + deque);
        System.out.println("LinkedList: " + linkedList);

        operation.run();

        System.out.println("\nTiming analysis of each function:");
        for (DataStructure ds : supportedDataStructures) {
            OperationStats stat = stats.get(ds);
            printPerformance(ds, "Additions", stat.addOps, stat.addTime);
            printPerformance(ds, "Searches", stat.searchOps, stat.searchTime);
            printPerformance(ds, "Removals", stat.removeOps, stat.removeTime);
        }
        visualizePerformance(supportedDataStructures);
    }

    /**
     * Analyzes lexeme performance with a default lexeme type (null).
     *
     * @param code      the source code to analyze
     * @param operation a runnable operation to execute before analysis
     */
    public void analyzePerformance(String code, Runnable operation) {
        analyzePerformance(code, operation, null);
    }

    /**
     * Extracts lexemes from the given code based on the specified lexeme type.
     *
     * @param code       the source code to analyze
     * @param lexemeType the specific lexeme type to extract (or null for all types)
     */
    private void extractLexemes(String code, LexemeType lexemeType) {
        Map<LexemeType, Set<String>> lexemesByType;
        if (lexemeType != null) {
            lexemesByType = LexemeAnalyzer.analyzeCode(code, lexemeType);
        } else {
            lexemesByType = LexemeAnalyzer.analyzeCode(code);
        }

        List<String> allLexemes = new ArrayList<>();
        for (Set<String> lexemes : lexemesByType.values()) {
            allLexemes.addAll(lexemes);
        }

        int lexemeCount = allLexemes.size();
        int randomLexemeCount = random.nextInt(lexemeCount) + 1;
        Collections.shuffle(allLexemes);

        for (int i = 0; i < randomLexemeCount; i++) {
            addLexeme(allLexemes.get(i));
        }
    }

    /**
     * Adds a lexeme to both deque and linked list data structures.
     *
     * @param lexeme the lexeme to add
     */
    @Override
    public void addLexeme(String lexeme) {
        addToStructure(lexeme, deque, DataStructure.DEQUE);
        addToStructure(lexeme, linkedList, DataStructure.LINKED_LIST);
    }

    /**
     * Removes a lexeme from both deque and linked list data structures.
     *
     * @param lexeme the lexeme to remove
     */
    @Override
    public void removeLexeme(String lexeme) {
        removeFromStructure(lexeme, deque, DataStructure.DEQUE);
        removeFromStructure(lexeme, linkedList, DataStructure.LINKED_LIST);
    }

    /**
     * Searches for a lexeme in both deque and linked list data structures.
     *
     * @param lexeme the lexeme to search for
     */
    @Override
    public void searchLexeme(String lexeme) {
        searchInStructure(lexeme, deque, DataStructure.DEQUE);
        searchInStructure(lexeme, linkedList, DataStructure.LINKED_LIST);
    }

    /**
     * Retrieves a random lexeme count from the deque.
     *
     * @return a random number of lexemes in the deque
     */
    public int getRandomLexemeCount() {
        return random.nextInt(deque.size()) + 1;
    }

    /**
     * Retrieves a lexeme from the deque based on an index.
     *
     * @param index the index to search for
     * @return the lexeme found at the given index, or null if not found
     */
    public String searchLexemeByIndex(int index) {
        return deque.stream()
                .skip(index)
                .findFirst()
                .orElse(null);
    }

}
