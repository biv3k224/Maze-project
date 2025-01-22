package net.bproject.maze.maze_project.model;



public class Maze {

    private final int rows;
    private final int cols;
    private final boolean[][] horizontalWalls;
    private final boolean[][] verticalWalls;

    // Constructor
    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        // Initialize walls: all walls are initially present
        horizontalWalls = new boolean[rows + 1][cols]; // Horizontal walls (between rows)
        verticalWalls = new boolean[rows][cols + 1];   // Vertical walls (between columns)

        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j < cols; j++) {
                horizontalWalls[i][j] = true;
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= cols; j++) {
                verticalWalls[i][j] = true;
            }
        }
    }

    // Getters
    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public boolean[][] getHorizontalWalls() {
        return horizontalWalls;
    }

    public boolean[][] getVerticalWalls() {
        return verticalWalls;
    }

    // Remove the wall between two adjacent cells
    public void removeWall(int row, int col, int dRow, int dCol) {
        if (dRow == -1 && dCol == 0) { // Up
            horizontalWalls[row][col] = false;
        } else if (dRow == 1 && dCol == 0) { // Down
            horizontalWalls[row + 1][col] = false;
        } else if (dRow == 0 && dCol == -1) { // Left
            verticalWalls[row][col] = false;
        } else if (dRow == 0 && dCol == 1) { // Right
            verticalWalls[row][col + 1] = false;
        }
    }

    // Check if movement is possible between two cells
    public boolean canMove(int row, int col, int dRow, int dCol) {
        if (dRow == -1 && dCol == 0) { // Up
            return !horizontalWalls[row][col];
        } else if (dRow == 1 && dCol == 0) { // Down
            return !horizontalWalls[row + 1][col];
        } else if (dRow == 0 && dCol == -1) { // Left
            return !verticalWalls[row][col];
        } else if (dRow == 0 && dCol == 1) { // Right
            return !verticalWalls[row][col + 1];
        }
        return false;
    }
}