package de.rki.corona;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;

import de.rki.corona.utils.BinarySearchTree;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.From;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.Size;

//mvn -q -Dtest=de.rki.corona.BinarySearchTreeProperties test



class BinarySearchTreeProperties {


    @Example
    void reproduces_example_sequence() {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        bst.add(5);
        bst.add(3);
        bst.add(7);
        bst.remove(3);
        Assertions.assertEquals(1, bst.count(5));
        int sum = bst.sum();
        List<Integer> list = bst.toList();

        // Expected: only 5 and 7 remain, in order
        Assertions.assertEquals(Arrays.asList(5, 7), list);
        Assertions.assertEquals(12, sum);
    }

    /* ====== Arbitraries / Generators ====== */

    @Provide
    Arbitrary<List<Integer>> intLists() {
        // Keeps values in a modest range so shrinking is fast, but includes dupes
        return Arbitraries.integers().between(-100, 100)
            .list().ofMinSize(0).ofMaxSize(200);

    }

    @Provide
    Arbitrary<Integer> anyInt() {
        return Arbitraries.integers().between(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Provide
    Arbitrary<Integer> smallInt() {
        return Arbitraries.integers().between(-100, 100);
    }

    /* ====== Basic invariants on a populated tree ====== */

    @Property(tries = 200)
    void inorder_toList_is_sorted_and_reflects_multiplicities(@ForAll("intLists") List<Integer> values) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        for (int v : values) bst.add(v);

        List<Integer> asList = bst.toList();

        // 1) in-order should be non-decreasing
        for (int i = 1; i < asList.size(); i++) {
            Assertions.assertTrue(
            asList.get(i - 1) <= asList.get(i),
            "List not sorted at index " + (i - 1) + ": " + asList
        );
        }

        // 2) multiplicities should match input multiset
        Map<Integer, Long> expectedCounts = values.stream()
                .collect(Collectors.groupingBy(x -> x, Collectors.counting()));
        Map<Integer, Long> actualCounts = asList.stream()
                .collect(Collectors.groupingBy(x -> x, Collectors.counting()));
        Assertions.assertEquals(expectedCounts, actualCounts, "Multiplicity mismatch between input and toList()");
    }

    @Property(tries = 200)
    void sum_matches_sum_of_toList(@ForAll("intLists") List<Integer> values) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        values.forEach(bst::add);

        int sumByTree = bst.sum();
        long sumByList = bst.toList().stream().mapToLong(Integer::intValue).sum();

        Assertions.assertEquals(sumByList, sumByTree,
                "sum() must equal the sum of values returned by toList()");
    }

    @Property(tries = 200)
    void count_matches_frequency_in_list(@ForAll("intLists") List<Integer> values, @ForAll("smallInt") Integer probe) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        values.forEach(bst::add);

        int count = bst.count(probe);
        long expected = bst.toList().stream().filter(x -> Objects.equals(x, probe)).count();

        Assertions.assertEquals(expected, count,
                () -> "count(" + probe + ") mismatch");
    }

    @Property(tries = 150)
    void removing_all_instances_makes_count_zero(@ForAll("intLists") List<Integer> values,
                                                 @ForAll("smallInt") Integer target) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        values.forEach(bst::add);

        // Remove target as many times as it appears + a few extra to test idempotence
        int initial = bst.count(target);
        for (int i = 0; i < initial + 3; i++) {
            bst.remove(target);
        }
        Assertions.assertEquals(0, bst.count(target), "Count after removing all instances should be zero");
        // Removing non-existent element shouldn't break invariants
        List<Integer> list = bst.toList();
        for (int i = 1; i < list.size(); i++) {
            Assertions.assertTrue(list.get(i - 1) <= list.get(i), "Tree broke ordering after over-removal");
        }
    }

    @Property(tries = 5)
    void empty_tree_invariants_hold() {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        Assertions.assertEquals(0, bst.sum(), "Empty tree sum should be zero");
        Assertions.assertTrue(bst.toList().isEmpty(), "Empty tree toList should be empty");
        Assertions.assertEquals(0, bst.count(123), "Empty tree count should be zero");
    }

    /* ====== Model-based test with a reference multiset ======
       We generate a sequence of operations and compare BST against a HashMap<Integer,Integer>
       as ground truth for multiplicities.
    */

    interface Op {
        void apply(BinarySearchTree<Integer> bst, Map<Integer, Integer> model);
        String describe();
    }

    static Op addOp(int x) {
        return new Op() {
            @Override public void apply(BinarySearchTree<Integer> bst, Map<Integer, Integer> model) {
                bst.add(x);
                model.merge(x, 1, Integer::sum);
            }
            @Override public String describe() { return "add(" + x + ")"; }
        };
    }

    static Op removeOp(int x) {
        return new Op() {
            @Override public void apply(BinarySearchTree<Integer> bst, Map<Integer, Integer> model) {
                bst.remove(x);
                model.compute(x, (k, v) -> v == null ? null : (v > 1 ? v - 1 : null));
            }
            @Override public String describe() { return "remove(" + x + ")"; }
        };
    }

    @Provide
    Arbitrary<Op> ops() {
        Arbitrary<Integer> smallInts = Arbitraries.integers().between(-50, 50);
        Arbitrary<Op> adds = smallInts.map(BinarySearchTreeProperties::addOp);
        Arbitrary<Op> removes = smallInts.map(BinarySearchTreeProperties::removeOp);
        return Arbitraries.oneOf(adds, removes);
    }

    @Property(tries = 200)
    void model_based_sequence_keeps_tree_consistent(
            @ForAll @Size(value = 0, max = 100) List<@From("ops") Op> ops) {

        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        Map<Integer, Integer> model = new HashMap<>();

        StringBuilder trace = new StringBuilder();
        for (Op op : ops) {
            op.apply(bst, model);
            trace.append(op.describe()).append("; ");
        }

        // 1) Check multiplicities using count()
        for (Map.Entry<Integer, Integer> e : model.entrySet()) {
            int actual = bst.count(e.getKey());
            Assertions.assertEquals(e.getValue().intValue(), actual,
                    () -> "After ops: " + trace + " count(" + e.getKey() + ") mismatch");
        }
        // Any element not in model should have count 0 (spot check with a small range)
        for (int probe = -60; probe <= 60; probe++) {
            if (!model.containsKey(probe)) {
                Assertions.assertEquals(0, bst.count(probe),
                        "After ops: " + trace + " unexpected count for " + probe);
            }
        }

        // 2) toList sorted & matches model multiplicities
        List<Integer> asList = bst.toList();
        for (int i = 1; i < asList.size(); i++) {
            Assertions.assertTrue(asList.get(i - 1) <= asList.get(i),
                    () -> "Ordering broken after ops: " + trace);
        }
        Map<Integer, Long> listCounts = asList.stream()
                .collect(Collectors.groupingBy(x -> x, Collectors.counting()));
        Map<Integer, Long> modelCounts = model.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().longValue()));
        Assertions.assertEquals(modelCounts, listCounts,
                () -> "Multiplicity mismatch after ops: " + trace);

        // 3) sum equals sum of keys by multiplicity
        long expectedSum = model.entrySet().stream()
                .mapToLong(e -> (long) e.getKey() * e.getValue())
                .sum();
        Assertions.assertEquals(expectedSum, (long) bst.sum(),
                () -> "Sum mismatch after ops: " + trace);
    }



}
