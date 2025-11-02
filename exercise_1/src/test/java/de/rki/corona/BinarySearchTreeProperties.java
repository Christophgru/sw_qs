package de.rki.corona;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;

import de.rki.corona.utils.BinarySearchTree;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

class BinarySearchTreeProperties {

    /* ====== Arbitraries / Generators ====== */

    @Provide
    Arbitrary<List<Integer>> intLists() {
        // Keeps values in a modest range so shrinking is fast, but includes dupes
        return Arbitraries.integers().between(-100, 100)
            .list().ofMinSize(0).ofMaxSize(200);

    }

    @Provide
    Arbitrary<Integer> anyInt() {
        return Arbitraries.integers().between(-1000, 1000);
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
    void count_matches_frequency_in_list(@ForAll("intLists") List<Integer> values, @ForAll("anyInt") Integer probe) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        values.forEach(bst::add);

        int count = bst.count(probe);
        long expected = bst.toList().stream().filter(x -> Objects.equals(x, probe)).count();

        Assertions.assertEquals(expected, count,
                () -> "count(" + probe + ") mismatch");
    }

    @Property(tries = 150)
    void removing_all_instances_makes_count_zero(@ForAll("intLists") List<Integer> values,
                                                 @ForAll("anyInt") Integer target) {
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

    @Property(tries = 150)
    void empty_tree_invariants_hold() {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        Assertions.assertEquals(0, bst.sum(), "Empty tree sum should be zero");
        Assertions.assertTrue(bst.toList().isEmpty(), "Empty tree toList should be empty");
        Assertions.assertEquals(0, bst.count(123), "Empty tree count should be zero");
    }

    
}
