package ua.udunt.ise.analyzer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler;
import ua.udunt.ise.LexemeType;

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

    void printPerformance(DataStructure ds, String operation, int ops, long time) {
        double avgTime = ops == 0 ? 0 : time / (ops * 1000.0);
        System.out.printf("%s -> %s: %d operations, Average time: %.2f microseconds\n", ds, operation, ops, avgTime);
    }

    /**
     * Visualizes the performance results using a bar chart.
     */
    void visualizePerformance(List<String> labels, List<DataStructure> supportedDataStructures) {
        List<Double> addTimes = new ArrayList<>();
        List<Double> searchTimes = new ArrayList<>();
        List<Double> removeTimes = new ArrayList<>();

        for (DataStructure ds : supportedDataStructures) {
            OperationStats stat = stats.get(ds);
            addTimes.add(stat.addOps == 0 ? 0 : stat.addTime / (double) (stat.addOps * 1000));
            searchTimes.add(stat.searchOps == 0 ? 0 : stat.searchTime / (double) (stat.searchOps * 1000));
            removeTimes.add(stat.removeOps == 0 ? 0 : stat.removeTime / (double) (stat.removeOps * 1000));
        }

        CategoryChart chart = new CategoryChartBuilder()
                .width(800)
                .height(600)
                .title("Data Structure Performance")
                .xAxisTitle("Operations")
                .yAxisTitle("Time (microseconds)")
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
