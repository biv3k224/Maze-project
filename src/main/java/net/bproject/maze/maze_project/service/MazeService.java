package net.bproject.maze.maze_project.service;

import org.springframework.stereotype.Service;
import net.bproject.maze.maze_project.model.Maze;
import java.util.*;

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
        List<int[]> directions = getShuffledDirections();

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (isInBounds(newRow, newCol, maze.getRows(), maze.getCols()) && !visited[newRow][newCol]) {
                maze.removeWall(row, col, dir[0], dir[1]);
                generateMazeRecursive(newRow, newCol, maze, visited);
            }
        }
    }

    // Solve methods now work on a COPY of the maze to preserve original state
    public int[][] solveMazeDFS(Maze originalMaze) {
        Maze mazeCopy = deepCopyMaze(originalMaze);
        return solveUsingAlgorithm(mazeCopy, "dfs");
    }

    public int[][] solveMazeBFS(Maze originalMaze) {
        Maze mazeCopy = deepCopyMaze(originalMaze);
        return solveUsingAlgorithm(mazeCopy, "bfs");
    }

    public int[][] solveMazeDijkstra(Maze originalMaze) {
        Maze mazeCopy = deepCopyMaze(originalMaze);
        return solveUsingAlgorithm(mazeCopy, "dijkstra");
    }

    public int[][] solveMazeAStar(Maze originalMaze) {
        Maze mazeCopy = deepCopyMaze(originalMaze);
        return solveUsingAlgorithm(mazeCopy, "astar");
    }

    // Deep copy maze to avoid modifying original walls
    private Maze deepCopyMaze(Maze original) {
        Maze copy = new Maze(original.getRows(), original.getCols());
        
        // Copy horizontal walls
        for (int i = 0; i < original.getHorizontalWalls().length; i++) {
            System.arraycopy(
                original.getHorizontalWalls()[i], 0,
                copy.getHorizontalWalls()[i], 0,
                original.getHorizontalWalls()[i].length
            );
        }
        
        // Copy vertical walls
        for (int i = 0; i < original.getVerticalWalls().length; i++) {
            System.arraycopy(
                original.getVerticalWalls()[i], 0,
                copy.getVerticalWalls()[i], 0,
                original.getVerticalWalls()[i].length
            );
        }
        
        return copy;
    }

    // Shared solving logic
    public int[][] solveUsingAlgorithm(Maze maze, String algorithm) {
        int rows = maze.getRows();
        int cols = maze.getCols();
        int[][] solution = new int[rows][cols];
        boolean[][] visited = new boolean[rows][cols];

        switch (algorithm.toLowerCase()) {
            case "bfs":
                bfsSolve(maze, solution);
                break;
            case "dijkstra":
                dijkstraSolve(maze, solution);
                break;
            case "astar":
                aStarSolve(maze, solution);
                break;
            case "dfs":
            default:
                dfsSolve(0, 0, maze, solution, visited);
        }
        return solution;
    }

    // BFS Implementation
    private void bfsSolve(Maze maze, int[][] solution) {
        int rows = maze.getRows();
        int cols = maze.getCols();
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        int[][] parent = new int[rows * cols][2];

        queue.add(new int[]{0, 0});
        visited[0][0] = true;
        parent[0] = new int[]{-1, -1};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];

            if (row == rows - 1 && col == cols - 1) {
                reconstructPath(solution, parent, row, col, cols);
                return;
            }

            for (int[] dir : getDirections()) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                if (isValidMove(maze, visited, newRow, newCol, rows, cols) && 
                    maze.canMove(row, col, dir[0], dir[1])) {
                    visited[newRow][newCol] = true;
                    parent[newRow * cols + newCol] = new int[]{row, col};
                    queue.add(new int[]{newRow, newCol});
                }
            }
        }
    }
    
    private boolean dfsSolve(int row, int col, Maze maze, int[][] solution, boolean[][] visited) {
        if (row == maze.getRows() - 1 && col == maze.getCols() - 1) {
            solution[row][col] = 1;
            return true;
        }

        visited[row][col] = true;
        solution[row][col] = 1;

        for (int[] dir : getDirections()) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isValidMove(maze, visited, newRow, newCol, maze.getRows(), maze.getCols()) &&
                maze.canMove(row, col, dir[0], dir[1])) {
                if (dfsSolve(newRow, newCol, maze, solution, visited)) {
                    return true;
                }
            }
        }

        solution[row][col] = 0; // Backtrack
        return false;
    }

    // A* Implementation
    private void aStarSolve(Maze maze, int[][] solution) {
        int rows = maze.getRows();
        int cols = maze.getCols();
        int[][] distances = new int[rows][cols];
        int[][] parent = new int[rows * cols][2]; // Track parent coordinates
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));

        initializeDistances(distances);
        distances[0][0] = 0;
        Arrays.fill(parent, new int[]{-1, -1}); // Initialize parents to (-1, -1)
        parent[0] = new int[]{-1, -1}; // Starting node has no parent

        pq.add(new int[]{0, 0, heuristic(0, 0, rows - 1, cols - 1)});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int row = current[0];
            int col = current[1];

            // Skip if a shorter path to this node has already been found
            if (current[2] > distances[row][col] + heuristic(row, col, rows - 1, cols - 1)) {
                continue;
            }

            if (row == rows - 1 && col == cols - 1) {
                reconstructPath(solution, parent, row, col, cols); // Use parent array
                return;
            }

            for (int[] dir : getDirections()) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                if (isInBounds(newRow, newCol, rows, cols) &&
                    maze.canMove(row, col, dir[0], dir[1])) {

                    int newCost = distances[row][col] + 1; // g-value
                    if (newCost < distances[newRow][newCol]) {
                        distances[newRow][newCol] = newCost;
                        int heuristicValue = heuristic(newRow, newCol, rows - 1, cols - 1);
                        pq.add(new int[]{newRow, newCol, newCost + heuristicValue});
                        parent[newRow * cols + newCol] = new int[]{row, col}; // Update parent
                    }
                }
            }
        }
    }

    private void dijkstraSolve(Maze maze, int[][] solution) {
        int rows = maze.getRows();
        int cols = maze.getCols();
        int[][] distances = new int[rows][cols];
        boolean[][] visited = new boolean[rows][cols];
        int[][] parent = new int[rows * cols][2];
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));

        // Initialize distances and parent array
        for (int[] row : distances) Arrays.fill(row, Integer.MAX_VALUE);
        for (int[] p : parent) Arrays.fill(p, -1); // Initialize parent to {-1, -1}
        distances[0][0] = 0;
        parent[0] = new int[]{-1, -1}; // Starting node has no parent

        pq.add(new int[]{0, 0, 0});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int row = current[0];
            int col = current[1];

            if (visited[row][col]) continue;
            visited[row][col] = true;

            if (row == rows - 1 && col == cols - 1) {
                reconstructPath(solution, parent, row, col, cols); // Pass parent instead of distances
                return;
            }

            for (int[] dir : getDirections()) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                if (isInBounds(newRow, newCol, rows, cols) &&
                    !visited[newRow][newCol] &&
                    maze.canMove(row, col, dir[0], dir[1])) {

                    int newDist = distances[row][col] + 1;
                    if (newDist < distances[newRow][newCol]) {
                        distances[newRow][newCol] = newDist;
                        parent[newRow * cols + newCol] = new int[]{row, col}; // Track parent
                        pq.add(new int[]{newRow, newCol, newDist});
                    }
                }
            }
        }
    }

    // Helper methods
    private boolean isValidMove(Maze maze, boolean[][] visited, int row, int col, int rows, int cols) {
        return row >= 0 && row < rows && col >= 0 && col < cols && !visited[row][col];
    }

    private void initializeDistances(int[][] distances) {
        for (int[] row : distances) Arrays.fill(row, Integer.MAX_VALUE);
    }

    private int heuristic(int row, int col, int goalRow, int goalCol) {
        return Math.abs(row - goalRow) + Math.abs(col - goalCol);
    }

    private void reconstructPath(int[][] solution, int[][] parent, int row, int col, int cols) {
        List<int[]> tempPath = new ArrayList<>();
        while (row != -1 && col != -1) {
            tempPath.add(new int[]{row, col});
            int index = row * cols + col;
            int[] prev = parent[index];
            row = prev[0];
            col = prev[1];
        }
        // Reverse the path to go from start -> end
        Collections.reverse(tempPath);
        for (int[] coord : tempPath) {
            solution[coord[0]][coord[1]] = 1;
        }
    }

    // Keep existing helper methods (getDirections(), isInBounds(), etc.)
    private List<int[]> getShuffledDirections() {
        List<int[]> directions = Arrays.asList(
            new int[]{-1, 0}, // Up
            new int[]{0, 1},  // Right
            new int[]{1, 0},  // Down
            new int[]{0, -1}  // Left
        );
        Collections.shuffle(directions);
        return directions;
    }
    private List<int[]> getDirections() {
        List<int[]> directions = new ArrayList<>();
        directions.add(new int[]{-1, 0}); // Up
        directions.add(new int[]{0, 1});  // Right
        directions.add(new int[]{1, 0}); // Down
        directions.add(new int[]{0, -1}); // Left
        return directions;
    }


    private boolean isInBounds(int row, int col, int rows, int cols) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
}