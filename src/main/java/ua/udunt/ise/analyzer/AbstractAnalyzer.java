package ua.udunt.ise.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler;
import ua.udunt.ise.lexeme.LexemeType;

/**
 * The {@code AbstractAnalyzer} class defines an abstract structure for performing lexeme analysis.
 * It provides methods for analyzing lexemes, adding, searching, and removing them from a data structure.
 */
public abstract class AbstractAnalyzer {

    Map<DataStructure, OperationStats> stats = new EnumMap<>(DataStructure.class);

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
    long measureTime(Runnable operation) {
        long start = System.nanoTime();
        operation.run();
        return System.nanoTime() - start;
    }

    /**
     * Adds a lexeme to the specified data structure.
     *
     * @param lexeme   the lexeme to add
     * @param structure the data structure to store the lexeme
     * @param type      the type of data structure
     */
    void addToStructure(String lexeme, Collection<String> structure, DataStructure type) {
        OperationStats stat = stats.get(type);
        if (!structure.contains(lexeme)) {
            long addTime = measureTime(() -> structure.add(lexeme));
            stat.addTime += addTime;
            stat.addOps++;
            System.out.printf("Adding time for lexeme (%s) in structure (%s): %d ns%n\n",
                    lexeme, type.toString(), addTime);
            ;
        } else {
            System.out.printf("Lexeme (%s) already exists\n", lexeme);
        }
    }

    /**
     * Removes a lexeme from the specified data structure.
     *
     * @param lexeme   the lexeme to remove
     * @param structure the data structure to remove the lexeme from
     * @param type      the type of data structure
     */
    void removeFromStructure(String lexeme, Collection<String> structure, DataStructure type) {
        OperationStats stat = stats.get(type);
        if (structure.contains(lexeme)) {
            long removeTime = measureTime(() -> structure.remove(lexeme));
            stat.removeTime += removeTime;
            stat.removeOps++;
            System.out.printf("Removing time for lexeme (%s) in structure (%s): %d ns%n\n",
                    lexeme, type.toString(), removeTime);
        } else {
            System.out.printf("Lexeme (%s) does not exist\n", lexeme);
        }
    }

    /**
     * Searches for a lexeme in the specified data structure.
     *
     * @param lexeme   the lexeme to search for
     * @param structure the data structure to search in
     * @param type      the type of data structure
     */
    void searchInStructure(String lexeme, Collection<String> structure, DataStructure type) {
        OperationStats stat = stats.get(type);
        long searchTime = measureTime(() -> structure.contains(lexeme));
        stat.searchTime += searchTime;
        stat.searchOps++;
        System.out.printf("Searching time for lexeme (%s) in structure (%s): %d ns%n\n",
                lexeme, type.toString(), searchTime);
    }

    /**
     * Prints the performance analysis results.
     *
     * @param ds       the data structure type
     * @param operation the operation performed
     * @param ops      the number of operations performed
     * @param time     the total execution time
     */
    void printPerformance(DataStructure ds, String operation, int ops, long time) {
        double avgTime = ops == 0 ? 0 : (double) time / ops;
        System.out.printf("%s -> %s: %d operations, Average time: %.2f ns\n", ds, operation, ops, avgTime);
    }

    /**
     * Visualizes the performance results using a bar chart.
     *
     * @param supportedDataStructures  the data structures analyzed
     */
    void visualizePerformance(List<DataStructure> supportedDataStructures) {
        List<Double> addTimes = new ArrayList<>();
        List<Double> searchTimes = new ArrayList<>();
        List<Double> removeTimes = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (DataStructure ds : supportedDataStructures) {
            OperationStats stat = stats.get(ds);
            labels.add(ds.name());
            addTimes.add(stat.addOps == 0 ? 0 : stat.addTime / (double) (stat.addOps));
            searchTimes.add(stat.searchOps == 0 ? 0 : stat.searchTime / (double) (stat.searchOps));
            removeTimes.add(stat.removeOps == 0 ? 0 : stat.removeTime / (double) (stat.removeOps));
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

    /**
     * Enum representing different data structures used in the analysis.
     */
    enum DataStructure {

        LINKED_LIST,
        DEQUE,
        QUEUE,
        STACK

    }

    /**
     * Stores operation statistics for each data structure.
     */
    static class OperationStats {

        long addTime = 0;
        long searchTime = 0;
        long removeTime = 0;
        int addOps = 0;
        int searchOps = 0;
        int removeOps = 0;

    }

}
