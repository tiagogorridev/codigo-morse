package com.morse;

public class Node {
    public Object value;
    public Node left;
    public Node right;

    public Node(Object value, Node left, Node right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public Node(Object value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
}