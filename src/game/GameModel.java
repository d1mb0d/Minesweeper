package game;

public class GameModel {
    private Field field;

    public GameModel(GameSettings gameSettings) {
        int rows = gameSettings.getFieldSizeRows();
        int columns = gameSettings.getFieldSizeColumns();
        int numMines = gameSettings.getNumMines();
        field = new Field(rows, columns, numMines);
    }

    public Field getField() {
        return field;
    }

    public void reset() {
        field.reset();
    }
}
