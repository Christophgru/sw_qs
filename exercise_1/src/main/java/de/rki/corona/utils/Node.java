package de.rki.corona.utils;

//implement for T extends Comparable<T>
public class Node<T extends Comparable<T>> {
    T value;
    int count = 1; // For handling duplicates
    Node<T> left, right;

    public Node(T item) {
        value = item;
        left = right = null;
    }
    public Integer getIntValue() {
        assert value instanceof Integer : "Value is not an Integer";    
        return (Integer) value;
    }

    public Double getDoubleValue() {
        assert value instanceof Double : "Value is not a Double";
        return (Double) value;
    }

    public Float getFloatValue() {
        assert value instanceof Float : "Value is not a Float";
        return (Float) value;
    }
    public void incrementCount() {
        count++;
    }
    public void decrementCount() {
        count--;  }
    public int getCount() {
        return count;
    }

}
