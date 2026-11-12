package com.example.musilmaturita;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CipherController {

    @FXML
    private ComboBox<String> cipherSelector;

    @FXML
    private TextField keyField;

    @FXML
    private TextArea inputArea;

    @FXML
    private TextArea outputArea;

    private final String[] baconCodes = {
            "AAAAA", "AAAAB", "AAABA", "AAABB", "AABAA", "AABAB", "AABBA", "AABBB",
            "ABAAA", "ABAAB", "ABABA", "ABABB", "ABBAA", "ABBAB", "ABBBA", "ABBBB",
            "BAAAA", "BAAAB", "BAABA", "BAABB", "BABAA", "BABAB", "BABBA", "BABBB",
            "BBAAA", "BBAAB"
    };

    @FXML
    public void initialize() {
        cipherSelector.getItems().addAll(
                "Caesarova šifra",
                "Atbash",
                "Baconova šifra",
                "Vigenèrova šifra",
                "Gronsfeldova šifra",
                "Autokey šifra"
        );
        // Vybere první položku jako výchozí, aby nevznikla NullPointerException
        cipherSelector.getSelectionModel().selectFirst();
    }

    @FXML
    private void onEncrypt() {
        processText(true);
    }

    @FXML
    private void onDecrypt() {
        processText(false);
    }

    private void processText(boolean encrypt) {
        String input = inputArea.getText();
        String selectedCipher = cipherSelector.getValue();

        if (selectedCipher == null) {
            outputArea.setText("Vyberte prosím šifru.");
            return;
        }

        if (input == null || input.isEmpty()) {
            outputArea.setText("");
            return;
        }

        String result = "";
        String keyInput = keyField.getText() == null ? "" : keyField.getText().trim();

        switch (selectedCipher) {
            case "Caesarova šifra":
                int shift;
                try {
                    shift = Integer.parseInt(keyInput);
                } catch (NumberFormatException e) {
                    outputArea.setText("Zadejte platné celé číslo pro posun!");
                    return;
                }
                if (!encrypt) {
                    shift = -shift;
                }
                result = caesarCipher(input, shift);
                break;

            case "Atbash":
                result = atbashCipher(input);
                break;

            case "Baconova šifra":
                result = encrypt ? baconEncrypt(input) : baconDecrypt(input);
                break;

            case "Vigenèrova šifra":
                String cleanVigenereKey = extractLetters(keyInput);
                if (cleanVigenereKey.isEmpty()) {
                    outputArea.setText("Zadejte platný textový klíč (písmena A–Z)!");
                    return;
                }
                result = vigenereCipher(input, cleanVigenereKey, encrypt);
                break;

            case "Gronsfeldova šifra":
                if (!keyInput.matches("\\d+")) {
                    outputArea.setText("Zadejte platný číselný klíč (pouze číslice 0–9)!");
                    return;
                }
                result = gronsfeldCipher(input, keyInput, encrypt);
                break;

            case "Autokey šifra":
                String cleanAutokey = extractLetters(keyInput);
                if (cleanAutokey.isEmpty()) {
                    outputArea.setText("Zadejte platný počáteční klíč (písmena A–Z)!");
                    return;
                }
                result = autokeyCipher(input, cleanAutokey, encrypt);
                break;
        }

        outputArea.setText(result);
    }

    // --- POMOCNÉ METODY PRO POSUN A KLÍČ ---

    // Posune jedno písmeno o zadaný počet pozic v anglické abecedě (zachová velikost)
    private char shiftChar(char c, int shift) {
        if (c >= 'A' && c <= 'Z') {
            int offset = (c - 'A' + shift) % 26;
            if (offset < 0) offset += 26;
            return (char) ('A' + offset);
        } else if (c >= 'a' && c <= 'z') {
            int offset = (c - 'a' + shift) % 26;
            if (offset < 0) offset += 26;
            return (char) ('a' + offset);
        }
        return c;
    }

    // Vyfiltruje z klíče pouze písmena A-Z a převede je na velká písmena
    private String extractLetters(String key) {
        StringBuilder clean = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            char c = Character.toUpperCase(key.charAt(i));
            if (c >= 'A' && c <= 'Z') {
                clean.append(c);
            }
        }
        return clean.toString();
    }

    // --- CAESAROVA ŠIFRA ---
    private String caesarCipher(String text, int shift) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            result.append(shiftChar(text.charAt(i), shift));
        }
        return result.toString();
    }

    // --- ATBASH ŠIFRA ---
    private String atbashCipher(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                result.append((char) ('Z' - (c - 'A')));
            } else if (c >= 'a' && c <= 'z') {
                result.append((char) ('z' - (c - 'a')));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    // --- BACONOVA ŠIFRA ---
    private String baconEncrypt(String text) {
        StringBuilder result = new StringBuilder();
        text = text.toUpperCase();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                int index = c - 'A';
                result.append(baconCodes[index]).append(" ");
            } else if (c == ' ') {
                result.append("  ");
            }
        }
        return result.toString().trim();
    }

    private String baconDecrypt(String text) {
        StringBuilder result = new StringBuilder();
        StringBuilder clean = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = Character.toUpperCase(text.charAt(i));
            if (c == 'A' || c == 'B') {
                clean.append(c);
            }
        }

        for (int i = 0; i + 4 < clean.length(); i += 5) {
            String block = clean.substring(i, i + 5);
            for (int j = 0; j < baconCodes.length; j++) {
                if (baconCodes[j].equals(block)) {
                    result.append((char) ('A' + j));
                    break;
                }
            }
        }
        return result.toString();
    }

    // --- VIGENÈROVA ŠIFRA ---
    private String vigenereCipher(String text, String key, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        int keyIndex = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                char keyChar = key.charAt(keyIndex % key.length());
                int shift = keyChar - 'A';
                if (!encrypt) {
                    shift = -shift;
                }
                result.append(shiftChar(c, shift));
                keyIndex++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    // --- GRONSFELDOVA ŠIFRA ---
    private String gronsfeldCipher(String text, String numericKey, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        int keyIndex = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                int shift = numericKey.charAt(keyIndex % numericKey.length()) - '0';
                if (!encrypt) {
                    shift = -shift;
                }
                result.append(shiftChar(c, shift));
                keyIndex++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    // --- AUTOKEY ŠIFRA ---
    private String autokeyCipher(String text, String initialKey, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        StringBuilder keyStream = new StringBuilder(initialKey);
        int keyIndex = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                char keyChar = keyStream.charAt(keyIndex);
                int shift = keyChar - 'A';

                if (encrypt) {
                    char encryptedChar = shiftChar(c, shift);
                    result.append(encryptedChar);
                    // Při šifrování se do proudu klíče přidává původní písmeno otevřeného textu
                    keyStream.append(Character.toUpperCase(c));
                } else {
                    char decryptedChar = shiftChar(c, -shift);
                    result.append(decryptedChar);
                    // Při dešifrování se do proudu klíče přidává právě rozšifrované písmeno
                    keyStream.append(Character.toUpperCase(decryptedChar));
                }
                keyIndex++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}