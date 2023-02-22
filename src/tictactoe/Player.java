package tictactoe;


import static tictactoe.Game.getUserInput;

public class Player {
    public static final String ENTER_COORDINATES = "Enter the coordinates: ";
    char symbol;
    char enemySymbol;
    String type;

    public Player(char symbol, String type) {
        this.symbol = symbol;
        this.enemySymbol = symbol == 'X' ? 'O' : 'X';
        this.type = type;
    }

    public char getSymbol() {
        return symbol;
    }

    public char getEnemySymbol() {
        return enemySymbol;
    }

    public String getType() {
        return type;
    }

    public void chooseCoordinates(Game game) {
        char[][] field = game.getField();

        System.out.print(ENTER_COORDINATES);
        String userInput = getUserInput();

        MoveValidator validator = new MoveValidator();
        validator.checkCoordinates(userInput, field);

        String error = validator.getError();
        if (error == null) {
            int[] coordinates = validator.convertUserInputToArrayCoordinates(userInput);
            setCoordinates(field, coordinates);
        } else {
            System.out.println(error);
            chooseCoordinates(game);
        }
    }

    public void setCoordinates(char[][] field, int[] coordinates) {

        int y = coordinates[0];
        int x = coordinates[1];

        field[y][x] = symbol;
    }


}




