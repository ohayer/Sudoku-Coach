package org.example.hint;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HintDialog {
    public static void showHints(Component parent, List<Hint> hintsForNumber) {
        if (hintsForNumber.isEmpty()) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Brak dostępnych wskazówek dla tej liczby.",
                    "Brak wskazówek",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String[] titles = hintsForNumber.stream()
                .map(Hint::getTitle)
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(
                parent,
                "Wybierz wskazówkę:",
                "Wskazówki",
                JOptionPane.PLAIN_MESSAGE,
                null,
                titles,
                titles[0]
        );

        if (selected != null) {
            hintsForNumber.stream()
                    .filter(h -> h.getTitle().equals(selected))
                    .findFirst()
                    .ifPresent(h -> JOptionPane.showMessageDialog(
                            parent,
                            h.getText(),
                            h.getTitle(),
                            JOptionPane.INFORMATION_MESSAGE
                    ));
        }
    }
}
