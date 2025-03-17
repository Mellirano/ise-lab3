package ua.udunt.ise.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import ua.udunt.ise.LexemeAnalyzer;
import ua.udunt.ise.LexemeType;

public class TimingAnalyzer extends AbstractAnalyzer {

    private final Queue<String> queue = new LinkedList<>();
    private final Stack<String> stack = new Stack<>();
    private final Random random = new Random();
    private final List<DataStructure> supportedDataStructures = Arrays.asList(
            DataStructure.QUEUE,
            DataStructure.STACK
    );
    private final List<String> chartLabels = Arrays.asList(
            "Queue", "Stack"
    );

    public TimingAnalyzer() {
        for (DataStructure ds : supportedDataStructures) {
            stats.put(ds, new OperationStats());
        }
    }

    @Override
    public void analyzePerformance(String code, Runnable operation, LexemeType lexemeType) {
        extractLexemes(code, lexemeType);
        boolean hasLexemes = !queue.isEmpty() || !stack.isEmpty();
        if (!hasLexemes) {
            throw new IllegalArgumentException("No lexemes available for performance analysis");
        }
        System.out.println("\nExtracted lexemes in each data structure:");
        System.out.println("Queue: " + queue);
        System.out.println("Stack: " + stack);

        operation.run();

        System.out.println("\nTiming analysis of each function:");
        for (DataStructure ds : supportedDataStructures) {
            OperationStats stat = stats.get(ds);
            printPerformance(ds, "Additions", stat.addOps, stat.addTime);
            printPerformance(ds, "Searches", stat.searchOps, stat.searchTime);
            printPerformance(ds, "Removals", stat.removeOps, stat.removeTime);
        }
        visualizePerformance(chartLabels, supportedDataStructures);
    }

    public void analyzePerformance(String code, Runnable operation) {
        analyzePerformance(code, operation, null);
    }

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

    @Override
    public void addLexeme(String lexeme) {
        addToStructure(lexeme, queue, DataStructure.QUEUE);
        addToStructure(lexeme, stack, DataStructure.STACK);
    }

    private void addToStructure(String lexeme, Collection<String> structure, DataStructure type) {
        OperationStats stat = stats.get(type);
        if (!structure.contains(lexeme)) {
            long addTime = measureTime(() -> structure.add(lexeme));
            stat.addTime += addTime;
            stat.addOps++;
            System.out.printf("Adding time for lexeme (%s) in structure (%s): %.2f microseconds\n",
                    lexeme, type.toString(), addTime / 1000.0);
            ;
        } else {
            System.out.printf("Lexeme (%s) already exists\n", lexeme);
        }
    }

    @Override
    public void removeLexeme(String lexeme) {
        removeFromStructure(lexeme, queue, DataStructure.QUEUE);
        removeFromStructure(lexeme, stack, DataStructure.STACK);
    }

    private void removeFromStructure(String lexeme, Collection<String> structure, DataStructure type) {
        OperationStats stat = stats.get(type);
        if (structure.contains(lexeme)) {
            long removeTime = measureTime(() -> structure.remove(lexeme));
            stat.removeTime += removeTime;
            stat.removeOps++;
            System.out.printf("Removing time for lexeme (%s) in structure (%s): %.2f microseconds\n",
                    lexeme, type.toString(), removeTime / 1000.0);
        } else {
            System.out.printf("Lexeme (%s) does not exist\n", lexeme);
        }
    }

    @Override
    public void searchLexeme(String lexeme) {
        searchInStructure(lexeme, queue, DataStructure.QUEUE);
        searchInStructure(lexeme, stack, DataStructure.STACK);
    }

    private void searchInStructure(String lexeme, Collection<String> structure, DataStructure type) {
        OperationStats stat = stats.get(type);
        long searchTime = measureTime(() -> structure.contains(lexeme));
        stat.searchTime += searchTime;
        stat.searchOps++;
        System.out.printf("Searching time for lexeme (%s) in structure (%s): %.2f microseconds\n",
                lexeme, type.toString(), searchTime / 1000.0);
    }

    public int getRandomLexemeCount() {
        return random.nextInt(queue.size()) + 1;
    }

    public String searchLexemeByIndex(int index) {
        return queue.stream()
                .skip(index)
                .findFirst()
                .orElse(null);
    }

}
