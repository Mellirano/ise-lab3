package ua.udunt.ise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler;


public class ProbabilisticAnalyzer {

    private final LinkedList<String> linkedList = new LinkedList<>();
    private final Queue<String> queue = new LinkedList<>();
    private final Stack<String> stack = new Stack<>();
    private final Map<DataStructure, OperationStats> stats = new EnumMap<>(DataStructure.class);

    public ProbabilisticAnalyzer() {
        for (DataStructure ds : DataStructure.values()) {
            stats.put(ds, new OperationStats());
        }
    }

    private long measureTime(Runnable operation) {
        long start = System.nanoTime();
        operation.run();
        return System.nanoTime() - start;
    }

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

    public void extractLexemes(String code) {
        extractLexemes(code, null);
    }

    public void extractLexemes(String code, LexemeType lexemeType) {
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
        System.out.println("\nExtracted lexemes in each data structure:");
        System.out.println("LinkedList: " + linkedList);
        System.out.println("Queue: " + queue);
        System.out.println("Stack: " + stack);
    }

    public void analyzePerformance() {
        System.out.println("\nProbabilistic-combinatorial analysis of each function:");
        for (DataStructure ds : DataStructure.values()) {
            OperationStats stat = stats.get(ds);
            printPerformance(ds, "Additions", stat.addOps, stat.addTime);
            printPerformance(ds, "Searches", stat.searchOps, stat.searchTime);
            printPerformance(ds, "Removals", stat.removeOps, stat.removeTime);
        }
        visualizePerformance();
    }

    private void printPerformance(DataStructure ds, String operation, int ops, long time) {
        double avgTime = ops == 0 ? 0 : time / (ops * 1000.0);
        System.out.printf("%s -> %s: %d operations, Average time: %.2f microseconds\n", ds, operation, ops, avgTime);
    }

    private void visualizePerformance() {
        List<String> labels = Arrays.asList("LinkedList", "Queue", "Stack");
        List<Double> addTimes = new ArrayList<>();
        List<Double> searchTimes = new ArrayList<>();
        List<Double> removeTimes = new ArrayList<>();

        for (DataStructure ds : DataStructure.values()) {
            OperationStats stat = stats.get(ds);
            addTimes.add(stat.addOps == 0 ? 0 : stat.addTime / (double) stat.addOps);
            searchTimes.add(stat.searchOps == 0 ? 0 : stat.searchTime / (double) stat.searchOps);
            removeTimes.add(stat.removeOps == 0 ? 0 : stat.removeTime / (double) stat.removeOps);
        }

        CategoryChart chart = new CategoryChartBuilder()
                .width(800)
                .height(600)
                .title("Data Structure Performance")
                .xAxisTitle("Operations")
                .yAxisTitle("Time (nanoseconds)")
                .theme(Styler.ChartTheme.GGPlot2)
                .build();

        chart.addSeries("Addition", labels, addTimes);
        chart.addSeries("Search", labels, searchTimes);
        chart.addSeries("Removal", labels, removeTimes);

        new SwingWrapper<>(chart).displayChart();
    }

    public enum DataStructure {

        LINKED_LIST,
        QUEUE,
        STACK

    }

    private static class OperationStats {

        long addTime = 0;
        long searchTime = 0;
        long removeTime = 0;
        int addOps = 0;
        int searchOps = 0;
        int removeOps = 0;

    }

}
