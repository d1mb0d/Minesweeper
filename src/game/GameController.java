package game;

import view.*;
import java.util.Scanner;

public class GameController {
    private View view;

    public int chooseViewMode() {
        System.out.println("Choose the game view mode:");
        System.out.println("1. Text Version");
        System.out.println("2. Graphical Version");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        int choice = Calculations.readNumberInRange(input, 1, 2, 10);
        return choice;
    }

    public void startGame() {
        int rows = 9;
        int columns = 9;
        int mines = 10;
        GameSettings gameSettings = new GameSettings(rows, columns, mines);
        GameModel gameModel = new GameModel(gameSettings);

        int type = chooseViewMode();
        try {
            if (type == 1) {
                gameSettings.setViewMode(ViewMode.TEXT);
                view = ConsoleView.createView(gameSettings.getViewMode(), gameModel.getField().getCells(), gameModel, gameSettings);
            } else if (type == 2) {
                gameSettings.setViewMode(ViewMode.GRAPHICAL);
                view = GraphicalView.createView(gameSettings.getViewMode(), gameModel.getField().getCells(), gameModel, gameSettings);
            }
        } catch (NullPointerException e) {
            view.showErrorDialog("An error occurred: " + e.getMessage());
        }

        view.promptForAction();
    }
}
