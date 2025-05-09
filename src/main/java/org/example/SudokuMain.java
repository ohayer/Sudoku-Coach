package org.example;

import org.example.api.Grid;
import org.example.api.SudokuApiClient;

import javax.swing.*;
import java.util.Arrays;
import java.util.Optional;

public class SudokuMain {
    /**
     * Główna klasa aplikacji Sudoku.
     * <p>
     * Klasa ta jest odpowiedzialna za uruchomienie aplikacji Sudoku, pobranie planszy z API oraz
     * wyświetlenie GUI.
     */
    public static void main(String[] args) {
        SudokuApiClient client = new SudokuApiClient();
        try {
            Optional<Grid> maybeGrid = client.fetchNewBoard();
            if (maybeGrid.isPresent()) {
                Grid grid = maybeGrid.get();
                System.out.println("Difficulty: " + grid.getDifficulty());
                System.out.println("Puzzle: " + Arrays.deepToString(grid.getValue()));

//          Przypisanie gridów do Sudoku
                SudokuGUI gui = new SudokuGUI();

//            Moje rozwiązanie sudoku
                SudokuSolver solver = new SudokuSolver();
                solver.setGrid(grid.getValue());

                //Jeśli rozwiązanie jest poprawne, to zapisuje je do solvera
                try {
                    int[][] mySolution = SudokuSolver.solve();
                    grid.setSolution(mySolution);
                } catch (IllegalArgumentException e) {
                    System.out.println("Niepoprawne rozwiązanie sudoku moją metodą, używam rozwiązania z api. " + e.getMessage());
                }


//            Tworzenie GUI
                SwingUtilities.invokeLater(() -> gui.createAndShowGUI(grid.getValue(), grid.getSolution()));
            } else {
                System.err.println("Brak planszy w odpowiedzi API.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}