package game;

import view.ViewMode;

public class GameSettings {
    private int fieldSizeRows;
    private int fieldSizeColumns;
    private int numMines;
    private ViewMode viewMode;

    public GameSettings(int rows, int columns, int numMines) {
        setFieldSizeRows(rows);
        setFieldSizeColumns(columns);
        setNumMines(numMines);
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    public int getFieldSizeRows() {
        return fieldSizeRows;
    }

    public void setFieldSizeRows(int fieldSizeRows) {
        this.fieldSizeRows = fieldSizeRows;
    }

    public int getFieldSizeColumns() {
        return fieldSizeColumns;
    }

    public void setFieldSizeColumns(int fieldSizeColumns) {
        this.fieldSizeColumns = fieldSizeColumns;
    }

    public int getNumMines() {
        return numMines;
    }

    public void setNumMines(int numMines) {
        this.numMines = numMines;
    }
}
