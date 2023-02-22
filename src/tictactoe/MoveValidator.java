package tictactoe;

public class MoveValidator {
    public static final String CELL_IS_OCCUPIED = "This cell is occupied! Choose another one!";
    public static final String OUT_OF_RANGE = "Coordinates should be from 1 to 3!";
    public static final String NOT_A_NUMBER = "You should enter numbers!";
    public static final String INVALID_COORDINATES_NUMBER = "Please, enter only 2 numbers";
    public static final int MIN_USER_COORDINATE = 1;
    public static final int MAX_USER_COORDINATE = 3;
    public static final int ARRAY_INDEX_OFFSET = 1;
    private String error;

    public String getError() {
        return error;
    }

    public void checkCoordinates(String userInput, char[][] field) {

        String[] coordinates = userInput.split(" ");
        checkNumberOfCoordinates(coordinates);

        if (error == null) {
            checkCoordinatesAreNumeric(coordinates);
            checkCoordinatesAreWithinRange(coordinates);
            checkCoordinatesAreAvailable(coordinates, field);
        }
    }

    private void checkCoordinatesAreAvailable(String[] coordinates, char[][] field) {

        int[] humanCoordinatesInt = convertCoordinatesToInt(coordinates);
        int[] coordinatesInt = adjustCoordinatesByOffset(humanCoordinatesInt);

        int y = coordinatesInt[0];
        int x = coordinatesInt[1];

        if (field[y][x] != ' ') {
            error = CELL_IS_OCCUPIED;
        }
    }

    private void checkCoordinatesAreWithinRange(String[] coordinates) {
        int[] coordinatesInt = convertCoordinatesToInt(coordinates);

        for (int coordinate : coordinatesInt) {
            error =
                    coordinate >= MIN_USER_COORDINATE &&
                            coordinate <= MAX_USER_COORDINATE ?
                            error : OUT_OF_RANGE;
        }
    }

    private void checkCoordinatesAreNumeric(String[] coordinatesString) {
        for (String coordinate : coordinatesString) {
            try {
                Integer.parseInt(coordinate);
            } catch (Exception e) {
                error = NOT_A_NUMBER;
            }
        }
    }

    private void checkNumberOfCoordinates(String[] coordinatesString) {
        if (coordinatesString.length != 2) {
            error = INVALID_COORDINATES_NUMBER;
        }
    }

    private int[] convertCoordinatesToInt(String[] coordinatesString) {
        int[] coordinates = new int[coordinatesString.length];

        for (int i = 0; i < coordinatesString.length; i++) {
            coordinates[i] = Integer.valueOf(coordinatesString[i]);
        }
        return coordinates;
    }

    public int[] convertUserInputToArrayCoordinates(String userInput) {
        int[] humanCoordinatesInt = convertCoordinatesToInt(userInput.split(" "));
        int[] coordinatesInt = adjustCoordinatesByOffset(humanCoordinatesInt);

        return coordinatesInt;
    }

    private int[] adjustCoordinatesByOffset(int[] coordinates) {
        int y = coordinates[0] - ARRAY_INDEX_OFFSET;
        int x = coordinates[1] - ARRAY_INDEX_OFFSET;
        return new int[]{y,x};
    }
}
