package org.example;

import org.jetbrains.annotations.NotNull;

import java.util.*;


public class SudokuSolver {

    private static final int SIZE = 9;
    private static int[][] grid;

    public SudokuSolver() {
    }

    public static int[][] getGrid() {
        return grid;
    }

    public static void setGrid(int[][] grid) {
        SudokuSolver.grid = grid;
    }

    public SudokuSolver(int[][] grid) {
        SudokuSolver.grid = grid;
    }

    public static void main(String[] args) {
        grid = new int[][]{
                {4, 0, 0, 0, 0, 0, 0, 6, 9},
                {0, 0, 3, 2, 0, 0, 0, 8, 1},
                {0, 0, 0, 6, 0, 0, 4, 0, 0},
                {1, 5, 7, 0, 0, 0, 6, 9, 0},
                {0, 0, 0, 0, 7, 0, 8, 0, 2},
                {2, 0, 4, 0, 1, 6, 0, 0, 0},
                {5, 0, 0, 0, 0, 3, 7, 2, 8},
                {6, 0, 2, 8, 9, 4, 0, 3, 0},
                {8, 3, 1, 5, 2, 0, 0, 4, 0},
        };
        for (int[] ints : solve()) {
            System.out.println(Arrays.toString(ints));
        }
    }

    public static int[][] solve(int[][] inputGrid) {
        setGrid(inputGrid);
        return solve();  // wywołuje oryginalną metodę solve()
    }

    /**
     * solving the sudoku puzzle
     * and checking if the puzzle has multiple solutions
     *
     * @return the solved puzzle
     */
    public static int[][] solve() {
        Set<String> solutions = new HashSet<>();
        int[][] solution = null;
        for (int i = 0; i < 6; i++) {
            solution = solveSinglePuzlle();
            solutions.add(Arrays.deepToString(solution));
            if (solutions.size() > 1) {
                throw new IllegalArgumentException("Multiple solutions found");
            }
        }
        System.out.println(solutions.size());
        return solution;
    }


    /**
     * making a copy of the grid to test the possibilities
     * and then inserting the numbers in the grid
     * until the puzzle is solved
     * after filling one cell, the possibilities are recalculated
     * after filling the grid, it is validated
     *
     * @return the solved puzzle
     */
    public static int[][] solveSinglePuzlle() {
        int[][] testGrid = new int[SIZE][SIZE];

        boolean solved = false;
        int counter = 0;
        solving:
        while (!solved) {
            resetGrid(grid, testGrid);
            Map<String, List<Integer>> possibilities = possibilities(testGrid);
            while (!possibilities.isEmpty()) {
                for (Map.Entry<String, List<Integer>> entry : possibilities.entrySet()) {
                    //System.out.println("Posibilities size: " + possibilities.size());
                    try {
                        randomInsertion(entry, testGrid);
                    } catch (IllegalArgumentException e) {
                        //System.out.println("dodaje do countera");
                        counter++;
                        if (counter > 2500) {
                            throw new IllegalArgumentException("Unsolvable or invalid sudoku puzzle");
                        } else {
                            //System.out.println("No possibilities left");
                            continue solving;
                        }
                    }
//                    try {
//                        Thread.sleep(1000); // Adjust sleep if needed for logging
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println(Arrays.deepToString(testGrid)); // For debugging
                    break; // Insert one record and break to recreate the map
                }
                possibilities = possibilities(testGrid);
            }
            solved = validate(testGrid);
            //System.out.println("Solved: " + solved);

        }
        return testGrid;
    }

    private static void resetGrid(int[][] source, int[][] destination) {
        for (int i = 0; i < SIZE; i++) {
            try {
                System.arraycopy(source[i], 0, destination[i], 0, SIZE);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Invalid sudoku puzzle. Please check values and array lengths.");
            }
        }
    }

    /**
     * validating the whole grid
     *
     * @return true if the grid is valid
     */
    public static boolean validate(int[][] testGrid) {
        for (int j = 0; j < testGrid.length; j++) {
            if (!validate(testGrid, j)) return false;
        }
        return true;
    }


    /**
     * validating the rows and columns
     *
     * @return true if the row and column are valid
     */
    public static boolean validate(int[][] testGrid, int arrayIndex) {
        int[] sortedArrayHorizontally = Arrays.stream(testGrid[arrayIndex]).sorted().toArray();
        Set<Integer> column = new HashSet<>();
        for (int i = 0; i < sortedArrayHorizontally.length; i++) {
            column.add(testGrid[i][arrayIndex]);
            if (sortedArrayHorizontally[i] != i + 1) return false;
        }
        return column.size() == SIZE;
    }

    /**
     * checking the possibilities for each cell
     *
     * @return a map with the indexes as a string and the possible numbers in array
     */
    public static Map<String, List<Integer>> possibilities(int[][] grid) {
        int[][] newGrid = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(grid[i], 0, newGrid[i], 0, SIZE);
        }
        Map<String, List<Integer>> indexesToFill = new TreeMap<>();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (newGrid[i][j] == 0) {
                    List<Integer> availableNumbers = getIntegers(newGrid, i, j);
                    indexesToFill.put(i + "," + j, availableNumbers);
                }
            }
        }

        findUniqueIn3x3(indexesToFill);

        // Sort the map by the length of the value lists
        List<Map.Entry<String, List<Integer>>> entries = new ArrayList<>(indexesToFill.entrySet());

        //shuffle the entries to get a random order fpr validating multi solutions
        Collections.shuffle(entries);
        entries.sort(Comparator.comparingInt(entry -> entry.getValue().size()));


        Map<String, List<Integer>> sortedIndexesToFill = new LinkedHashMap<>();
        for (Map.Entry<String, List<Integer>> entry : entries) {
            sortedIndexesToFill.put(entry.getKey(), entry.getValue());
        }

        //sortedIndexesToFill.forEach((k, v) -> System.out.println(k + " : " + v + " "));
        //sout the first record
        //System.out.println("First record: " + sortedIndexesToFill.entrySet().iterator().next());


        //System.out.println("----------------------");

        if (sortedIndexesToFill.size() >= 65) {
            throw new IllegalArgumentException("Unsolvable sudoku, too many empty cells.");
        }
        return sortedIndexesToFill;
    }

    /**
     * getting the available numbers for each cell
     * searching the row, column and the 3x3 box
     *
     * @return a list of available numbers
     */
    private static @NotNull List<Integer> getIntegers(int[][] newGrid, int i, int j) {
        List<Integer> availableNumbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        for (int k = 0; k < SIZE; k++) {
            if (newGrid[i][k] != 0) {
                availableNumbers.remove(Integer.valueOf(newGrid[i][k]));
            }
            if (newGrid[k][j] != 0) {
                availableNumbers.remove(Integer.valueOf(newGrid[k][j]));
            }
        }
        //checking the 3x3 box
        int boxRow = i - i % 3;
        int boxColumn = j - j % 3;
        for (int l = boxRow; l < boxRow + 3; l++) {
            for (int m = boxColumn; m < boxColumn + 3; m++) {
                if (newGrid[l][m] != 0) {
                    availableNumbers.remove(Integer.valueOf(newGrid[l][m]));
                }
            }
        }
        return availableNumbers;
    }

    /**
     * inserting the numbers in the grid
     * starting with the cells with only one possibility
     */
    private static void randomInsertion(Map.Entry<String, List<Integer>> possibility, int[][] testGrid) {
        String[] indexes = possibility.getKey().split(",");
        int i = Integer.parseInt(indexes[0]);
        int j = Integer.parseInt(indexes[1]);
        List<Integer> availableNumbers = possibility.getValue();
        int size = availableNumbers.size();

        if (size == 1) {
            testGrid[i][j] = availableNumbers.stream().findFirst().get();
            //System.out.println("Only one possibility: " + availableNumbers.getFirst() + " at " + i + "," + j);
            //System.out.println("Inserted: " + testGrid[i][j] + " at " + i + "," + j);
        } else {
            //System.out.println("possibilities: " + availableNumbers);
            Random random = new Random();
            int randomIndex = random.nextInt(availableNumbers.size());
            testGrid[i][j] = availableNumbers.get(randomIndex);
            //System.out.println("Inserted: " + testGrid[i][j] + " at " + i + "," + j);
        }
    }

    /**
     * checking for only one possibility in 3x3 grid
     * and updating row in the map with the unique number
     */
    private static void findUniqueIn3x3(Map<String, List<Integer>> indexesToFill) {
        for (int boxRow = 0; boxRow < SIZE; boxRow += 3) {
            for (int boxCol = 0; boxCol < SIZE; boxCol += 3) {
                Map<Integer, List<String>> numberPositions = new HashMap<>();
                for (int i = boxRow; i < boxRow + 3; i++) {
                    for (int j = boxCol; j < boxCol + 3; j++) {
                        String key = i + "," + j;
                        List<Integer> numbers = indexesToFill.get(key);
                        if (numbers != null) {
                            for (Integer number : numbers) {
                                numberPositions.putIfAbsent(number, new ArrayList<>());
                                numberPositions.get(number).add(key);
                            }
                        }
                    }
                }
                for (Map.Entry<Integer, List<String>> entry : numberPositions.entrySet()) {
                    if (entry.getValue().size() == 1) {
                        String uniquePosition = entry.getValue().get(0);
                        indexesToFill.put(uniquePosition, Collections.singletonList(entry.getKey()));
                        //System.out.println("Unique in 3x3: " + entry.getKey() + " at " + uniquePosition);
                    }
                }
            }
        }
    }
}
