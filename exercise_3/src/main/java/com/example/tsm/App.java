package com.example.tsm;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        TestCoverageSolver solver = new TestCoverageSolver();
        Path path = Paths
                .get("D:\\bin\\uniulm\\sw_qs\\excercise_3\\src\\main\\java\\com\\example\\tsm\\coveredLines.txt");
        solver.solve(path, Algo.Pruning);
    }
}
