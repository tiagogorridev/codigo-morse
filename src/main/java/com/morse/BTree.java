package com.morse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BTree {
    public Node root; // Raiz da árvore

    public BTree(Object value) {
        root = new Node(value); // Inicializa a raiz com um valor (pode ser null)
    }

    // DECODIFICAÇÃO 
    private String decodeRecursive(String remainingCode, Node currNode) {
        if (remainingCode.isEmpty()) {
            if (currNode.value == null) { // Verifica se o nó tem um valor antes de tentar converter para string
                System.out.println("codigo invalido: terminou em nó vazio");
                return null;
            }
            return currNode.value.toString();
        }

        char morseDigit = remainingCode.charAt(0); // Pega o primeiro caractere do código morse
        String rest = remainingCode.substring(1); // Resto do código

        if (morseDigit != '.' && morseDigit != '-' && morseDigit != '/') {
            System.out.println("caracter invalido: " + morseDigit);
            return null;
        }

        if (morseDigit == '/') return " ";

        if (morseDigit == '.') {
            if (currNode.left == null) {
                System.out.println("codigo invalido: " + remainingCode);
                return null;
            }
            return decodeRecursive(rest, currNode.left);
        } 
        else {
            if (currNode.right == null) {
                System.out.println("codigo invalido: " + remainingCode);
                return null;
            }
            return decodeRecursive(rest, currNode.right);
        }
    }

    public String decodeMorseCode(String morseCode) {
        String[] letters = morseCode.split(" "); // Divide o código morse em letras
        String translate = ""; // Resultado final

        for (String letter : letters) { // Decodifica cada letra individualmente
            String decoded = decodeRecursive(letter, root);
            if (decoded == null) {
                return null;
            }
            translate += decoded;
        }
        return translate;
    }

    // CODIFICAÇÃO 
    private String encodeRecursive(char targetChar, Node currNode, String pathSoFar) {
        if (currNode.value != null && currNode.value.equals(targetChar)) // Caso base - encontrou o caractere
         return pathSoFar; // Começa vazio (na raiz) e vai adicionando '.' ou '-'

        String result = null;

        if (currNode.left != null) { // Tenta ir para a esqeurda (adiciona '.' ao caminho)
            result = encodeRecursive(targetChar, currNode.left, pathSoFar + ".");
            if (result != null) return result;
        }
        
        if (currNode.right != null) { // Tenta ir para a direita (adiciona '-' ao caminho)
            result = encodeRecursive(targetChar, currNode.right, pathSoFar + "-");
            if (result != null) return result;
        }
        return null;
    }

    public String encodeToMorseCode(String text) {
        char[] letters = text.toCharArray(); // Divide o texto em caracteres
        String translate = "";

        for (int i = 0; i < letters.length; i++) {
            char letter = letters[i];
            if (letter == ' ') translate += "/ ";
            else {
                String morseCode = encodeRecursive(Character.toUpperCase(letter), root, ""); // Busca o código morse do caractere
                if (morseCode == null) {
                    System.out.println("caracter nao encontrado: " + letter);
                    return null;
                }
                translate += morseCode;
            }
            if (i < letters.length - 1 && letter != ' ') translate += " ";
        }
        return translate;
    }

    // INSERÇÃO 
    public void addNewCharacter(char letter, String morseCode) throws Exception {
        Node current = root;

        if (!Character.isLetterOrDigit(letter)) {
            throw new Exception("Apenas letras (A-Z) e números (0-9) são permitidos.");
        }

        for (char c : morseCode.toCharArray()) { // Percorre o código morse para encontrar a posição correta
            if (c == '.') {
                if (current.left == null) current.left = new Node(null); // Cria nó vazio se não existir
                current = current.left;  // Desce para a esquerda
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
    
    // REMOÇÃO E BUSCA 
    public boolean removerCaractere(char caractere) {
        caractere = Character.toUpperCase(caractere);
        
        Node nodeToRemove = encontrarNo(root, caractere); // Encontra o nó que contém o caractere
        if (nodeToRemove == null) {
            return false;
        }
        nodeToRemove.value = null; // Apenas remove o valor, mantendo a estrutura
        return true;
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
        return atual.value != null; // Retorna true se o nó final tiver um valor
    }

    private Node encontrarNo(Node currNode, char caractere) {
        if (currNode == null) return null;
        
        if (currNode.value != null && currNode.value.equals(Character.toUpperCase(caractere))) {
            return currNode;
        }
        
        Node esquerda = encontrarNo(currNode.left, caractere); // Busca na esquerda e se n encontrar, na direita
        if (esquerda != null) return esquerda;
        
        return encontrarNo(currNode.right, caractere);
    }

    // PROFUNDIDADE E ALTURA 
    public int buscarProfundidade(char caractere) {
        return buscarProfundidadeRec(root, caractere, 0); // Começa da raiz com profundidade 0
    }

    private int buscarProfundidadeRec(Node currNode, char caractere, int profundidade) {
        if (currNode == null) { // Nó nulo - caractere não encontrado
            return -1;
        }

        if (currNode.value != null && currNode.value.equals(Character.toUpperCase(caractere))) { // Nó encontrado
            return profundidade;
        }

        int esquerda = buscarProfundidadeRec(currNode.left, caractere, profundidade + 1); // Busca na subárvore esquerda e se n encontrar, na direita

        if (esquerda != -1) {
            return esquerda;
        }
        return buscarProfundidadeRec(currNode.right, caractere, profundidade + 1);
    }

    public int calcularAltura() {
        return calcularAlturaRec(root);
    }

    private int calcularAlturaRec(Node node) {
        if (node == null) 
            return -1;
        else 
            return Math.max(calcularAlturaRec(node.left), calcularAlturaRec(node.right)) + 1;

    }

    public int calcularAlturaDaLetra(char letra) {
        return calcularAlturaDaLetraRec(root, letra);
    }

    private int calcularAlturaDaLetraRec(Node node, char letra) {
        if(node == null){
            return 0;
        } 
        if (node.value != null && node.value.equals(letra)){
            return calcularAlturaRec(node);
        } else {
            return Math.max(
                calcularAlturaDaLetraRec(node.left, letra), 
                calcularAlturaDaLetraRec(node.right, letra)
            );
        }
    }

    // CONTADORES 
    public int contarLetras() { // Conta apenas nós com valor
        return contarNosRec(root, true);
    }

    public int contarNosVazios() { // Conta apenas nós sem valor
        return contarNosRec(root, false);
    }

    private int contarNosRec(Node node, boolean contarComValor) { // true = com valor, false = sem valor
        if (node == null) return 0;
        int conta = (contarComValor ? node.value != null : node.value == null) ? 1 : 0;
        return conta + contarNosRec(node.left, contarComValor) + contarNosRec(node.right, contarComValor);
    }

    public int contarTotalNos() {
        return contarTotalNosRec(root);
    }

    private int contarTotalNosRec(Node node) {
        if (node == null) return 0;
        return 1 + contarTotalNosRec(node.left) + contarTotalNosRec(node.right);
    }
    
    // DETALHES 
    public String getDetalhesNo(char caractere) {
        Node node = encontrarNo(root, caractere);
        
        if (node == null) {
            return "Caractere não encontrado!";
        }
        
        int profundidade = buscarProfundidade(caractere);
        int altura = calcularAlturaRec(node);
        
        Node pai = encontrarPai(root, node);
        
        StringBuilder detalhes = new StringBuilder();
        detalhes.append("- Detalhes do Nó -'").append(caractere).append("\n");
        detalhes.append("Profundidade: ").append(profundidade).append("\n");
        detalhes.append("Altura: ").append(altura).append("\n");
        
        if (pai == null) {
            detalhes.append("Pai: nó raiz (não tem pai)\n");
        } else if (pai.value != null) {
            detalhes.append("Pai: ").append(pai.value).append("\n");
        } else {
            detalhes.append("Pai: nó vazio (root ou intermediário)\n");
        }
        
        detalhes.append("É folha: ").append(node.left == null && node.right == null ? "Sim" : "Não").append("\n");
        
        if (node.left != null) {
            detalhes.append("Filho esquerdo (•): ").append(node.left.value != null ? node.left.value : "vazio").append("\n");
        } else {
            detalhes.append("Filho esquerdo (•): não existe\n");
        }
        
        if (node.right != null) {
            detalhes.append("Filho direito (−): ").append(node.right.value != null ? node.right.value : "vazio").append("\n");
        } else {
            detalhes.append("Filho direito (−): não existe\n");
        }
        
        return detalhes.toString();
    }

    private Node encontrarPai(Node atual, Node alvo) {
        if (atual == null || atual == alvo) {
            return null;
        }

        if (atual.left == alvo || atual.right == alvo) {
            return atual;
        }

        Node paiEsquerda = encontrarPai(atual.left, alvo);
        if (paiEsquerda != null) {
            return paiEsquerda;
        }
        return encontrarPai(atual.right, alvo);
    }

    public String getDetalhesArvore() {
        StringBuilder detalhes = new StringBuilder();
        detalhes.append("- DETALHES DA ARVORE -\n");
        
        detalhes.append("Nó Raiz: ").append(root.value != null ? root.value : "vazio").append("\n\n");
        
        int altura = calcularAltura();
        detalhes.append("Altura Total: ").append(altura).append("\n\n");
        
        int totalLetras = contarLetras();
        detalhes.append("Total de Letras: ").append(totalLetras).append("\n\n");
        
        detalhes.append("Nós Folha:\n");
        detalhes.append(listarNosFolha(root));
        detalhes.append("\n");
        
        detalhes.append("Menor profundidade geral: ").append(menorProfundidade(root, 0)).append("\n");
        detalhes.append("Menor profundidade à esquerda: ").append(menorProfundidade(root.left, 1)).append("\n");
        detalhes.append("Menor profundidade à direita: ").append(menorProfundidade(root.right, 1)).append("\n\n");
        
        detalhes.append("Altura de cada nó:\n");
        detalhes.append(listarAlturas(root));
        
        return detalhes.toString();
    }

    private String listarNosFolha(Node node) {
        if (node == null) return "";
        
        StringBuilder folhas = new StringBuilder();
        
        if (node.left == null && node.right == null && node.value != null) {
            folhas.append("  • ").append(node.value).append("\n");
        }
        
        folhas.append(listarNosFolha(node.left));
        folhas.append(listarNosFolha(node.right));
        
        return folhas.toString();
    }

    private String listarAlturas(Node node) {
        if (node == null) return "";
        
        StringBuilder lista = new StringBuilder();
        
        if (node.value != null) {
            int altura = calcularAlturaRec(node);
            lista.append("  • ").append(node.value).append(" → Altura: ").append(altura).append("\n");
        }
        
        lista.append(listarAlturas(node.left));
        lista.append(listarAlturas(node.right));
        
        return lista.toString();
    }

    private Integer menorProfundidade(Node node, int profundidade) {
        if (node == null) return null;
        
        if (node.left == null && node.right == null && node.value != null) {
            return profundidade;
        }
        
        Integer esquerda = menorProfundidade(node.left, profundidade + 1);
        Integer direita = menorProfundidade(node.right, profundidade + 1);
        
        // Trata os nulls manualmente
        if (esquerda == null && direita == null) return null;
        if (esquerda == null) return direita;
        if (direita == null) return esquerda;
        
        return Math.min(esquerda, direita); // min pois é a menor profundidade
    }
    
    // CARREGAMENTO 
    public boolean carregarAlfabetoInicial() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/data/alfabeto-vazio.txt")))) {
            String linha;
            int count = 0;

            while ((linha = br.readLine()) != null) {
                String[] partes = linha.trim().split(" ");
                if (partes.length == 2) {
                    try {
                        addNewCharacter(partes[0].charAt(0), partes[1]);
                        count++;
                    } catch (Exception e) { }
                }
            }
            return count > 0;

        } catch (IOException e) { return false; }
    }
}