package view;

import game.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GraphicalView extends Component implements View {
    private JFrame frame;
    private JPanel gamePanel;
    private JButton[][] cellButtons;
    private Cell[][] cells;
    private GameSettings gameSettings;
    private GameModel gameModel;
    private Calculations calculations;
    private View view;
    private JLabel timerLabel;
    private Timer timer;
    private long startTime;
    private Image[] images;
    public GraphicalView(Cell[][] cells, GameModel gameModel, GameSettings gameSettings, Calculations calculations) {
        timer = new Timer(1000, new TimerActionListener());
        timer.start();
        startTime = System.currentTimeMillis();
        int rows = cells.length;
        int columns = cells[0].length;
        this.cells = cells;
        this.gameModel = gameModel;
        this.gameSettings = gameSettings;
        this.calculations = calculations;
        view = this;

        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setResizable(true);

        images = new Image[11];
        ImageIcon[] imageIcons = new ImageIcon[11];
        for (int i = 0; i <= 10; i++) {
            imageIcons[i] = new ImageIcon("src/resources/" + i + ".jpg");
            images[i] = imageIcons[i].getImage().getScaledInstance(frame.getWidth() / (gameSettings.getFieldSizeRows() + 2), frame.getHeight() / (gameSettings.getFieldSizeRows() + 2), Image.SCALE_FAST);
        }

        createMenu();

        gamePanel = new JPanel(new GridLayout(rows, columns));
        cellButtons = new JButton[rows][columns];
        timerLabel = new JLabel("Time: 00:00");

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                JButton button = createCellButton(i, j);
                cellButtons[i][j] = button;
                gamePanel.add(button);
            }
        }
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.add(timerLabel, BorderLayout.NORTH);
        frame.setVisible(true);
    }

    private JButton createCellButton(int i, int j) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(frame.getWidth() / (gameSettings.getFieldSizeRows() + 1), frame.getHeight() / (gameSettings.getFieldSizeRows() + 1)));
        button.addActionListener(e -> handleUserAction(i, j, view));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    handleRightClick(i, j, view);
                }
                else if (SwingUtilities.isLeftMouseButton(e)) {
                    handleUserAction(i, j, view);
                }
            }
        });
        return button;
    }

    @Override
    public void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameMenuItem = new JMenuItem("New Game");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        JMenuItem settingsMenuItem = new JMenuItem("Settings");
        JMenuItem exitMenuItem = new JMenuItem("Exit");

        newGameMenuItem.addActionListener(e -> {
            clearField();
            startTime = System.currentTimeMillis();
            timer.restart();
            gameModel.reset();
            view.displayField(gameModel.getField().getCells());
        });

        aboutMenuItem.addActionListener(e -> view.showAboutDialog());

        settingsMenuItem.addActionListener(e -> view.showSettingsDialog(gameSettings));

        exitMenuItem.addActionListener(e -> System.exit(0));

        gameMenu.add(newGameMenuItem);
        gameMenu.add(aboutMenuItem);
        gameMenu.add(settingsMenuItem);
        gameMenu.add(exitMenuItem);
        menuBar.add(gameMenu);

        frame.setJMenuBar(menuBar);
    }

    @Override
    public void displayField(Cell[][] cells) {
        this.cells = cells;

        int rows = cells.length;
        int columns = cells[0].length;

        ActionListener[] actionListeners = {revealActionListener, flagActionListener};

        ImageIcon[] icons = new ImageIcon[11];
        for (int i = 0; i <= 10; i++) {
            icons[i] = new ImageIcon(images[i]);
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                JButton button = cellButtons[i][j];
                Cell cell = cells[i][j];

                button.removeActionListener(revealActionListener);
                button.removeActionListener(flagActionListener);

                if (cell.isRevealed()) {
                    button.addActionListener(actionListeners[0]);

                    if (cell.hasMine()) {
                        button.setIcon(icons[9]);
                    } else {
                        int adjacentMines = cell.getAdjacentMines();
                        button.setIcon(icons[adjacentMines]);
                    }
                } else if (cell.isFlagged()) {
                    button.addActionListener(actionListeners[1]);
                    button.setIcon(icons[10]);
                } else {
                    button.addActionListener(actionListeners[1]);
                    button.setIcon(null);
                }
            }
        }
        SwingUtilities.invokeLater(() -> frame.revalidate());
    }

    ActionListener revealActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            int rows = gameSettings.getFieldSizeRows();
            int columns = gameSettings.getFieldSizeColumns();
            int row = -1;
            int col = -1;

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (button == cellButtons[i][j]) {
                        row = i;
                        col = j;
                        break;
                    }
                }
            }

            if (row != -1 && col != -1) {
                Cell cell = cells[row][col];
                cell.setRevealed(true);

                if (cell.getAdjacentMines() == 0) {
                    calculations.expandAreaWithoutMines(row, col);
                }
            }
        }
    };

    ActionListener flagActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            int rows = gameSettings.getFieldSizeRows();
            int columns = gameSettings.getFieldSizeColumns();
            int row = -1;
            int col = -1;

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (button == cellButtons[i][j]) {
                        row = i;
                        col = j;
                        break;
                    }
                }
            }

            if (row != -1 && col != -1) {
                Cell cell = cells[row][col];
                boolean isFlagged = cell.isFlagged();
                cell.setFlagged(!isFlagged);
                displayField(cells);
            }
        }
    };

    @Override
    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    @Override
    public void promptForAction() {
    }

    @Override
    public void showAboutDialog() {
        String message = "Minesweeper Game\n\n" +
                "Created by: Dmitriy Bodrov\n" +
                "Version: 1.0\n" +
                "Date: 26.05.2023";
        JOptionPane.showMessageDialog(frame, message, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showErrorDialog(String errorMessage) {
    }

    @Override
    public void showSettingsDialog(GameSettings gameSettings) {
        boolean validInput = false;

        do {
            String rows = JOptionPane.showInputDialog(frame, "Enter the number of rows:");
            String columns = JOptionPane.showInputDialog(frame, "Enter the number of columns:");
            String numMines = JOptionPane.showInputDialog(frame, "Enter the number of mines:");

            try {
                int parsedRows = Integer.parseInt(rows);
                int parsedColumns = Integer.parseInt(columns);
                int parsedNumMines = Integer.parseInt(numMines);

                if (calculations.isValidInput(parsedRows, parsedColumns, parsedNumMines)) {
                    gameSettings.setFieldSizeRows(parsedRows);
                    gameSettings.setFieldSizeColumns(parsedColumns);
                    gameSettings.setNumMines(parsedNumMines);
                    validInput = true;
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid input! Please enter valid numeric values.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid input! Please enter valid numeric values.");
            }
        } while (!validInput);
        clearField();
        gameModel.reset();

        try {
            GameModel gameModel = new GameModel(gameSettings);
            gameSettings.setViewMode(ViewMode.GRAPHICAL);
            view = createView(gameSettings.getViewMode(), gameModel.getField().getCells(), gameModel, gameSettings);
            frame.dispose();
            displayField(cells);
        } catch (NullPointerException ex) {
            showErrorDialog("Null pointer exception occurred.");
        }
    }

    public static View createView(ViewMode viewMode, Cell[][] cells, GameModel gameModel, GameSettings gameSettings) {
        if (viewMode == ViewMode.TEXT) {
            return new ConsoleView(cells, gameModel, gameSettings, new Calculations(cells));
        } else {
            return new GraphicalView(cells, gameModel, gameSettings, new Calculations(cells));
        }
    }

    public void handleRightClick(int row, int column, View view) {
        Cell cell = cells[row][column];
        boolean isFlagged = cell.isFlagged();
        cell.setFlagged(!isFlagged);
        view.displayField(cells);
    }

    public void handleUserAction(int row, int column, View view) {
        ActionListener listener = new GraphicalView.CellButtonListener(row, column, cells, view);
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "click");
        listener.actionPerformed(event);

        if (cells[row][column].hasMine()) {
            timer.stop();
            blockButtons();
            return;
        }

        if (cells[row][column].getAdjacentMines() == 0) {
            calculations.expandAreaWithoutMines(row, column);
        }

        if (Calculations.checkGameWon(cells)) {
            timer.stop();
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            String playerName = JOptionPane.showInputDialog(frame, "Enter your name:");
            int score = Calculations.calculateHighScore(gameSettings.getNumMines(), gameSettings.getFieldSizeRows(), gameSettings.getFieldSizeColumns(), (int) (elapsedTime / 1000));
            saveRecord(playerName, score);
            blockButtons();
            view.displayMessage("Congratulations! You won the game!");
            return;
        }
        view.displayField(cells);
    }
    private class TimerActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String formattedTime = Calculations.updateTimerDisplay(startTime);
            timerLabel.setText(formattedTime);
        }
    }

    public void saveRecord(String playerName, int timeInSeconds) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("src\\records.txt", true))) {
            writer.println(playerName + "," + timeInSeconds);
        } catch (IOException e) {
            System.out.println("Error saving record: " + e.getMessage());
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
                    view.displayMessage("Game over! You clicked on a mine.");
                    return;
                }

                view.displayField(cells);
            }
        }
    }

    private void clearField() {
        int rows = cells.length;
        int columns = cells[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                JButton button = cellButtons[i][j];

                button.setEnabled(true);
                button.removeActionListener(revealActionListener);
                button.removeActionListener(flagActionListener);
                button.setText("");
                button.setBackground(null);
                button.setIcon(null);
            }
        }
    }

    private void blockButtons() {
        int rows = cells.length;
        int columns = cells[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                JButton button = cellButtons[i][j];
                cells[i][j].setRevealed(true);
                button.setEnabled(false);
            }
        }
    }
}
