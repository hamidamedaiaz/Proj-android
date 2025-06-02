package edu.polytech.vide;

import java.util.Observable;

public class M extends Observable {
    private final String TAG = "leo " + getClass().getSimpleName();
    private final int LINES;
    private final int COLUMNS;
    private Square[][] squares;

    class Square implements Cloneable{
        private int line;
        private int column;
        private boolean value;

        public Square(int line, int column, boolean value) {
            this.line = line;
            this.column = column;
            this.value = value;
        }

        public void setAlive(boolean value) {
            this.value = value;
        }

        public boolean isAlive() {
            return value;
        }

        @Override
        public Square clone() {
            return new Square(line, column, value);
        }
    }

    public M(int lines, int columns) {
        this.LINES = lines;
        this.COLUMNS = columns;
        this.squares = new Square[LINES][COLUMNS];
        initEmptyGrid();
    }

    public boolean isAlive(int i, int j) {
        return squares[i][j].isAlive();
    }

    private void initEmptyGrid() {
        for (int i = 0; i < LINES; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                squares[i][j] = new Square(i, j, false);
            }
        }
    }

    public void nextGeneration() {
        Square[][] newGrid = copyGrid();

        for (int i = 0; i < LINES; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                int neighbors = countAliveNeighbors(i, j);
                boolean alive = squares[i][j].isAlive();

                if (alive) {
                    newGrid[i][j].setAlive(neighbors == 2 || neighbors == 3);
                } else {
                    newGrid[i][j].setAlive(neighbors == 3);
                }
            }
        }

        squares = newGrid;
        setChanged();
        notifyObservers();
    }

    private int countAliveNeighbors(int x, int y) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && nx < LINES && ny >= 0 && ny < COLUMNS && squares[nx][ny].isAlive()) {
                    count++;
                }
            }
        }
        return count;
    }

    private Square[][] copyGrid() {
        Square[][] clone = new Square[LINES][COLUMNS];
        for (int i = 0; i < LINES; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                clone[i][j] = squares[i][j].clone();
            }
        }
        return clone;
    }

    public void setPlaneur() {
        initEmptyGrid();
        if (LINES > 2 && COLUMNS > 2) {
            squares[0][1].setAlive(true);
            squares[1][2].setAlive(true);
            squares[2][0].setAlive(true);
            squares[2][1].setAlive(true);
            squares[2][2].setAlive(true);
        }
        notifyUpdate();
    }

    public void setCarre() {
        initEmptyGrid();
        if (LINES > 2 && COLUMNS > 2) {
            squares[1][1].setAlive(true);
            squares[1][2].setAlive(true);
            squares[2][1].setAlive(true);
            squares[2][2].setAlive(true);
        }
        notifyUpdate();
    }

    public void setPhare() {
        initEmptyGrid();
        if (LINES > 4 && COLUMNS > 4) {
            squares[1][1].setAlive(true);
            squares[1][2].setAlive(true);
            squares[2][1].setAlive(true);
            squares[2][2].setAlive(true);
            squares[3][3].setAlive(true);
            squares[3][4].setAlive(true);
            squares[4][3].setAlive(true);
            squares[4][4].setAlive(true);
        }
        notifyUpdate();
    }

    private void notifyUpdate() {
        setChanged();
        notifyObservers();
    }
}