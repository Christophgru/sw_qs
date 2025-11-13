package com.example.tsm;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// enum of chooseable solving algos
enum Algo {
    Greedy, Pruning
};

/**
 * Reads tests with covered lines and finds a small set of tests that
 * covers all lines using a greedy set cover heuristic.
 */
public class TestCoverageSolver {

    /**
     * Simple data class: test name + set of covered lines.
     */
    public static class TestInfo {
        private final String name;
        private final Set<Integer> coveredLines;

        public TestInfo(String name, Set<Integer> coveredLines) {
            this.name = name;
            this.coveredLines = coveredLines;
        }

        public String getName() {
            return name;
        }

        public Set<Integer> getCoveredLines() {
            return coveredLines;
        }

        @Override
        public String toString() {
            return "TestInfo{name='" + name + "', coveredLines=" + coveredLines + '}';
        }
    }

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
     * Compute the union of all covered lines.
     */
    public static Set<Integer> computeAllLines(List<TestInfo> tests) {
        Set<Integer> all = new HashSet<>();
        for (TestInfo t : tests) {
            all.addAll(t.getCoveredLines());
        }
        return all;
    }

    /**
     * Greedy set cover:
     * At each step, pick the test that covers the most remaining uncovered lines.
     * This is not guaranteed to be perfectly minimal, but is usually good and
     * simple.
     */
    public static List<TestInfo> greedySetCover(List<TestInfo> tests) {
        // work on copies so we don't mutate the original lists from the caller
        List<TestInfo> remainingTests = new ArrayList<>(tests);
        Set<Integer> uncovered = computeAllLines(tests);

        List<TestInfo> chosen = new ArrayList<>();

        while (!uncovered.isEmpty()) {
            TestInfo bestTest = null;
            int bestNewCoverage = 0;

            for (TestInfo t : remainingTests) {
                Set<Integer> lines = new HashSet<>(t.getCoveredLines());
                lines.retainAll(uncovered); // keep only not-yet-covered lines
                int newCoverage = lines.size();

                if (newCoverage > bestNewCoverage) {
                    bestNewCoverage = newCoverage;
                    bestTest = t;
                }
            }

            if (bestTest == null || bestNewCoverage == 0) {
                // No test can cover any new line → impossible to fully cover all lines
                break;
            }

            chosen.add(bestTest);
            uncovered.removeAll(bestTest.getCoveredLines());
            remainingTests.remove(bestTest);
        }

        if (!uncovered.isEmpty()) {
            System.out.println("Warning: Could not cover all lines. Still uncovered: " + uncovered);
        }

        return chosen;
    }

    /**
     * Example of manually filling the classes with data (if no file is provided).
     */
    public static List<TestInfo> createSampleData() {
        List<TestInfo> tests = new ArrayList<>();

        // Example corresponding to your sample data
        tests.add(new TestInfo("removeFirstLast",
                new HashSet<>(Arrays.asList(45, 50, 72, 77, 78))));
        tests.add(new TestInfo("test_add",
                new HashSet<>(Arrays.asList(52, 50, 72, 77, 78))));

        // Add some extra sample tests if you want
        tests.add(new TestInfo("test_subtract",
                new HashSet<>(Arrays.asList(10, 11, 45))));
        tests.add(new TestInfo("test_multiply",
                new HashSet<>(Arrays.asList(11, 52, 79))));

        return tests;
    }

    void solve(Path path, Algo algo) {
        try {
            List<TestInfo> tests;

            tests = readTestsFromFile(path);
            System.out.println("Loaded tests from file: " + path.toAbsolutePath());
            if (path.toFile().length() == 0) {
                // No file provided → use hard-coded sample data
                tests = createSampleData();
                System.out.println("Using hard-coded sample tests.");
            }

            System.out.println("All tests:");
            for (TestInfo t : tests) {
                System.out.println("  " + t);
            }

            Set<Integer> allLines = computeAllLines(tests);
            System.out.println(
                    "\nAll lines that need to be covered: " + allLines + " (total " + allLines.size() + " lines)");
            List<TestInfo> smallestSet = new ArrayList<>();
            if (algo == Algo.Greedy) {
                smallestSet = greedySetCover(tests);
            } else if (algo == Algo.Pruning) {
                // implement other algos here
                smallestSet = prune(tests); // placeholder
            }

            System.out.println("\nSelected tests (greedy smallest set):");
            for (TestInfo t : smallestSet) {
                System.out.println("  " + t.getName() + " covers " + t.getCoveredLines());
            }
            System.out.println("\nNumber of selected tests: " + smallestSet.size() +
                    " covering all " + computeAllLines(smallestSet).size() + " lines.");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    List<TestInfo> prune(List<TestInfo> tests) {
        // All lines that this set of tests currently covers
        Set<Integer> covered = computeAllLines(tests);

        // Start with "best" = the current set (no pruning)
        List<TestInfo> best = new ArrayList<>(tests);

        // Try to remove each test one by one
        for (int i = 0; i < tests.size(); i++) {
            TestInfo t = tests.get(i);

            // Create a new list without this test
            List<TestInfo> remainingTests = new ArrayList<>(tests);
            remainingTests.remove(i);

            // Lines still covered if we drop t
            Set<Integer> remainingCoverage = computeAllLines(remainingTests);

            // If removing t still covers everything, we can recurse
            if (remainingCoverage.containsAll(covered)) {
                List<TestInfo> candidate = prune(remainingTests);
                if (candidate.size() < best.size()) {
                    best = candidate;
                }
            }
        }

        return best;
    }

}
