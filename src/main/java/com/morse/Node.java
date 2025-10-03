package com.morse;

public class Node {
    public Object value; // Armazena o caractere
    public Node left;
    public Node right;

    // Construtor completo
    public Node(Object value, Node left, Node right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    // Construtor simples - inicializa filhos como null
    public Node(Object value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
}