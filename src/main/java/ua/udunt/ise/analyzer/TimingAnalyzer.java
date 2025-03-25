package ua.udunt.ise.analyzer;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.style.Styler;
import ua.udunt.ise.lexeme.LexemeAnalyzer;
import ua.udunt.ise.lexeme.LexemeType;

/**
 * The TimingAnalyzer class provides functionality to benchmark
 * and analyze the performance of basic operations (add, search, remove)
 * on different data structures using extracted code lexemes.
 * It visualizes both average execution time and confidence intervals.
 */
@Slf4j
public class TimingAnalyzer {

    private static final double CONFIDENCE_LEVEL = 1.96; // Default 95%

    private final Map<DataStructure, OperationStats> stats = new EnumMap<>(DataStructure.class);
    private final Random random = new Random();

    private final Deque<String> deque = new ArrayDeque<>();
    private final List<String> linkedList = new LinkedList<>();

    /**
     * Initializes the analyzer with supported data structures.
     */
    public TimingAnalyzer() {
        for (DataStructure ds : DataStructure.values()) {
            stats.put(ds, new OperationStats());
        }
    }

    /**
     * Computes the confidence interval based on sample data.
     *
     * @param duration Operation durations
     * @return Confidence interval range
     */
    private static double computeConfidenceInterval(List<Long> duration) {
        if (duration.size() < 2) {
            return 0.0;
        }
        double mean = duration.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);

        double standardDeviation = Math.sqrt(duration.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2)).sum() / (duration.size() - 1));

        double operationCount = Math.sqrt(duration.size());

        return CONFIDENCE_LEVEL * standardDeviation / operationCount;
    }

    /**
     * Runs the performance analysis using the given code snippet and operation.
     *
     * @param code       Source code to analyze for lexemes
     * @param operation  The operation to benchmark
     * @param lexemeType Specific lexeme type to filter (nullable)
     */
    public void analyzePerformance(String code, Runnable operation, LexemeType lexemeType) {
        extractLexemes(code, lexemeType);
        boolean hasLexemes = !deque.isEmpty() || !linkedList.isEmpty();
        if (!hasLexemes) {
            throw new IllegalArgumentException("No lexemes available for performance analysis");
        }
        log.debug("Extracted lexemes in each data structure:");
        log.debug("Deque: {}", deque);
        log.debug("LinkedList: {}", linkedList);

        operation.run();

        log.info("Timing analysis of each function:");
        for (DataStructure ds : DataStructure.values()) {
            for (OperationType type : OperationType.values()) {
                // Average stats logging
                logAveragePerformance(ds, type);
                // Confidence interval logging
                logConfidenceInterval(ds, type);
            }
        }
        ChartBuilder.visualizeAveragePerformance(stats);
        ChartBuilder.visualizePerformanceWithConfidence(stats);
    }

    /**
     * Overloaded method to analyze performance without specifying a lexeme type.
     */
    public void analyzePerformance(String code, Runnable operation) {
        analyzePerformance(code, operation, null);
    }

    /**
     * Extracts lexemes from the given code and stores them in both data structures.
     *
     * @param code       Source code to analyze
     * @param lexemeType Optional lexeme type filter
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
     * Adds a lexeme to both Deque and LinkedList, measuring execution time.
     *
     * @param lexeme Lexeme to add
     */
    public void addLexeme(String lexeme) {
        recordOperation(() -> deque.add(lexeme), DataStructure.DEQUE, OperationType.ADD);
        recordOperation(() -> linkedList.add(lexeme), DataStructure.LINKED_LIST, OperationType.ADD);
    }

    /**
     * Removes a lexeme from both data structures, measuring execution time.
     *
     * @param lexeme Lexeme to remove
     */
    public void removeLexeme(String lexeme) {
        recordOperation(() -> deque.remove(lexeme), DataStructure.DEQUE, OperationType.REMOVE);
        recordOperation(() -> linkedList.remove(lexeme), DataStructure.LINKED_LIST, OperationType.REMOVE);
    }

    /**
     * Searches for a lexeme in both data structures, measuring execution time.
     *
     * @param lexeme Lexeme to search
     */
    public void searchLexeme(String lexeme) {
        recordOperation(() -> deque.contains(lexeme), DataStructure.DEQUE, OperationType.SEARCH);
        recordOperation(() -> linkedList.contains(lexeme), DataStructure.LINKED_LIST, OperationType.SEARCH);
    }

    /**
     * Records operation timing for a given data structure and operation type.
     */
    private void recordOperation(Runnable op, DataStructure ds, OperationType type) {
        long duration = measureTime(op);
        OperationStats stat = stats.get(ds);
        switch (type) {
            case ADD -> {
                stat.addOpsDuration.add(duration);
                stat.addTimeGauge += duration;
                stat.addOpsCounter++;
            }
            case REMOVE -> {
                stat.removeOpsDuration.add(duration);
                stat.removeTimeGauge += duration;
                stat.removeOpsCounter++;
            }
            case SEARCH -> {
                stat.searchOpsDuration.add(duration);
                stat.searchTimeGauge += duration;
                stat.searchOpsCounter++;
            }
            default -> throw new IllegalArgumentException("Unknown operation type: " + type);
        }
    }

    /**
     * Measures the execution time of an operation in nanoseconds.
     *
     * @param operation Operation to time
     * @return Duration in nanoseconds
     */
    long measureTime(Runnable operation) {
        long start = System.nanoTime();
        operation.run();
        return System.nanoTime() - start;
    }

    /**
     * Logs the average execution time for a given data structure and operation type.
     */
    private void logAveragePerformance(DataStructure ds, OperationType type) {
        OperationStats stat = stats.get(ds);
        int opsCount = stat.getOpsCounter(type);
        double averageTime = stat.calculateAverageTime(type);

        log.info("{} -> {}: {} operations, Average time: {} ns",
                ds.name(), type.name(), opsCount, String.format("%.2f", averageTime));
    }

    /**
     * Logs the 95% confidence interval for a given data structure and operation type.
     */
    private void logConfidenceInterval(DataStructure ds, OperationType type) {
        OperationStats stat = stats.get(ds);
        List<Long> duration = stat.getDuration(type);
        double mean = duration.stream().mapToLong(Long::longValue).average().orElse(0);
        double confidenceInterval = computeConfidenceInterval(duration);
        double lowerBound = mean - confidenceInterval;
        double upperBound = mean + confidenceInterval;

        log.info("{} -> {} 95% Confidence interval: [{} ns, {} ns] (mean: {} ns)",
                ds.name(), type.name(),
                String.format("%.2f", lowerBound),
                String.format("%.2f", upperBound),
                String.format("%.2f", mean));
    }

    /**
     * @return A random number of lexemes from the deque.
     */
    public int getRandomLexemeCount() {
        return random.nextInt(deque.size()) + 1;
    }

    /**
     * Retrieves a lexeme by index from the deque.
     *
     * @param index Index to retrieve
     * @return Lexeme or null if out of bounds
     */
    public String searchLexemeByIndex(int index) {
        return deque.stream()
                .skip(index)
                .findFirst()
                .orElse(null);
    }

    /**
     * Enum representing supported data structures.
     */
    enum DataStructure {

        DEQUE, LINKED_LIST

    }

    /**
     * Enum representing operation types.
     */
    enum OperationType {

        ADD, REMOVE, SEARCH

    }

    /**
     * Helper class to hold statistics for a single data structure.
     */
    @Data
    private static final class OperationStats {

        private long addTimeGauge = 0L;
        private long searchTimeGauge = 0L;
        private long removeTimeGauge = 0L;

        private int addOpsCounter = 0;
        private int searchOpsCounter = 0;
        private int removeOpsCounter = 0;

        private List<Long> addOpsDuration = new ArrayList<>();
        private List<Long> searchOpsDuration = new ArrayList<>();
        private List<Long> removeOpsDuration = new ArrayList<>();

        /**
         * Calculates average execution time for a given operation.
         */
        double calculateAverageTime(OperationType type) {
            return switch (type) {
                case ADD -> addOpsCounter == 0 ? 0 : (double) addTimeGauge / addOpsCounter;
                case REMOVE -> removeOpsCounter == 0 ? 0 : (double) removeTimeGauge / removeOpsCounter;
                case SEARCH -> searchOpsCounter == 0 ? 0 : (double) searchTimeGauge / searchOpsCounter;
            };
        }

        /**
         * Returns list of operation durations for a given type.
         */
        List<Long> getDuration(OperationType type) {
            return switch (type) {
                case ADD -> addOpsDuration;
                case REMOVE -> removeOpsDuration;
                case SEARCH -> searchOpsDuration;
            };
        }

        /**
         * Returns total operation count for a given type.
         */
        int getOpsCounter(OperationType type) {
            return switch (type) {
                case ADD -> addOpsCounter;
                case REMOVE -> removeOpsCounter;
                case SEARCH -> searchOpsCounter;
            };
        }

    }

    /**
     * Helper class to generate performance charts.
     */
    private static final class ChartBuilder {

        /**
         * Builds and upload to file average operation time chart.
         */
        private static void visualizeAveragePerformance(Map<DataStructure, OperationStats> stats) {
            try {
                List<String> labels = new ArrayList<>();
                List<Double> addTimeDuration = new ArrayList<>();
                List<Double> searchTimeDuration = new ArrayList<>();
                List<Double> removeTimeDuration = new ArrayList<>();

                for (DataStructure ds : DataStructure.values()) {
                    OperationStats stat = stats.get(ds);
                    labels.add(ds.name());
                    addTimeDuration.add(stat.calculateAverageTime(OperationType.ADD));
                    searchTimeDuration.add(stat.calculateAverageTime(OperationType.SEARCH));
                    removeTimeDuration.add(stat.calculateAverageTime(OperationType.REMOVE));
                }

                CategoryChart chart = new CategoryChartBuilder()
                        .width(800)
                        .height(600)
                        .title("Data structure performance")
                        .xAxisTitle("Operations")
                        .yAxisTitle("Time (nanoseconds)")
                        .theme(Styler.ChartTheme.Matlab)
                        .build();

                chart.addSeries("Addition", labels, addTimeDuration);
                chart.addSeries("Search", labels, searchTimeDuration);
                chart.addSeries("Removal", labels, removeTimeDuration);

                try {
                    BitmapEncoder.saveBitmap(chart, "./charts/average_performance", BitmapEncoder.BitmapFormat.PNG);
                    log.info("Saved chart to './charts/average_performance'");
                } catch (IOException e) {
                    log.error("Cannot write chart to file", e);
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }

        /**
         * Builds and upload to file performance chart with confidence intervals.
         */
        private static void visualizePerformanceWithConfidence(Map<DataStructure, OperationStats> stats) {
            try {
                List<String> labels = new ArrayList<>();
                List<Double> addMeans = new ArrayList<>();
                List<Double> addErrors = new ArrayList<>();

                List<Double> searchMeans = new ArrayList<>();
                List<Double> searchErrors = new ArrayList<>();

                List<Double> removeMeans = new ArrayList<>();
                List<Double> removeErrors = new ArrayList<>();

                for (DataStructure ds : DataStructure.values()) {
                    labels.add(ds.name());
                    OperationStats stat = stats.get(ds);
                    collectConfidenceInterval(stat, OperationType.ADD, addMeans, addErrors);
                    collectConfidenceInterval(stat, OperationType.SEARCH, searchMeans, searchErrors);
                    collectConfidenceInterval(stat, OperationType.REMOVE, removeMeans, removeErrors);
                }

                CategoryChart chart = new CategoryChartBuilder()
                        .width(900)
                        .height(600)
                        .title("Data structure performance with 95% confidence level")
                        .xAxisTitle("Operations")
                        .yAxisTitle("Time (nanoseconds)")
                        .theme(Styler.ChartTheme.Matlab)
                        .build();

                chart.addSeries("Addition", labels, addMeans, addErrors);
                chart.addSeries("Search", labels, searchMeans, searchErrors);
                chart.addSeries("Removal", labels, removeMeans, removeErrors);

                try {
                    BitmapEncoder.saveBitmap(chart, "./charts/confidence_performance", BitmapEncoder.BitmapFormat.PNG);
                    log.info("Saved chart to './charts/confidence_performance'");
                } catch (IOException e) {
                    log.error("Cannot write chart to file", e);
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }

        /**
         * Collects mean and confidence interval values for charting.
         */
        private static void collectConfidenceInterval(OperationStats stat, OperationType type,
                                                      List<Double> means, List<Double> errors) {
            List<Long> durations = stat.getDuration(type);
            double mean = stat.calculateAverageTime(type);
            double ci = computeConfidenceInterval(durations);
            means.add(mean);
            errors.add(ci);
        }

    }

}
