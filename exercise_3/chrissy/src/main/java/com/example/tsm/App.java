package com.example.tsm;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.tsm.TestCoverageSolver.TestInfo;

/**
 * Hello world!
 */
public class App {

    /**
     * Parse a file of the form:
     * testName1
     * 45
     * 50
     * 72
     * testName2
     * 10
     * 11
     * ...
     */
    public static List<TestInfo> readTestsFromFile(Path path) throws IOException {
        List<TestInfo> tests = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            String currentTestName = null;
            Set<Integer> currentLines = new HashSet<>();

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // skip blank lines
                }

                if (line.matches("\\d+")) {
                    // This line is a line number
                    if (currentTestName == null) {
                        throw new IOException("Found line number before any test name: " + line);
                    }
                    currentLines.add(Integer.parseInt(line));
                } else {
                    // This line is a new test name
                    if (currentTestName != null) {
                        tests.add(new TestInfo(currentTestName, new HashSet<>(currentLines)));
                        currentLines.clear();
                    }
                    currentTestName = line;
                }
            }

            // Add last test if file didn't end with a blank line
            if (currentTestName != null) {
                tests.add(new TestInfo(currentTestName, currentLines));
            }
        }

        return tests;
    }

    /**
     * Example of manually filling the classes with data (if no file is provided).
     */
    public static List<TestInfo> createSampleData(int n, int m) {
        // create n tests covering gaussian distr with average of m random lines
        List<TestInfo> tests = new ArrayList<>();

        // Example corresponding to your sample data
        for (int i = 1; i <= n; i++) {
            Set<Integer> lines = new HashSet<>();
            for (int j = 0; j < m; j++) {
                int line = (int) (Math.random() * 100); // random line between 0 and 99
                lines.add(line);
            }
            tests.add(new TestInfo("test" + i, lines));
        }
        return tests;
    }

    public static void main(String[] args) {
        TestCoverageSolver solver = new TestCoverageSolver();
        Path path = Paths
                .get("D:\\bin\\uniulm\\sw_qs\\exercise_3\\src\\main\\java\\com\\example\\tsm\\coveredLines.txt");
        List<TestInfo> data = null;

        // lets to some time evaluation
        long deltaPruning = 0;
        long deltaGreedy = 0;
        double acc_deviation_algos = 0;
        int number_attempts = 100;
        for (int i = 0; i < number_attempts; i++) {
            try {
                data = readTestsFromFile(path);
                data = createSampleData(20, 11);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long startTime = System.currentTimeMillis();
            int smallest_set_found_pruning = solver.solve(data, Algo.Pruning).size();
            long endTime = System.currentTimeMillis();
            deltaPruning = deltaPruning + (endTime - startTime);
            long startTimeGreedy = System.currentTimeMillis();
            int smallest_set_found_greedy = solver.solve(data, Algo.Greedy).size();
            long endTimeGreedy = System.currentTimeMillis();
            deltaGreedy = deltaGreedy + (endTimeGreedy - startTimeGreedy);
            if (smallest_set_found_pruning != smallest_set_found_greedy) {
                System.out.println(
                        "Got diffrent results in run [" + i + "]: greedy ->" + smallest_set_found_greedy + " pruning-> "
                                + smallest_set_found_pruning);
                acc_deviation_algos += smallest_set_found_greedy - smallest_set_found_pruning;
            }
        } // print deviation with three digits
        System.err.println("Average deviation of Greedy:" + (acc_deviation_algos / number_attempts));
        System.out.println("Pruning Time taken: " + deltaPruning + " ms");
        System.out.println("Greedy Time taken: " + deltaGreedy + " ms");
    }
}
