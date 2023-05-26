package view;

import game.Cell;
import game.GameSettings;

public interface View {
    void displayField(Cell[][] cells);

    void displayMessage(String message);

    void promptForAction();

    void showAboutDialog();

    void showErrorDialog(String errorMessage);

    void showSettingsDialog(GameSettings gameSettings);

    void createMenu();
}
