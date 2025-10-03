package com.morse;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MainController {

    @FXML private VBox inserirPanel, removerPanel, codificarPanel, decodificarPanel, buscarProfundidadePanel, detalhesNoPanel, detalhesArvorePanel;
    @FXML private TextField letraInput, morseInput, removerInput, textoInput, morseInputDecode, profundidadeInput, detalhesNoInput;
    @FXML private TextArea resultadoCodificacao, resultadoDecodificacao, mensagensArea, resultadoProfundidade, resultadoDetalhesNo, resultadoDetalhesArvore;
    @FXML private Canvas treeCanvas;

    private BTree arvore;

    @FXML
    private void initialize() {
        arvore = new BTree(null);
        esconderTodosPaineis();

        if (arvore.carregarAlfabetoInicial()) {
            adicionarMensagem("Alfabeto carregado");
            atualizarArvore();
        } else adicionarMensagem("Aviso: Alfabeto não carregado.");

        adicionarMensagem("Sistema inicializado.");
    }

    private void esconderTodosPaineis() {
        VBox[] paineis = {inserirPanel, removerPanel, codificarPanel, decodificarPanel, buscarProfundidadePanel, detalhesNoPanel, detalhesArvorePanel};
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
    @FXML private void mostrarBuscaProfundidade() { mostrarPainel(buscarProfundidadePanel); }
    @FXML private void mostrarDetalhesNo() { mostrarPainel(detalhesNoPanel); }
    @FXML private void mostrarDetalhesArvore() { mostrarPainel(detalhesArvorePanel); }

    @FXML
    private void inserirCaractere() {
        String letra = letraInput.getText().trim().toUpperCase();
        String morse = morseInput.getText().trim();

        if (letra.isEmpty() || morse.isEmpty() || letra.length() != 1) {
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

        if (arvore.codigoExiste(morse)) {
            adicionarMensagem("ERRO: Código já existe!");
            return;
        }

        try {
            arvore.addNewCharacter(c, morse);
            adicionarMensagem("Inserido: " + c + " = " + morse);
            letraInput.clear();
            morseInput.clear();
            atualizarArvore();
        } catch (Exception e) {
            adicionarMensagem("ERRO: " + e.getMessage());
        }
    }

    private boolean caracterExiste(char letra) {
        return arvore.buscarCaractere(letra);
    }

    @FXML
    private void removerCaractere() {
        String texto = removerInput.getText().trim().toUpperCase();
        
        if (texto.isEmpty()) {
            adicionarMensagem("ERRO: Digite um caractere!");
            return;
        }
        
        if (texto.length() > 1) {
            adicionarMensagem("ERRO: Digite apenas um caractere!");
            return;
        }
        
        char caractere = texto.charAt(0);
        
        if (arvore.removerCaractere(caractere)) {
            adicionarMensagem("Caractere '" + caractere + "' removido com sucesso");
            removerInput.clear();
            atualizarArvore();
        } else {
            adicionarMensagem("ERRO: Caractere '" + caractere + "' não encontrado na árvore");
        }
    }

    @FXML
    private void codificarTexto() {
        String texto = textoInput.getText().trim();
        if (texto.isEmpty()) {
            adicionarMensagem("ERRO: Digite um texto!");
            return;
        }

        String resultado = arvore.encodeToMorseCode(texto);
        
        if (resultado == null) {
            adicionarMensagem("ERRO: Caractere não encontrado na árvore");
            resultadoCodificacao.setText("");
        } else {
            resultadoCodificacao.setText(resultado);
            adicionarMensagem("Codificado com sucesso");
        }
    }

    @FXML
    private void decodificarMorse() {
        String morse = morseInputDecode.getText().trim();
        if (morse.isEmpty()) {
            adicionarMensagem("ERRO: Digite código morse!");
            return;
        }

        String resultado = arvore.decodeMorseCode(morse);
        
        if (resultado == null) {
            adicionarMensagem("ERRO: Código morse inválido");
            resultadoDecodificacao.setText("");
        } else {
            resultadoDecodificacao.setText(resultado);
            adicionarMensagem("Decodificado com sucesso");
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
            desenharNo(gc, arvore.root, treeCanvas.getWidth() / 2, 40, treeCanvas.getWidth() / 4);
        }
    }

    private void desenharNo(GraphicsContext gc, Node node, double x, double y, double offset) {
        if (node == null) return;

        gc.setFill(Color.WHITE); // Círculo
        gc.fillOval(x - 15, y - 15, 30, 30);

        gc.setStroke(Color.BLACK); // Borda
        gc.setLineWidth(2);
        gc.strokeOval(x - 15, y - 15, 30, 30);

        if (node.value != null) { // Desenha o valor do nó (se existir)
            gc.setFill(Color.BLACK);
            gc.fillText(node.value.toString(), x - 5, y + 5);
        }

        double childY = y + 80;
        double childOffset = Math.max(offset / 2, 30);

        if (node.left != null) { // Desenha linha e símbolo para filho esquerdo (ponto)
            double leftX = x - offset;
            
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeLine(x, y + 15, leftX, childY - 15);
            desenharNo(gc, node.left, leftX, childY, childOffset);
        }

        if (node.right != null) {// Desenha linha e símbolo para filho direito (traço)
            double rightX = x + offset;
            
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

    @FXML
    private void buscarProfundidadeCaractere() {
        String texto = profundidadeInput.getText().trim();
    
        if (texto.isEmpty()) {
            adicionarMensagem("ERRO: Digite um caractere!");
            return;
        }
        if (texto.length() > 1) {
            adicionarMensagem("ERRO: Digite apenas um caractere!");
            return;
        }
        try {
            char caractere = texto.charAt(0);
            int profundidade = arvore.buscarProfundidade(caractere);

            if(profundidade < 0) resultadoProfundidade.setText("Profundidade não pôde ser identificada");
            else resultadoProfundidade.setText("Profundidade: " + profundidade);
            adicionarMensagem("Busca realizada com sucesso");
        } catch (Exception e) {
            adicionarMensagem("ERRO: Caractere não encontrado");
        }
    }

    @FXML
    private void verDetalhesNo() {
        String texto = detalhesNoInput.getText().trim().toUpperCase();
        
        if (texto.isEmpty()) {
            adicionarMensagem("ERRO: Digite um caractere!");
            return;
        }
        if (texto.length() > 1) {
            adicionarMensagem("ERRO: Digite apenas um caractere!");
            return;
        }
        try {
            char caractere = texto.charAt(0);
            String detalhes = arvore.getDetalhesNo(caractere);
            resultadoDetalhesNo.setText(detalhes);
            adicionarMensagem("Detalhes do nó consultados");
        } catch (Exception e) {
            adicionarMensagem("ERRO: " + e.getMessage());
            resultadoDetalhesNo.setText("Erro ao buscar detalhes!");
        }
    }

    @FXML
    private void verDetalhesArvore() {
        try {
            String detalhes = arvore.getDetalhesArvore();
            resultadoDetalhesArvore.setText(detalhes);
            adicionarMensagem("Detalhes da árvore consultados");
        } catch (Exception e) {
            adicionarMensagem("ERRO: " + e.getMessage());
            resultadoDetalhesArvore.setText("Erro ao buscar detalhes!");
        }
    }
}
