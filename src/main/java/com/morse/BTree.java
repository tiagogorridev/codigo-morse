package com.morse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BTree {
    public Node root;

    public BTree(Object value) {
        root = new Node(value);
    }

    private String decodeRecursive(String remainingCode, Node currNode) {
        if (remainingCode.isEmpty()) return currNode.value.toString();

        char morseDigit = remainingCode.charAt(0);
        String rest = remainingCode.substring(1);

        if (morseDigit != '.' && morseDigit != '-' && morseDigit != '/') {
            System.out.println("caracter invalido: " + morseDigit);
            System.exit(1);
        }

        if (morseDigit == '/') return " ";

        if (morseDigit == '.') {
            if (currNode.left == null) {
                System.out.println("codigo invalido: " + remainingCode);
                System.exit(1);
            }

            return decodeRecursive(rest, currNode.left);
        } else {
            if (currNode.right == null) {
                System.out.println("codigo invalido: " + remainingCode);
                System.exit(1);
            }

            return decodeRecursive(rest, currNode.right);
        }
    }

    private String encodeRecursive(char targetChar, Node currNode, String pathSoFar) {
        if (currNode.value != null && currNode.value.equals(targetChar)) return pathSoFar;

        String result = null;

        if (currNode.left != null) {
            result = encodeRecursive(targetChar, currNode.left, pathSoFar + ".");
            if (result != null) return result;
        }

        if (currNode.right != null) {
            result = encodeRecursive(targetChar, currNode.right, pathSoFar + "-");
            if (result != null) return result;
        }

        return null;
    }

    public String decodeMorseCode(String morseCode) {
        String[] letters = morseCode.split(" ");
        String translate = "";

        for (String letter : letters) {
            translate += decodeRecursive(letter, root);
        }

        return translate;
    }

    public String encodeToMorseCode(String text) {
        char[] letters = text.toCharArray();
        String translate = "";

        for (int i = 0; i < letters.length; i++) {
            char letter = letters[i];

            if (letter == ' ') translate += "/ ";
            else {
                String morseCode = encodeRecursive(Character.toUpperCase(letter), root, "");
                if (morseCode == null) {
                    System.out.println("caracter nao encontrado: " + letter);
                    System.exit(1);
                }

                translate += morseCode;
            }

            if (i < letters.length - 1 && letter != ' ') translate += " ";
        }

        return translate;
    }

    public int buscarProfundidade(char caractere) {
        return buscarProfundidadeRec(root, caractere, 0);
    }

    private int buscarProfundidadeRec(Node currNode, char caractere, int profundidade) {

        if (currNode == null) {
            return -1; // não encontrado
        }

        if (currNode.value != null && currNode.value.equals(Character.toUpperCase(caractere))) {
            return profundidade;
        }

        // busca na esquerda
        int esquerda = buscarProfundidadeRec(currNode.left, caractere, profundidade + 1);
        if (esquerda != -1) {
            return esquerda;
        }

        // busca na direita
        return buscarProfundidadeRec(currNode.right, caractere, profundidade + 1);
    }

    // >>> Método que faz a inserção na árvore sem alterar Node.java <<<
    public void addNewCharacter(char letter, String morseCode) throws Exception {
        Node current = root;
        for (char c : morseCode.toCharArray()) {
            if (c == '.') {
                if (current.left == null) current.left = new Node(null);
                current = current.left;
            } else if (c == '-') {
                if (current.right == null) current.right = new Node(null);
                current = current.right;
            } else {
                throw new Exception("Código inválido (use apenas . ou -).");
            }
        }
        if (current.value != null) {
            throw new Exception("Já existe um caractere neste código.");
        }
        current.value = letter;
    }

    public boolean carregarAlfabetoInicial() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/data/alfabeto.txt")))) {

            String linha;
            int count = 0;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.trim().split(" ");
                if (partes.length == 2) {
                    try {
                        addNewCharacter(partes[0].charAt(0), partes[1]);
                        count++;
                    } catch (Exception e) {
                        // ignora erros individuais, continua carregando
                    }
                }
            }
            return count > 0;

        } catch (IOException e) { return false; }
    }

    public boolean buscarCaractere(char letra) {
        return buscarRecursiveCaractere(root, letra);
    }

    private boolean buscarRecursiveCaractere(Node currNode, char letra) {
        if (currNode == null) return false;

        else if (currNode.value != null && currNode.value.equals(letra)) return true;

        return buscarRecursiveCaractere(currNode.left, letra) || buscarRecursiveCaractere(currNode.right, letra);
    }

    public boolean codigoExiste(String codigo) {
        Node atual = root;
        for (char c : codigo.toCharArray()) {
            atual = (c == '.') ? atual.left : atual.right;
            if (atual == null) return false;
        }
        return atual.value != null;
    }
}
