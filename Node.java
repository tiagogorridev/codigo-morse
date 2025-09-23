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
}
