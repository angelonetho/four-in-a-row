package dev.netho.game.entity;

import dev.netho.game.exception.IllegalMoveException;

public class Board {

    public static final int ROWS = 6;
    public static final int COLS = 7;

    private final Disc[][] grid;


    public Board() {
        grid = new Disc[ROWS][COLS];

        // Inicializa com todas as células do tabuleiro vazias
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                grid[i][j] = Disc.EMPTY;
            }
        }
    }

    public boolean insert(int column, Disc disc) throws IllegalMoveException {
        if (column < 0 || column > ROWS) {
            throw new IllegalMoveException("Coluna Inválida: " + column);
        }

        for (int i = ROWS - 1; i >= 0; i--) {
            if (grid[i][column] == Disc.EMPTY) {
                grid[i][column] = disc;
                return true;
            }

        }

        return false;
    }

    private boolean checkDirection(int x, int y, Disc disc, int dx, int dy) {
        int count = 0;
        for (int k = 0; k < 4; k++) {
            int nx = x + dx * k;
            int ny = y + dy * k;
            if (nx < 0 || nx >= ROWS || ny < 0 || ny >= COLS) {
                break;
            }
            if (grid[nx][ny] == disc) {
                count++;
            } else {
                break;
            }
        }
        return count == 4;
    }

    public boolean checkDrawCondition() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j] == Disc.EMPTY) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean checkWinCondition(Disc disc) {
        // checa em todas as posições todas as direções
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (checkDirection(i, j, disc, 1, 0)  // vertical
                        || checkDirection(i, j, disc, 0, 1)  // horizontal
                        || checkDirection(i, j, disc, 1, 1)  // diagonal descendente
                        || checkDirection(i, j, disc, 1, -1) // diagonal ascendente
                ) {
                    return true;
                }
            }
        }
        return false;
    }

    public Disc[][] getGrid() {
        return grid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Disc[] row : grid) {
            for (Disc c : row) {
                switch (c) {
                    case EMPTY  -> sb.append(". ");
                    case PLAYER1-> sb.append("X ");
                    case PLAYER2-> sb.append("O ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
