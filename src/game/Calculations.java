package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Calculations {
    private Cell[][] cells;

    public Calculations(Cell[][] cells) {
        this.cells = cells;
    }

    public void updateAdjacentMineCount(int row, int column) {
        int rows = cells.length;
        int columns = cells[0].length;

        for (int i = Math.max(0, row - 1); i <= Math.min(row + 1, rows - 1); i++) {
            for (int j = Math.max(0, column - 1); j <= Math.min(column + 1, columns - 1); j++) {
                Cell neighborCell = cells[i][j];
                if (!neighborCell.hasMine()) {
                    int count = calculateAdjacentMineCount(i, j);
                    neighborCell.setAdjacentMines(count);
                }
            }
        }
    }

    public int calculateAdjacentMineCount(int row, int column) {
        int count = 0;
        int rows = cells.length;
        int columns = cells[0].length;

        for (int i = Math.max(0, row - 1); i <= Math.min(row + 1, rows - 1); i++) {
            for (int j = Math.max(0, column - 1); j <= Math.min(column + 1, columns - 1); j++) {
                if (cells[i][j].hasMine()) {
                    count++;
                }
            }
        }
        return count;
    }

    public void expandAreaWithoutMines(int row, int column) {
        int rows = cells.length;
        int columns = cells[0].length;

        if (row >= 0 && row < rows && column >= 0 && column < columns && cells[row][column].getAdjacentMines() == 0 && !cells[row][column].hasMine()) {
            cells[row][column].setRevealed(true);
            List<Cell> cellsToBeRevealed = new ArrayList<>();
            cellsToBeRevealed.add(cells[row][column]);

            while (!cellsToBeRevealed.isEmpty()) {
                Cell currentCell = cellsToBeRevealed.remove(0);
                List<Cell> neighbors = getCellNeighbors(currentCell);

                for (Cell neighborCell : neighbors) {
                    if (!neighborCell.isRevealed()) {
                        updateAdjacentMineCount(neighborCell.getRow(), neighborCell.getColumn());
                        neighborCell.setRevealed(true);
                        if (neighborCell.getAdjacentMines() == 0) {
                            cellsToBeRevealed.add(neighborCell);
                        }
                    }
                }
            }
        }
    }

    private List<Cell> getCellNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int rows = cells.length;
        int columns = cells[0].length;
        int row = cell.getRow();
        int column = cell.getColumn();

        for (int i = Math.max(0, row - 1); i <= Math.min(row + 1, rows - 1); i++) {
            for (int j = Math.max(0, column - 1); j <= Math.min(column + 1, columns - 1); j++) {
                if (i != row || j != column) {
                    neighbors.add(cells[i][j]);
                }
            }
        }
        return neighbors;
    }

    public static boolean checkGameWon(Cell[][] cells) {
        int rows = cells.length;
        int columns = cells[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Cell cell = cells[i][j];
                if ((cell.hasMine() && cell.isRevealed()) || (!cell.hasMine() && !cell.isRevealed())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int readNumberInRange(String input, int minValue, int maxValue, int maxAttempts) {
        int number = 0;
        int attempts = 0;

        while (attempts < maxAttempts) {
            try {
                number = Integer.parseInt(input);
                if (number < minValue || number > maxValue) {
                    System.out.println("Number is not within the specified range.");
                    attempts++;
                } else {
                    return number;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                attempts++;
            }

            if (attempts < maxAttempts) {
                System.out.println("Attempt " + attempts + "/" + maxAttempts);
                System.out.println("Please enter a valid number:");
                Scanner scanner = new Scanner(System.in);
                input = scanner.nextLine();
            }
        }
        System.out.println("Maximum number of attempts reached. Returning default value: " + number);
        return number;
    }

    public static void updateAllAdjacentMineCount(Cell[][] cells) {
        int rows = cells.length;
        int columns = cells[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Calculations calculations = new Calculations(cells);
                calculations.updateAdjacentMineCount(i, j);
            }
        }
    }

    public static String updateTimerDisplay(long startTime) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        long minutes = (elapsedTime / 1000) / 60;
        long seconds = (elapsedTime / 1000) % 60;

        String formattedTime = String.format("Time: %02d:%02d", minutes, seconds);

        return formattedTime;
    }

    public static int calculateHighScore(int numMines, int numRows, int numColumns, int gameTimeInSeconds) {
        int maxPossibleScore = numMines * 10;
        double timeFactor = (double) gameTimeInSeconds / (numRows * numColumns);
        double timeScore = maxPossibleScore * (1 - timeFactor);
        int highScore = (int) Math.round(timeScore); // Округляем до ближайшего целого значения
        return highScore;
    }

    public boolean isValidInput(int rows, int columns, int numMines) {
        if ((numMines >= rows * columns) || (rows < 0) || (columns < 0) || (numMines < 0) || (rows > cells.length) || (columns > cells[0].length)) {
            return false;
        }
        return true;
    }
}
