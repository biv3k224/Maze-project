package net.bproject.maze.maze_project.service;

import org.springframework.stereotype.Service;

import net.bproject.maze.maze_project.model.Maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class MazeService {

    private final Random random = new Random();

    // Generate a maze using Recursive Backtracking
    public Maze generateMaze(int rows, int cols) {
        Maze maze = new Maze(rows, cols);
        boolean[][] visited = new boolean[rows][cols];
        generateMazeRecursive(0, 0, maze, visited);
        return maze;
    }

    private void generateMazeRecursive(int row, int col, Maze maze, boolean[][] visited) {
        visited[row][col] = true;

        // Randomize directions to explore
        List<int[]> directions = getShuffledDirections();

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            // Check if the new cell is within bounds and unvisited
            if (isInBounds(newRow, newCol, maze.getRows(), maze.getCols()) && !visited[newRow][newCol]) {
                // Remove the wall between the current and new cell
                maze.removeWall(row, col, dir[0], dir[1]);
                // Recur for the new cell
                generateMazeRecursive(newRow, newCol, maze, visited);
            }
        }
    }

    // Solve the maze using Depth-First Search (DFS)
    public int[][] solveMaze(Maze maze) {
        int rows = maze.getRows();
        int cols = maze.getCols();
        int[][] solution = new int[rows][cols];
        boolean[][] visited = new boolean[rows][cols];

        // Start at the top-left corner
        dfsSolve(0, 0, maze, solution, visited);

        return solution;
    }

    private boolean dfsSolve(int row, int col, Maze maze, int[][] solution, boolean[][] visited) {
        // Mark the cell as part of the solution path
        solution[row][col] = 1;
        visited[row][col] = true;

        // Base case: reached the bottom-right corner
        if (row == maze.getRows() - 1 && col == maze.getCols() - 1) {
            return true;
        }

        // Try all possible moves (up, right, down, left)
        for (int[] dir : getDirections()) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (isInBounds(newRow, newCol, maze.getRows(), maze.getCols()) &&
                !visited[newRow][newCol] &&
                maze.canMove(row, col, dir[0], dir[1])) {
                if (dfsSolve(newRow, newCol, maze, solution, visited)) {
                    return true; // Found a solution
                }
            }
        }

        // Backtrack: unmark the cell as part of the solution path
        solution[row][col] = 0;
        return false;
    }

    // Helper: Check if a cell is within bounds
    private boolean isInBounds(int row, int col, int rows, int cols) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    // Helper: Get shuffled directions for maze generation
    private List<int[]> getShuffledDirections() {
        List<int[]> directions = getDirections();
        Collections.shuffle(directions);
        return directions;
    }

    // Helper: Directions for moving in the grid
    private List<int[]> getDirections() {
        List<int[]> directions = new ArrayList<>();
        directions.add(new int[]{-1, 0}); // Up
        directions.add(new int[]{0, 1});  // Right
        directions.add(new int[]{1, 0}); // Down
        directions.add(new int[]{0, -1}); // Left
        return directions;
    }
}

