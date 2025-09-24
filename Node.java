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

        for (String letter: letters) {
            translate += decodeRecursive(letter,this);
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
                String morseCode = encodeRecursive(Character.toUpperCase(letter), this, "");
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

    public void addNewCharacter(char newCharacter, String newMorseCode) {
        if (newMorseCode == null || newMorseCode.isBlank()) {
            System.out.println("Codigo morse n pode estar vazio");
            System.exit(1);
        }

        for (char c: newMorseCode.toCharArray()) {
            if (c != '.' && c != '-') {
                System.out.println("Codigo morse invalido, somente '-' e '.' permitidos");
                System.exit(1);
            }
        }

        newCharacter = Character.toUpperCase(newCharacter);

        if (findCharacter(newCharacter, this)) {
            System.out.println("Caracter ja existente");
            System.exit(1);
        }

        if (findMorseCode(newMorseCode, this)) {
            System.out.println("Codigo morse ja existe na arvore");
            System.exit(1);
        }

        addNewCharacterRecursive(newCharacter, newMorseCode, this);
    }

    private void addNewCharacterRecursive(char newCharacter, String newMorseCode, Node currentNode) {
        if (newMorseCode.isEmpty()) {
            currentNode.value = newCharacter;
            return;
        }

        char morseChar = newMorseCode.charAt(0);
        String remainingMorseCode = newMorseCode.substring(1);

        if (morseChar == '.') {
            if (currentNode.left == null) currentNode.left = new Node(null);
            addNewCharacterRecursive(newCharacter, remainingMorseCode, currentNode.left);
        } else {
            if (currentNode.right == null) currentNode.right = new Node(null);
            addNewCharacterRecursive(newCharacter, remainingMorseCode, currentNode.right);
        }
    }

    private boolean findCharacter(char targetChar, Node currentNode) {
        if (currentNode == null) return false;

        if (currentNode.value != null && currentNode.value.equals(targetChar)) return true;

        return findCharacter(targetChar, currentNode.left) || findCharacter(targetChar, currentNode.right);
    }

    private boolean findMorseCode(String targetMorseCode, Node currentNode) {
        if (currentNode == null) return false;

        if (targetMorseCode.isEmpty()) return currentNode.value != null;

        char morseChar = targetMorseCode.charAt(0);
        String remainingMorseCode = targetMorseCode.substring(1);

        Node nextNode;
        if (morseChar == '.') nextNode = currentNode.left;
        else nextNode = currentNode.right;

        if (nextNode == null) return false;

        return findMorseCode(remainingMorseCode, nextNode);
    }
}
