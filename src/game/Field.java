package game;

import java.util.Random;

public class Field {
    private int rows;
    private int columns;
    private int numMines;
    private Cell[][] cells;

    public Field(int rows, int columns, int numMines) {
        this.rows = rows;
        this.columns = columns;
        this.numMines = numMines;
        cells = new Cell[rows][columns];

        initializeCells();
        try {
            placeMines();
        } catch (Exception e) {
            System.out.println("An error occurred while placing mines: " + e.getMessage());
        }
    }

    public Cell[][] getCells() {
        return cells;
    }

    private void initializeCells() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    private void placeMines() throws Exception {
        if (numMines > rows * columns) {
            throw new Exception("Number of mines exceeds the available cells.");
        }

        Random random = new Random();
        int minesToPlace = numMines;

        while (minesToPlace > 0) {
            int randomRow = random.nextInt(rows);
            int randomColumn = random.nextInt(columns);

            Cell cell = cells[randomRow][randomColumn];

            if (!cell.hasMine()) {
                cell.setHasMine(true);
                minesToPlace--;
            }
        }
    }

    public void reset() {
        initializeCells();

        try {
            placeMines();
        } catch (Exception e) {
            System.out.println("An error occurred while placing mines: " + e.getMessage());
        }

        Calculations.updateAllAdjacentMineCount(cells);
    }
}
