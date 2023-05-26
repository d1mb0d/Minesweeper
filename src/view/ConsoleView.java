package view;

import game.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class ConsoleView implements View {
    private GameSettings gameSettings;
    private GameModel gameModel;
    private Calculations calculations;
    private Cell[][] cells;
    private View view;

    public ConsoleView(Cell[][] cells, GameModel gameModel, GameSettings gameSettings, Calculations calculations) {
        this.cells = cells;
        this.gameModel = gameModel;
        this.gameSettings = gameSettings;
        this.calculations = calculations;
    }

    @Override
    public void displayField(Cell[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                if (cell.isRevealed()) {
                    if (cell.hasMine()) {
                        System.out.print("X ");
                    } else {
                        int adjacentMines = cell.getAdjacentMines();
                        System.out.print(adjacentMines + " ");
                    }
                } else {
                    System.out.print("- ");
                }
            }
            System.out.println();
        }
    }

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void promptForAction() {
        while (true) {
            displayField(cells);
            System.out.print("Enter row and column (e.g., 0 1), or enter 'M' to open the menu: ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("M")) {
                createMenu();
            } else {
                try {
                    String[] coordinates = input.split(" ");
                    int row = Integer.parseInt(coordinates[0]);
                    int column = Integer.parseInt(coordinates[1]);
                    handleUserAction(row, column, this);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Invalid input! Please enter valid row and column numbers.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter valid row and column numbers.");
                }
            }
        }
    }

    @Override
    public void showAboutDialog() {
        System.out.println("Minesweeper Game\n\n");
        System.out.println("Created by: Dmitriy Bodrov\n" );
        System.out.println("Version: 1.0\n");
        System.out.println("Date: 26.05.2023");
    }

    @Override
    public void showErrorDialog(String errorMessage) {
        System.out.println("Error: " + errorMessage);
    }

    @Override
    public void showSettingsDialog(GameSettings gameSettings) {
        boolean validInput = false;

        do {
            System.out.print("Enter the number of rows: ");
            Scanner scanner = new Scanner(System.in);
            int rows = scanner.nextInt();
            System.out.print("Enter the number of columns: ");
            int columns = scanner.nextInt();
            System.out.print("Enter the number of mines: ");
            int numMines = scanner.nextInt();

            if (calculations.isValidInput(rows, columns, numMines)) {
                gameSettings.setFieldSizeRows(rows);
                gameSettings.setFieldSizeColumns(columns);
                gameSettings.setNumMines(numMines);
                validInput = true;
            }
            else {
                displayMessage("Invalid input! Please enter valid numeric values.");
            }
        } while (!validInput);
        this.gameModel.reset();
        GameModel gameModel = new GameModel(gameSettings);
        gameSettings.setViewMode(ViewMode.TEXT);
        view = createView(gameSettings.getViewMode(), gameModel.getField().getCells(), gameModel, gameSettings);
    }

    public void showNewGameDialog() {
        System.out.print("Starting a new game with the same parameters:\n");
        gameModel.reset();
        gameSettings.setViewMode(ViewMode.TEXT);
        view = createView(gameSettings.getViewMode(), gameModel.getField().getCells(), gameModel, gameSettings);
    }

    @Override
    public void createMenu() {
        System.out.println("Menu:");
        System.out.println("1. New Game");
        System.out.println("2. About");
        System.out.println("3. Settings");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
        Scanner scanner = new Scanner(System.in);
        try {
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    showNewGameDialog();
                    break;
                case 2:
                    showAboutDialog();
                    return;
                case 3:
                    showSettingsDialog(gameSettings);
                    break;
                case 4:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice!");
                    break;
            }
            view.promptForAction();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input! Please enter a valid integer value.");
        }
    }

    public static View createView(ViewMode viewMode, Cell[][] cells, GameModel gameModel, GameSettings gameSettings) {
        if (viewMode == ViewMode.TEXT) {
            return new ConsoleView(cells, gameModel, gameSettings, new Calculations(cells));
        } else {
            return new GraphicalView(cells, gameModel, gameSettings, new Calculations(cells));
        }
    }

    public void handleUserAction(int row, int column, View view) {
        ActionListener listener = new CellButtonListener(row, column, cells, view);
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "click");
        listener.actionPerformed(event);

        if (cells[row][column].getAdjacentMines() == 0) {
            calculations.expandAreaWithoutMines(row, column);
        }
    }

    private class CellButtonListener implements ActionListener {
        private int row;
        private int column;
        private Cell[][] cells;
        private View view;

        public CellButtonListener(int row, int column, Cell[][] cells, View view) {
            this.row = row;
            this.column = column;
            this.cells = cells;
            this.view = view;
        }

        public void actionPerformed(ActionEvent e) {
            Cell cell = cells[row][column];

            if (!cell.isRevealed()) {
                cell.setRevealed(true);
                calculations.updateAdjacentMineCount(row, column);

                if (cell.hasMine()) {
                    cell.setRevealed(true);
                    view.displayMessage("Game over! You clicked on a mine.");
                    return;
                }
                if (calculations.checkGameWon(cells)) {
                    view.displayMessage("Congratulations! You won the game.");
                }
            }
        }
    }
}
