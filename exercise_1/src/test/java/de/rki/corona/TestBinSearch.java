package de.rki.corona;
import de.rki.corona.utils.BinarySearchTree;


public class TestBinSearch {
    //this is a small example to show that the BinarySearchTree works for simple cases
    public static void main(String[] args) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        bst.add(5);
        bst.add(3);
        bst.add(7);
        bst.remove(3);
        assert bst.count(5) == 1;
        int sum = bst.sum();
        java.util.List<Integer> list = bst.toList();
        System.out.println("Tree (sum=" + sum + ") to List: " + list);
    }
}
