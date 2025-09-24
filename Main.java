import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Node tree = new Node("START");
        initializeMorseCodeBinaryThree(tree);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Digite codigo morse tendo cada letra separada por espaco (ex: .. .- .):");
            String morseCode = scanner.nextLine();
            System.out.println(tree.decodeMorseCode(morseCode));

            System.out.println("Digite um oração contendo somente letras do alfabeto ou espaços (ou caracteres anteriormente adicionados)");
            String input = scanner.nextLine();
            System.out.println(tree.encodeToMorseCode(input));

            System.out.println("Digite um caracter a ser adicionado");
            char newChar = scanner.nextLine().charAt(0);
            System.out.println("Digite o codigo morse para o caracter anteriormente digitado");
            String newMorseCode = scanner.nextLine();
            tree.addNewCharacter(newChar, newMorseCode);
        }
    }

    private static void initializeMorseCodeBinaryThree(Node tree) {
        String filePath = "alfabeto.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Node currNode = tree;

                char currChar = line.charAt(0);

                String morseCode = line.split(" ")[1];
                for (int i = 0; i < morseCode.length(); i++) {
                    char currMorseCode =  morseCode.charAt(i);

                    if (currMorseCode == '.') {
                        if (currNode.left == null) currNode.left = new Node(null);
                        currNode = currNode.left;
                    } else {
                        if (currNode.right == null) currNode.right = new Node(null);
                        currNode = currNode.right;
                    }

                    if (i == morseCode.length() - 1) currNode.value = currChar;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
