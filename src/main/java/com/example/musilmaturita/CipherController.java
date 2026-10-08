package com.example.musilmaturita;

import javafx.collections.FXCollections;
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

    private String[] baconCodes = {
            "AAAAA", "AAAAB", "AAABA", "AAABB", "AABAA", "AABAB", "AABBA", "AABBB",
            "ABAAA", "ABAAB", "ABABA", "ABABB", "ABBAA", "ABBAB", "ABBBA", "ABBBB",
            "BAAAA", "BAAAB", "BAABA", "BAABB", "BABAA", "BABAB", "BABBA", "BABBB",
            "BBAAA", "BBAAB"
    };

    @FXML
    public void initialize() {
        cipherSelector.getItems().add("Caesarova šifra");
        cipherSelector.getItems().add("Atbash");
        cipherSelector.getItems().add("Baconova šifra");
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

        if (input == null || input.isEmpty()) {
            outputArea.setText("");
            return;
        }

        String result = "";

        if (selectedCipher.equals("Caesarova šifra")) {
            int shift;
            try {
                shift = Integer.parseInt(keyField.getText().trim());
            } catch (Exception e) {
                outputArea.setText("Zadejte platné číslo pro posun!");
                return;
            }

            if (!encrypt) {
                shift = -shift;
            }
            result = caesarCipher(input, shift);

        } else if (selectedCipher.equals("Atbash")) {
            result = atbashCipher(input);

        } else if (selectedCipher.equals("Baconova šifra")) {
            if (encrypt) {
                result = baconEncrypt(input);
            } else {
                result = baconDecrypt(input);
            }
        }

        outputArea.setText(result);
    }

    // --- CAESAROVA ŠIFRA ---
    private String caesarCipher(String text, int shift) {
        String result = "";

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c >= 'A' && c <= 'Z') {
                char shifted = (char) (c + shift);

                while (shifted > 'Z') shifted -= 26;
                while (shifted < 'A') shifted += 26;
                result += shifted;

            } else if (c >= 'a' && c <= 'z') {
                char shifted = (char) (c + shift);
                while (shifted > 'z') shifted -= 26;
                while (shifted < 'a') shifted += 26;
                result += shifted;

            } else {
                result += c;
            }
        }
        return result;
    }

    // --- ATBASH ŠIFRA ---
    private String atbashCipher(String text) {
        String result = "";

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c >= 'A' && c <= 'Z') {
                result += (char) ('Z' - (c - 'A'));
            } else if (c >= 'a' && c <= 'z') {
                result += (char) ('z' - (c - 'a'));
            } else {
                result += c;
            }
        }
        return result;
    }

    // --- BACONOVA ŠIFRA ---
    private String baconEncrypt(String text) {
        String result = "";
        text = text.toUpperCase();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c >= 'A' && c <= 'Z') {
                int index = c - 'A';
                result += baconCodes[index] + " ";
            } else if (c == ' ') {
                result += "  ";
            }
        }
        return result.trim();
    }

    private String baconDecrypt(String text) {
        String result = "";

        String clean = "";
        for (int i = 0; i < text.length(); i++) {
            char c = Character.toUpperCase(text.charAt(i));
            if (c == 'A' || c == 'B') {
                clean += c;
            }
        }

        for (int i = 0; i + 4 < clean.length(); i += 5) {
            String block = clean.substring(i, i + 5);

            for (int j = 0; j < baconCodes.length; j++) {
                if (baconCodes[j].equals(block)) {
                    result += (char) ('A' + j);
                    break;
                }
            }
        }
        return result;
    }
}