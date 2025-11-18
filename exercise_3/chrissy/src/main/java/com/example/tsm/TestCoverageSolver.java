package com.example.tsm;

import java.util.ArrayList;
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

    List<TestInfo> solve(List<TestInfo> tests, Algo algo) {
        List<TestInfo> smallestSet = new ArrayList<>();

        if (algo == Algo.Greedy) {
            smallestSet = greedySetCover(tests);
        } else if (algo == Algo.Pruning) {
            // implement other algos here
            smallestSet = prune(tests); // placeholder
        }
        return smallestSet;
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
