package tictactoe;

import java.util.Arrays;
import java.util.Scanner;

public class Game {
    public static final String BAD_PARAMETERS = "Bad parameters!";
    public static final String ENTER_COMMAND = "Enter command: ";
    private char[][] field;
    private boolean gameIsOver = false;
    private String startCommand;
    private String[] startParams = new String[2];
    private String gameResultMessage;
    private Player currentPlayer;
    private char winnerSymbol = ' ';

    public Game() {
        this.field = initializeField();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    private boolean isGameIsOver() {
        return gameIsOver;
    }

    private static char[][] initializeField() {
        char[][] field = new char[3][3];

        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < field[row].length; col++) {
                field[row][col] = ' ';
            }
        }
        return field;
    }

    public void runCommand() {

        Command action;

        try {
            action = Command.valueOf(startCommand.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        switch (action) {
            case START -> startGame();
            case EXIT -> exitGame();
        }
    }

    private void startGame() {
        String[] playerTypes = startParams.clone();

        Player player1 = createPlayer('X', playerTypes[0]);
        Player player2 = createPlayer('O', playerTypes[1]);

        if (player1 == null || player2 == null) return;

        displayField();

        while (!isGameIsOver()) {
            currentPlayer = player1;
            player1.chooseCoordinates(this);
            displayField();
            checkIfGameIsOver();

            if (!isGameIsOver()) {
                currentPlayer = player2;
                player2.chooseCoordinates(this);
                displayField();
                checkIfGameIsOver();
            }
        }
        printGameResult();
    }

    private void exitGame() {
        System.exit(0);
    }

    private void printGameResult() {
        System.out.println(gameResultMessage);
    }

    private void displayField() {
        for (int i = 0; i < 9; i++) {
            System.out.print("-");
        }
        System.out.println();

        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < field[row].length; col++) {
                if (col == 0) {
                    System.out.print("| ");
                }
                System.out.print(field[row][col] + " ");
                if (col == 2) {
                    System.out.print("|");
                }
            }
            System.out.println();
        }

        for (int i = 0; i < 9; i++) {
            System.out.print("-");
        }
        System.out.println();
    }


    public char[][] getField() {
        return field;
    }

    private void checkIfGameIsOver() {
        gameResultMessage = checkGameResult();

        if (gameResultMessage != null && !gameResultMessage.equals("Draw") ) {
            gameResultMessage += " wins";
        }

        gameIsOver = gameResultMessage != null;
    }

    private boolean checkIfPlayerWins(char symbol) {
        if (
                (field[0][0] == symbol && field[0][1] == symbol && field[0][2] == symbol) ||
                        (field[1][0] == symbol && field[1][1] == symbol && field[1][2] == symbol) ||
                        (field[2][0] == symbol && field[2][1] == symbol && field[2][2] == symbol) ||
                        (field[0][0] == symbol && field[1][0] == symbol && field[2][0] == symbol) ||
                        (field[0][1] == symbol && field[1][1] == symbol && field[2][1] == symbol) ||
                        (field[0][2] == symbol && field[1][2] == symbol && field[2][2] == symbol) ||
                        (field[0][0] == symbol && field[1][1] == symbol && field[2][2] == symbol) ||
                        (field[0][2] == symbol && field[1][1] == symbol && field[2][0] == symbol)
        ) {
            return true;
        } else {
            return false;
        }
    }

    public String checkGameResult() {
        char symbol = currentPlayer.getSymbol();
        char enemySymbol = currentPlayer.getSymbol() == 'X' ? 'O' : 'X';

        if (checkIfPlayerWins(symbol)) {
            return String.valueOf(symbol);
        } else if (checkIfPlayerWins(enemySymbol)) {
            return String.valueOf(enemySymbol);
        } else if (this.countEmptyCells() == 0) {
            return "Draw";
        }
        return null;
    }

    private int countEmptyCells() {
        int emptyCellsCounter = 0;
        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < field[row].length; col++) {
                if (field[row][col] == ' ') emptyCellsCounter++;
            }
        }
        return emptyCellsCounter;
    }

    public int[][] getEmptyCells() {
        int emptyCellsCounter = countEmptyCells();

        int[][] emptyCells = new int[emptyCellsCounter][];

        int i = 0;
        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < field[row].length; col++) {
                if (field[row][col] == ' ' && i < emptyCells.length) {
                    emptyCells[i] = new int[]{row, col};
                    i++;
                }
            }
        }
        return emptyCells;
    }

    public static String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }


    public void getCommand() {
        System.out.print(ENTER_COMMAND);
        validateFormat(getUserInput());
    }

    private void validateFormat(String userInput) {

        try {
            String[] commandParams = userInput.split(" ");
            String command = commandParams[0];
            String[] params = Arrays.copyOfRange(commandParams, 1, 3);

            boolean validCommand = isCommandValid(command);
            boolean validParameters = areParametersValid(params, command);

            if (!validParameters || !validCommand) {
                throw new IllegalArgumentException(BAD_PARAMETERS);
            }

            startCommand = command;
            startParams = params;

        } catch (Exception e) {
            System.out.println(BAD_PARAMETERS);
            getCommand();
        }
    }

    private static boolean isCommandValid(String command) {
        try {
            Command.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    private static boolean areParametersValid(String[] params, String command) {
        Command action = Command.valueOf(command.toUpperCase());
        if (action == Command.EXIT) {
            return true; // exit command can go without any params, so they don't matter
        }

        try {
            for (String param : params) {
                Parameter.valueOf(param.toUpperCase());
            }
            return true;

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private Player createPlayer(char symbol, String type) {

        Parameter userType;
        try {
            userType = Parameter.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        return  userType == Parameter.USER ?
                new User(symbol, type) :
                new AI(symbol, type);
    }

    private enum Command {
        START(),
        EXIT();
    }

    public enum Parameter {
        USER,
        EASY,
        MEDIUM,
        HARD;
    }
}
