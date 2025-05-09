package org.example;
import org.example.hint.Hint;
import org.example.hint.HintManager;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.Optional;

public class SudokuGUI {
    private int totalBlanks;
    private int correctCount;

    // Filtr pozwalający wpisać tylko jedną cyfrę 1–9
    static class DigitFilter extends DocumentFilter {
        @Override public void insertString(FilterBypass fb, int offset, String str, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
            if (str == null) return;
            if (fb.getDocument().getLength() + str.length() > 1) return;
            StringBuilder buf = new StringBuilder();
            for (char c : str.toCharArray()) if (Character.isDigit(c) && c != '0') buf.append(c);
            super.insertString(fb, offset, buf.toString(), attr);
        }
        @Override public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
            if (text == null) return;
            if (fb.getDocument().getLength() - length + text.length() > 1) return;
            StringBuilder buf = new StringBuilder();
            for (char c : text.toCharArray()) if (Character.isDigit(c) && c != '0') buf.append(c);
            super.replace(fb, offset, length, buf.toString(), attrs);
        }
    }

    /** Tworzy GUI na podstawie przekazanej planszy i rozwiązania */
    public void createAndShowGUI(int[][] PUZZLE, int[][] SOLUTION) {
        if (PUZZLE == null || SOLUTION == null) {
            throw new IllegalArgumentException("PUZZLE i SOLUTION muszą być ustawione przed uruchomieniem GUI.");
        }

        // Obliczamy liczbę pustych pól
        totalBlanks = 0;
        correctCount = 0;
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                if (PUZZLE[i][j] == 0) totalBlanks++;

        JFrame frame = new JFrame("Sudoku");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null); // centrowanie

        JPanel gridPanel = new JPanel(new GridLayout(9, 9));
        gridPanel.setBackground(Color.BLACK);

        HintManager hintManager = new HintManager("hints.json");

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                JTextField cell = new JTextField();
                cell.setHorizontalAlignment(JTextField.CENTER);
                cell.setFont(new Font("SansSerif", Font.BOLD, 20));
                cell.setBackground(Color.WHITE);
                cell.setForeground(Color.BLACK);

                int top    = (i % 3 == 0) ? 2 : 1;
                int left   = (j % 3 == 0) ? 2 : 1;
                int bottom = (i == 8)      ? 2 : 1;
                int right  = (j == 8)      ? 2 : 1;
                cell.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.GRAY));

                if (PUZZLE[i][j] != 0) {
                    cell.setText(String.valueOf(PUZZLE[i][j]));
                    cell.setEditable(false);
                    cell.setBackground(Color.LIGHT_GRAY);
                } else {
                    final int row = i, col = j;
                    ((AbstractDocument) cell.getDocument()).setDocumentFilter(new DigitFilter());
                    cell.getDocument().addDocumentListener(new DocumentListener() {
                        private void validateCell() {
                            String text = cell.getText().trim();
                            if (text.length() == 1) {
                                int num = Integer.parseInt(text);
                                if (num == SOLUTION[row][col]) {
                                    cell.setForeground(Color.GREEN);
                                    cell.setEditable(false);
                                    correctCount++;
                                    if (correctCount == totalBlanks) {
                                        JOptionPane.showMessageDialog(frame, "Gratulacje! Ukończyłeś Sudoku!");
                                        System.exit(0);
                                    }
                                } else {
                                    cell.setForeground(Color.RED);
                                    JOptionPane.showMessageDialog(
                                            frame,
                                            "Liczba została wprowadzona niepoprawnie.",
                                            "Błąd",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                    Optional<Hint> optHint = hintManager.getRandomHint(SOLUTION[row][col]);
                                    if (optHint.isPresent()) {
                                        JOptionPane.showMessageDialog(
                                                frame,
                                                optHint.get().getText(),
                                                "Podpowiedź",
                                                JOptionPane.INFORMATION_MESSAGE
                                        );
                                    } else {
                                        JOptionPane.showMessageDialog(
                                                frame,
                                                "Brak więcej podpowiedzi dla liczby rozwiązania.",
                                                "Brak podpowiedzi",
                                                JOptionPane.INFORMATION_MESSAGE
                                        );
                                    }
                                }
                            }
                        }
                        @Override public void insertUpdate(DocumentEvent e) { validateCell(); }
                        @Override public void removeUpdate(DocumentEvent e) { }
                        @Override public void changedUpdate(DocumentEvent e) { validateCell(); }
                    });
                }
                gridPanel.add(cell);
            }
        }

        frame.add(gridPanel);
        frame.setVisible(true);
    }
}
