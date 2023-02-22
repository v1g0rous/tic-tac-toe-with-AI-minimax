package tictactoe;

import java.util.Random;

public class AI extends Player {

    public AI(char symbol, String type) {
        super(symbol, type);
    }

    @Override
    public void chooseCoordinates(Game game) {
        char[][] field = game.getField();
        int[] coordinates;

        try {
            Game.Parameter difficulty = Game.Parameter.valueOf(getType().toUpperCase());

            coordinates = switch (difficulty) {
                case EASY -> makeAiMoveEasy(game);
                case MEDIUM -> makeAiMoveMedium(game);
                case HARD -> makeAiMoveHard(game);
                case USER -> null;
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (coordinates != null && coordinates.length > 0) {
            setCoordinates(field, coordinates);
        }
    }

    private int[] makeAiMoveHard(Game game) {
        System.out.println("Making move level \"hard\"");
        CoordinatesAnalyzer analyzer = new CoordinatesAnalyzer();
        return analyzer.getBestMoveByMinimax(game);
    }

    private int[] makeAiMoveMedium(Game game) {
        System.out.println("Making move level \"medium\"");
        CoordinatesAnalyzer analyzer = new CoordinatesAnalyzer();
        analyzer.checkBestAvailableCoordinates(game.getField());

        return analyzer.readyToAttack ? analyzer.getCoordinatesToAttack() :
                analyzer.readyToDefend ? analyzer.getCoordinatesToDefend() :
                        chooseRandomCoordinates(game);
    }

    private int[] makeAiMoveEasy(Game game) {
        System.out.println("Making move level \"easy\"");
        return chooseRandomCoordinates(game);
    }

    private int[] chooseRandomCoordinates(Game game) {
        Random randomGenerator = new Random();

        int[][] emptyCells = game.getEmptyCells();

        if (emptyCells.length > 0) {
            int random = randomGenerator.nextInt(emptyCells.length);
            int[] coordinates = emptyCells[random];
            return coordinates;
        }
        return new int[0];
    }

    private class CoordinatesAnalyzer {
        private int[] coordinatesToAttack = new int[2];
        private int[] coordinatesToDefend = new int[2];

        private boolean readyToAttack = false;
        private boolean readyToDefend = false;

        private int playerRowCounter = 0;
        private int enemyRowCounter = 0;
        private int playerColCounter = 0;
        private int enemyColCounter = 0;

        private int playerLeftToRightDiagCounter = 0;
        private int enemyLeftToRightDiagCounter = 0;
        private int playerRightToLeftDiagCounter = 0;
        private int enemyRightToLeftDiagCounter = 0;


        private int[] potentialCoordinatesRow = new int[2];
        private int[] potentialCoordinatesCol = new int[2];
        private int[] potentialCoordinatesLeftToRightDiag = new int[2];
        private int[] potentialCoordinatesRightToLeftDiag = new int[2];


        public int[] getCoordinatesToAttack() {
            return coordinatesToAttack;
        }

        public int[] getCoordinatesToDefend() {
            return coordinatesToDefend;
        }

        private static int getScoreByDepthAndResult(String gameResults, int depth) {

            if (gameResults.equals("O")) {
                return 10 - depth;
            } else if (gameResults.equals("X")) {
                return depth - 10;
            }

            return 0;
        }


        private int[] getBestMoveByMinimax(Game game) {

            boolean isMaximizingPlayer = game.getCurrentPlayer().getSymbol() == 'O';

            int bestScore = isMaximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            int[] bestMove = new int[2];
            int[][] emptyCells = game.getEmptyCells();

            for (int i = 0; i < emptyCells.length; i++) {
                int x = emptyCells[i][0];
                int y = emptyCells[i][1];

                game.getField()[x][y] = game.getCurrentPlayer().getSymbol();

                int depth = 0;

                int score = isMaximizingPlayer ?
                        getBestScoreByMinimax(game, false, depth) :
                        getBestScoreByMinimax(game, true, depth);

                game.getField()[x][y] = ' ';

                if (isMaximizingPlayer && score > bestScore) {
                    bestScore = score;
                    bestMove = new int[]{x, y};

                } else if (!isMaximizingPlayer && score < bestScore) {
                    bestScore = score;
                    bestMove = new int[]{x, y};
                }
            }
            return bestMove;
        }


        private static int getBestScoreByMinimax(Game game, Boolean isMaximizing, int depth) {

            ++depth;
            int bestScore;
            int score;

            String gameResults = game.checkGameResult();

            if (gameResults != null) {
                return getScoreByDepthAndResult(gameResults, depth);
            }

            bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            int[][] emptyCells = game.getEmptyCells();
            for (int i = 0; i < emptyCells.length; i++) {
                int x = emptyCells[i][0];
                int y = emptyCells[i][1];

                if(isMaximizing) {
                    game.getField()[x][y] = game.getCurrentPlayer().getSymbol();
                    score = getBestScoreByMinimax(game, false, depth);

                } else {
                    game.getField()[x][y] = game.getCurrentPlayer().getEnemySymbol();
                    score = getBestScoreByMinimax(game, true, depth);
                }

                game.getField()[x][y] = ' ';

                bestScore = isMaximizing ?
                        Math.max(score, bestScore) :
                        Math.min(score, bestScore);
            }
            return bestScore;
        }

        private void checkBestAvailableCoordinates(char[][] field) {
            for (int row = 0; row < field.length; row++) {
                for (int col = 0; col < field[row].length; col++) {
                    analyzeRows(field, row, col);
                    analyzeCols(field, row, col);
                    analyzeDiags(field, row, col);
                }
                makeDecision();

                playerRowCounter = 0;
                enemyRowCounter = 0;
                playerColCounter = 0;
                enemyColCounter = 0;
            }
        }

        private void makeDecision() {

            if (playerLeftToRightDiagCounter == 2 && enemyLeftToRightDiagCounter == 0) {
                readyToAttack = true;
                coordinatesToAttack = potentialCoordinatesLeftToRightDiag;
            } else if (enemyLeftToRightDiagCounter == 2 && playerLeftToRightDiagCounter == 0) {
                readyToDefend = true;
                coordinatesToDefend = potentialCoordinatesLeftToRightDiag;
            }

            if (playerRightToLeftDiagCounter == 2 && enemyRightToLeftDiagCounter == 0) {
                readyToAttack = true;
                coordinatesToAttack = potentialCoordinatesRightToLeftDiag;
            } else if (enemyRightToLeftDiagCounter == 2 && playerRightToLeftDiagCounter == 0) {
                readyToDefend = true;
                coordinatesToDefend = potentialCoordinatesRightToLeftDiag;
            }

            if (playerColCounter == 2 && enemyColCounter == 0) {
                readyToAttack = true;
                coordinatesToAttack = potentialCoordinatesCol;
            } else if (enemyColCounter == 2 && playerColCounter == 0) {
                readyToDefend = true;
                coordinatesToDefend = potentialCoordinatesCol;
            }

            if (playerRowCounter == 2 && enemyRowCounter == 0) {
                readyToAttack = true;
                coordinatesToAttack = potentialCoordinatesRow;
            } else if (enemyRowCounter == 2 && playerRowCounter == 0) {
                readyToDefend = true;
                coordinatesToDefend = potentialCoordinatesRow;
            }
        }

        private void analyzeDiags(char[][] field, int row, int col) {
            if (row == 0) {
                playerLeftToRightDiagCounter += field[col][row + col] == getSymbol() ? 1 : 0;
                enemyLeftToRightDiagCounter += field[col][row + col] == getEnemySymbol() ? 1 : 0;

                potentialCoordinatesLeftToRightDiag = field[col][row + col] == ' ' ? new int[]{col, row + col} : potentialCoordinatesLeftToRightDiag;

                playerRightToLeftDiagCounter += field[col][field[row].length - 1 - col] == getSymbol() ? 1 : 0;
                enemyRightToLeftDiagCounter += field[col][field[row].length - 1 - col] == getEnemySymbol() ? 1 : 0;

                potentialCoordinatesRightToLeftDiag = field[col][field[row].length - 1 - col] == ' ' ? new int[]{col, field[row].length - 1 - col} : potentialCoordinatesRightToLeftDiag;

            }
        }

        private void analyzeCols(char[][] field, int row, int col) {
            playerColCounter += field[col][row] == getSymbol() ? 1 : 0;
            enemyColCounter += field[col][row] == getEnemySymbol() ? 1 : 0;

            potentialCoordinatesCol = field[col][row] == ' ' ? new int[]{col, row} : potentialCoordinatesCol;
        }

        private void analyzeRows(char[][] field, int row, int col) {
            playerRowCounter += field[row][col] == getSymbol() ? 1 : 0;
            enemyRowCounter += field[row][col] == getEnemySymbol() ? 1 : 0;

            potentialCoordinatesRow = field[row][col] == ' ' ? new int[]{row, col} : potentialCoordinatesRow;

        }
    }


}
