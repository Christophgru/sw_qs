package de.rki.corona.utils;
public class BinarySearchTree<T extends Comparable<T>> {

    /*1. add - Add a value
    2. remove - Remove a value
    3. toList - Converts the tree into a list
    4. count - Counts the occurrences of a value in the tree
    5. sum - Calculate the sum of all values in the tree */
   private Node<T> root;
    public BinarySearchTree() {
         root = null;
    }


    // Adds a value to the tree
    public void add(T value) {
        root = addRecursive(root, value);
    }
    private Node<T> addRecursive(Node<T> current, T value) {
        if (current == null) {
            return new Node<>(value);
        }
        if (value.compareTo(current.value) < 0) {
            current.left = addRecursive(current.left, value);
        } else if (value.compareTo(current.value) > 0) {
            current.right = addRecursive(current.right, value);
        }else if(value.compareTo(current.value)==0){
            //duplicate value, increment count
            current.incrementCount();
        }
        return current;
    }
    // Removes a value from the tree
    public T remove(T value) {
        root = removeRecursive(root, value);
        return value;
    }
    

    private Node<T> removeRecursive(Node<T> current, T value) {
        if (current == null) {
            return null;
        }

        int cmp = value.compareTo(current.value);

        if (cmp == 0) {
            // If there are duplicates, just decrement and keep the node
            if (current.getCount() > 1) {
                current.decrementCount();
                return current;
            }

            // Now handle structural removal for a single remaining instance
            if (current.left == null && current.right == null) {
                return null; // No children
            }
            if (current.right == null) {
                return current.left; // One child
            }
            if (current.left == null) {
                return current.right; // One child
            }

            // Two children: swap with inorder successor and remove one occurrence there
            T smallestValue = findSmallestValue(current.right);
            current.value = smallestValue;
            // Remove exactly one occurrence of the successor value from the right subtree
            current.right = removeRecursive(current.right, smallestValue);
            return current;
        }

        if (cmp < 0) {
            current.left = removeRecursive(current.left, value);
        } else {
            current.right = removeRecursive(current.right, value);
        }
        return current;
    }


    private T findSmallestValue(Node<T> current) {
        return current.left == null ? current.value : findSmallestValue(current.left);
    }
    // Converts the tree into a list
    public java.util.List<T> toList() {
        java.util.List<T> list = new java.util.ArrayList<>();
        toListRecursive(root, list);
        return list;
    }
    private void toListRecursive(Node<T> current, java.util.List<T> list) {
        if (current != null) {
            toListRecursive(current.left, list);
            for (int i = 0; i < current.getCount(); i++) {
                list.add(current.value);
            }
            toListRecursive(current.right, list);
        }
    }
    //Counts the occurrences of a value in the tree
    public int count(T value) {
        return countRecursive(root, value);
    }
    private int countRecursive(Node<T> current, T value) {
        if (current == null) {
            return 0;
        }
        int count = (current.value.equals(value)) ? current.getCount() : 0;
        count += countRecursive(current.left, value);
        count += countRecursive(current.right, value);
        return count;
    }
    //Calculate the sum of all values in the tree
    public T sum() {
        //if Node root is null return the appropriate zero value for type T
        if(root==null){
            //check if T is integer, double or float and return 0 of that type
            return (T) (Integer) 0;
        }
        if (root.value instanceof Integer) {
            return (T) Integer.valueOf(sumRecursiveInts(root));

        } else if (root.value instanceof Double)
        {
            return (T) Double.valueOf(sumRecursiveDoubles(root));
        }
        else if (root.value instanceof Float)
        {
            return (T) Float.valueOf(sumRecursiveFloats(root));
        }
        else{
            throw new UnsupportedOperationException("Sum is only supported for Integer, Double and Float types");
        }
    }
    private int sumRecursiveInts(Node<T> current) {
        if (current == null) {
            return 0;
        }
        int sum = current.getIntValue()*current.getCount();
        sum += sumRecursiveInts(current.left);
        sum += sumRecursiveInts(current.right);
        return sum;
    }
    private double sumRecursiveDoubles(Node<T> current) {
        if (current == null) {
            return 0.0;
        }
        double sum = current.getDoubleValue()*current.getCount();
        sum += sumRecursiveDoubles(current.left);
        sum += sumRecursiveDoubles(current.right);
        return sum;
    }
    private float sumRecursiveFloats(Node<T> current) {
        if (current == null) {
            return 0.0f;
        }       
        float sum = current.getFloatValue()*current.getCount();
        sum += sumRecursiveFloats(current.left);
        sum += sumRecursiveFloats(current.right);
        return sum;
    }
}