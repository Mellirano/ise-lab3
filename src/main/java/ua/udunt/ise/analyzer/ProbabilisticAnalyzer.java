package ua.udunt.ise.analyzer;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import ua.udunt.ise.LexemeAnalyzer;
import ua.udunt.ise.LexemeType;

public class ProbabilisticAnalyzer extends AbstractAnalyzer {

    private final LinkedList<String> linkedList = new LinkedList<>();
    private final Queue<String> queue = new LinkedList<>();
    private final Stack<String> stack = new Stack<>();
    private final List<String> chartLabels = Arrays.asList(
            "LinkedList", "Queue", "Stack"
    );

    public ProbabilisticAnalyzer() {
        for (DataStructure ds : DataStructure.values()) {
            stats.put(ds, new OperationStats());
        }
    }

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
        visualizePerformance(chartLabels, List.of(DataStructure.values()));
    }

    public void analyzePerformance(String code, Runnable operation) {
        analyzePerformance(code, operation, null);
    }

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

    @Override
    public void addLexeme(String lexeme) {
        addToStructure(lexeme, linkedList, DataStructure.LINKED_LIST);
        addToStructure(lexeme, queue, DataStructure.QUEUE);
        addToStructure(lexeme, stack, DataStructure.STACK);
    }

    private void addToStructure(String lexeme, Collection<String> structure, DataStructure type) {
        OperationStats stat = stats.get(type);
        if (!structure.contains(lexeme)) {
            stat.addTime += measureTime(() -> structure.add(lexeme));
            stat.addOps++;
        }
    }

    @Override
    public void removeLexeme(String lexeme) {
        removeFromStructure(lexeme, linkedList, DataStructure.LINKED_LIST);
        removeFromStructure(lexeme, queue, DataStructure.QUEUE);
        removeFromStructure(lexeme, stack, DataStructure.STACK);
    }

    private void removeFromStructure(String lexeme, Collection<String> structure, DataStructure type) {
        OperationStats stat = stats.get(type);
        if (structure.contains(lexeme)) {
            stat.removeTime += measureTime(() -> structure.remove(lexeme));
            stat.removeOps++;
        }
    }

    @Override
    public void searchLexeme(String lexeme) {
        searchInStructure(lexeme, linkedList, DataStructure.LINKED_LIST);
        searchInStructure(lexeme, queue, DataStructure.QUEUE);
        searchInStructure(lexeme, stack, DataStructure.STACK);
    }

    private void searchInStructure(String lexeme, Collection<String> structure, DataStructure type) {
        OperationStats stat = stats.get(type);
        stat.searchTime += measureTime(() -> structure.contains(lexeme));
        stat.searchOps++;
    }

}
