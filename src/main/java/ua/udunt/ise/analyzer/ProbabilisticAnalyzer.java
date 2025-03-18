package ua.udunt.ise.analyzer;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import ua.udunt.ise.lexeme.LexemeAnalyzer;
import ua.udunt.ise.lexeme.LexemeType;

/**
 * The {@code ProbabilisticAnalyzer} class extends {@code AbstractAnalyzer} to perform
 * probabilistic and combinatorial analysis of lexemes stored in different data structures.
 * It measures the performance of lexeme addition, searching, and removal.
 */
public class ProbabilisticAnalyzer extends AbstractAnalyzer {

    private final LinkedList<String> linkedList = new LinkedList<>();
    private final Queue<String> queue = new ArrayDeque<>();
    private final Stack<String> stack = new Stack<>();

    /**
     * Initializes the probabilistic analyzer and sets up operation statistics for each data structure.
     */
    public ProbabilisticAnalyzer() {
        for (DataStructure ds : DataStructure.values()) {
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
        boolean hasLexemes = !linkedList.isEmpty() || !queue.isEmpty() || !stack.isEmpty();
        if (!hasLexemes) {
            throw new IllegalArgumentException("No lexemes available for performance analysis");
        }
        System.out.println("\nExtracted lexemes in each data structure:");
        System.out.println("LinkedList: " + linkedList);
        System.out.println("Queue: " + queue);
        System.out.println("Stack: " + stack);

        System.out.println("\nPerform some operations before analysis:");
        operation.run();

        System.out.println("\nProbabilistic-combinatorial analysis of each function:");
        for (DataStructure ds : DataStructure.values()) {
            OperationStats stat = stats.get(ds);
            printPerformance(ds, "Additions", stat.addOps, stat.addTime);
            printPerformance(ds, "Searches", stat.searchOps, stat.searchTime);
            printPerformance(ds, "Removals", stat.removeOps, stat.removeTime);
        }
        visualizePerformance(List.of(DataStructure.values()));
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
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Code cannot be null or empty");
        }
        Map<LexemeType, Set<String>> lexemesByType;
        if (lexemeType != null) {
            lexemesByType = LexemeAnalyzer.analyzeCode(code, lexemeType);
        } else {
            lexemesByType = LexemeAnalyzer.analyzeCode(code);
        }
        for (LexemeType type : lexemesByType.keySet()) {
            for (String lexeme : lexemesByType.get(type)) {
                addLexeme(lexeme);
            }
        }
    }

    /**
     * Adds a lexeme to all data structures.
     *
     * @param lexeme the lexeme to add
     */
    @Override
    public void addLexeme(String lexeme) {
        addToStructure(lexeme, linkedList, DataStructure.LINKED_LIST);
        addToStructure(lexeme, queue, DataStructure.QUEUE);
        addToStructure(lexeme, stack, DataStructure.STACK);
    }

    /**
     * Removes a lexeme from all data structures.
     *
     * @param lexeme the lexeme to remove
     */
    @Override
    public void removeLexeme(String lexeme) {
        removeFromStructure(lexeme, linkedList, DataStructure.LINKED_LIST);
        removeFromStructure(lexeme, queue, DataStructure.QUEUE);
        removeFromStructure(lexeme, stack, DataStructure.STACK);
    }

    /**
     * Searches for a lexeme in all data structures.
     *
     * @param lexeme the lexeme to search for
     */
    @Override
    public void searchLexeme(String lexeme) {
        searchInStructure(lexeme, linkedList, DataStructure.LINKED_LIST);
        searchInStructure(lexeme, queue, DataStructure.QUEUE);
        searchInStructure(lexeme, stack, DataStructure.STACK);
    }

}
