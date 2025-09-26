package com.morse;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainController {

    @FXML private VBox inserirPanel, removerPanel, codificarPanel, decodificarPanel;
    @FXML private TextField letraInput, morseInput, removerInput, textoInput, morseInputDecode;
    @FXML private TextArea resultadoCodificacao, resultadoDecodificacao, mensagensArea;
    @FXML private Canvas treeCanvas;

    private Node arvore;

    @FXML
    private void initialize() {
        arvore = new Node(null);
        esconderTodosPaineis();
        carregarAlfabetoInicial();
        adicionarMensagem("Sistema inicializado.");
    }

    private void carregarAlfabetoInicial() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/data/alfabeto.txt")))) {

            String linha;
            int count = 0;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.trim().split(" ");
                if (partes.length == 2) {
                    try {
                        addNewCharacter(arvore, partes[0].charAt(0), partes[1]);
                        count++;
                    } catch (Exception e) {
                        // ignora erros individuais, continua carregando
                    }
                }
            }
            if (count > 0) {
                adicionarMensagem("Alfabeto carregado: " + count + " caracteres");
                atualizarArvore();
            }
        } catch (IOException e) {
            adicionarMensagem("Aviso: Alfabeto não carregado.");
        }
    }

    // >>> Método que faz a inserção na árvore sem alterar Node.java <<<
    private void addNewCharacter(Node root, char letter, String morseCode) throws Exception {
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

    private void esconderTodosPaineis() {
        VBox[] paineis = {inserirPanel, removerPanel, codificarPanel, decodificarPanel};
        for (VBox painel : paineis) {
            painel.setVisible(false);
            painel.setManaged(false);
        }
    }

    private void mostrarPainel(VBox painel) {
        esconderTodosPaineis();
        painel.setVisible(true);
        painel.setManaged(true);
    }

    @FXML private void mostrarInserir() { mostrarPainel(inserirPanel); }
    @FXML private void mostrarRemover() { mostrarPainel(removerPanel); }
    @FXML private void mostrarCodificar() { mostrarPainel(codificarPanel); }
    @FXML private void mostrarDecodificar() { mostrarPainel(decodificarPanel); }

    @FXML
    private void inserirCaractere() {
        String letra = letraInput.getText().trim().toUpperCase();
        String morse = morseInput.getText().trim();

        if (letra.isEmpty() || morse.isEmpty() || letra.length() != 1 || !Character.isLetter(letra.charAt(0))) {
            adicionarMensagem("ERRO: Digite uma letra válida!");
            return;
        }

        if (!morse.matches("[.-]+")) {
            adicionarMensagem("ERRO: Use apenas . e -");
            return;
        }

        char c = letra.charAt(0);
        if (caracterExiste(c)) {
            adicionarMensagem("ERRO: Caractere já existe!");
            return;
        }

        if (codigoExiste(morse)) {
            adicionarMensagem("ERRO: Código já existe!");
            return;
        }

        try {
            addNewCharacter(arvore, c, morse);
            adicionarMensagem("Inserido: " + c + " = " + morse);
            letraInput.clear();
            morseInput.clear();
            atualizarArvore();
        } catch (Exception e) {
            adicionarMensagem("ERRO: " + e.getMessage());
        }
    }

    private boolean caracterExiste(char letra) {
        return buscarCaractere(arvore, letra);
    }

    private boolean buscarCaractere(Node node, char letra) {
        if (node == null) return false;
        if (node.value != null && node.value.equals(letra)) return true;
        return buscarCaractere(node.left, letra) || buscarCaractere(node.right, letra);
    }

    private boolean codigoExiste(String codigo) {
        Node atual = arvore;
        for (char c : codigo.toCharArray()) {
            atual = (c == '.') ? atual.left : atual.right;
            if (atual == null) return false;
        }
        return atual.value != null;
    }

    @FXML private void removerCaractere() { adicionarMensagem("Remoção não implementada."); }

    @FXML
    private void codificarTexto() {
        String texto = textoInput.getText().trim();
        if (texto.isEmpty()) {
            adicionarMensagem("ERRO: Digite um texto!");
            return;
        }

        try {
            String resultado = arvore.encodeToMorseCode(texto);
            resultadoCodificacao.setText(resultado);
            adicionarMensagem("Codificado com sucesso");
        } catch (Exception e) {
            adicionarMensagem("ERRO: Caractere não encontrado");
        }
    }

    @FXML
    private void decodificarMorse() {
        String morse = morseInputDecode.getText().trim();
        if (morse.isEmpty()) {
            adicionarMensagem("ERRO: Digite código morse!");
            return;
        }

        try {
            String resultado = arvore.decodeMorseCode(morse);
            resultadoDecodificacao.setText(resultado);
            adicionarMensagem("Decodificado com sucesso");
        } catch (Exception e) {
            adicionarMensagem("ERRO: Código inválido");
        }
    }

    @FXML
    private void atualizarArvore() {
        try {
            desenharArvore();
            adicionarMensagem("Árvore atualizada");
        } catch (Exception e) {
            adicionarMensagem("Erro ao desenhar árvore");
        }
    }

    @FXML private void limparMensagens() { mensagensArea.clear(); }

    private void desenharArvore() {
        GraphicsContext gc = treeCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, treeCanvas.getWidth(), treeCanvas.getHeight());
        if (arvore != null) {
            desenharNo(gc, arvore, treeCanvas.getWidth() / 2, 40, treeCanvas.getWidth() / 4);
        }
    }

    private void desenharNo(GraphicsContext gc, Node node, double x, double y, double offset) {
        if (node == null) return;

        // Desenha o círculo do nó
        gc.setFill(Color.WHITE);
        gc.fillOval(x - 15, y - 15, 30, 30);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(x - 15, y - 15, 30, 30);

        // Desenha o valor do nó (se existir)
        if (node.value != null) {
            gc.setFill(Color.BLACK);
            gc.fillText(node.value.toString(), x - 5, y + 5);
        }

        double childY = y + 80;
        double childOffset = Math.max(offset / 2, 30);

        // Desenha linha e símbolo para filho esquerdo (ponto)
        if (node.left != null) {
            double leftX = x - offset;
            
            // Linha para o filho esquerdo
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeLine(x, y + 15, leftX, childY - 15);
            
            desenharNo(gc, node.left, leftX, childY, childOffset);
        }

        // Desenha linha e símbolo para filho direito (traço)
        if (node.right != null) {
            double rightX = x + offset;
            
            // Linha para o filho direito
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeLine(x, y + 15, rightX, childY - 15);
            
            desenharNo(gc, node.right, rightX, childY, childOffset);
        }
    }


    private void adicionarMensagem(String msg) {
        String atual = mensagensArea.getText();
        if (atual.length() > 800) atual = atual.substring(0, 400);
        mensagensArea.setText(msg + "\n" + atual);
    }
}
