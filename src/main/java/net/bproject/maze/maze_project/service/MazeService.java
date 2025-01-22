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

    // Solve the maze using Depth-First Search (DFS)
    public int[][] solveMazeDFS(Maze maze) {
        return solveUsingAlgorithm(maze, "dfs");
    }

    // Solve the maze using Breadth-First Search (BFS)
    public int[][] solveMazeBFS(Maze maze) {
        return solveUsingAlgorithm(maze, "bfs");
    }

    // Solve the maze using Dijkstra's Algorithm
    public int[][] solveMazeDijkstra(Maze maze) {
        return solveUsingAlgorithm(maze, "dijkstra");
    }

    // Solve the maze using A* Algorithm
    public int[][] solveMazeAStar(Maze maze) {
        return solveUsingAlgorithm(maze, "astar");
    }

    private int[][] solveUsingAlgorithm(Maze maze, String algorithm) {
        // Logic for DFS, BFS, Dijkstra, A*
        int rows = maze.getRows();
        int cols = maze.getCols();
        int[][] solution = new int[rows][cols];
        boolean[][] visited = new boolean[rows][cols];
        
        if ("dfs".equalsIgnoreCase(algorithm)) {
            dfsSolve(0, 0, maze, solution, visited);
        } else if ("bfs".equalsIgnoreCase(algorithm)) {
            bfsSolve(maze, solution);
        } else if ("dijkstra".equalsIgnoreCase(algorithm)) {
            dijkstraSolve(maze, solution);
        } else if ("astar".equalsIgnoreCase(algorithm)) {
            aStarSolve(maze, solution);
        }
        return solution;
    }

    private void bfsSolve(Maze maze, int[][] solution) {
        int rows = maze.getRows();
        int cols = maze.getCols();
        boolean[][] visited = new boolean[rows][cols];
        int[][] parent = new int[rows * cols][2]; // To reconstruct the path
        Queue<int[]> queue = new LinkedList<>();

        // Start BFS from the top-left corner
        queue.add(new int[]{0, 0});
        visited[0][0] = true;
        parent[0] = new int[]{-1, -1}; // Start has no parent

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];

            // If we reach the bottom-right corner, reconstruct the path
            if (row == rows - 1 && col == cols - 1) {
                reconstructPath(solution, parent, row, col, cols);
                return;
            }

            // Explore neighbors
            for (int[] dir : getDirections()) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (isInBounds(newRow, newCol, rows, cols) &&
                    !visited[newRow][newCol] &&
                    maze.canMove(row, col, dir[0], dir[1])) {

                    visited[newRow][newCol] = true;
                    parent[newRow * cols + newCol] = new int[]{row, col};
                    queue.add(new int[]{newRow, newCol});
                }
            }
        }
    }

    // Reconstruct the path from the parent array
    private void reconstructPath(int[][] solution, int[][] parent, int row, int col, int cols) {
        while (row != -1 && col != -1) {
            solution[row][col] = 1; // Mark the solution path
            int[] prev = parent[row * cols + col];
            row = prev[0];
            col = prev[1];
        }
    }


    private void dijkstraSolve(Maze maze, int[][] solution) {
        int rows = maze.getRows();
        int cols = maze.getCols();
        int[][] distances = new int[rows][cols];
        boolean[][] visited = new boolean[rows][cols];
        int[][] parent = new int[rows * cols][2]; // To reconstruct the path
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));

        // Initialize distances to infinity
        for (int[] row : distances) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        // Start from the top-left corner
        distances[0][0] = 0;
        pq.add(new int[]{0, 0, 0}); // {row, col, cost}
        parent[0] = new int[]{-1, -1}; // Start has no parent

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int row = current[0];
            int col = current[1];

            if (visited[row][col]) continue; // Skip already visited nodes
            visited[row][col] = true;

            // If we reach the bottom-right corner, reconstruct the path
            if (row == rows - 1 && col == cols - 1) {
                reconstructPath(solution, parent, row, col, cols);
                return;
            }

            // Explore neighbors
            for (int[] dir : getDirections()) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (isInBounds(newRow, newCol, rows, cols) &&
                    !visited[newRow][newCol] &&
                    maze.canMove(row, col, dir[0], dir[1])) {

                    int newCost = distances[row][col] + 1; // Each step has a cost of 1
                    if (newCost < distances[newRow][newCol]) {
                        distances[newRow][newCol] = newCost;
                        parent[newRow * cols + newCol] = new int[]{row, col};
                        pq.add(new int[]{newRow, newCol, newCost});
                    }
                }
            }
        }
    }


    private void aStarSolve(Maze maze, int[][] solution) {
        int rows = maze.getRows();
        int cols = maze.getCols();
        int[][] distances = new int[rows][cols];
        boolean[][] visited = new boolean[rows][cols];
        int[][] parent = new int[rows * cols][2]; // To reconstruct the path
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));

        // Initialize distances to infinity
        for (int[] row : distances) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        // Start from the top-left corner
        distances[0][0] = 0;
        pq.add(new int[]{0, 0, heuristic(0, 0, rows - 1, cols - 1)}); // {row, col, f = g + h}
        parent[0] = new int[]{-1, -1}; // Start has no parent

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int row = current[0];
            int col = current[1];

            if (visited[row][col]) continue; // Skip already visited nodes
            visited[row][col] = true;

            // If we reach the bottom-right corner, reconstruct the path
            if (row == rows - 1 && col == cols - 1) {
                reconstructPath(solution, parent, row, col, cols);
                return;
            }

            // Explore neighbors
            for (int[] dir : getDirections()) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (isInBounds(newRow, newCol, rows, cols) &&
                    !visited[newRow][newCol] &&
                    maze.canMove(row, col, dir[0], dir[1])) {

                    int g = distances[row][col] + 1; // Cost to reach neighbor
                    int h = heuristic(newRow, newCol, rows - 1, cols - 1); // Heuristic to goal
                    int f = g + h;

                    if (g < distances[newRow][newCol]) { // Found a better path
                        distances[newRow][newCol] = g;
                        parent[newRow * cols + newCol] = new int[]{row, col};
                        pq.add(new int[]{newRow, newCol, f});
                    }
                }
            }
        }
    }

    // Heuristic function: Manhattan Distance
    private int heuristic(int row, int col, int goalRow, int goalCol) {
        return Math.abs(row - goalRow) + Math.abs(col - goalCol);
    }


    private boolean dfsSolve(int row, int col, Maze maze, int[][] solution, boolean[][] visited) {
        solution[row][col] = 1;
        visited[row][col] = true;

        if (row == maze.getRows() - 1 && col == maze.getCols() - 1) {
            return true;
        }

        for (int[] dir : getDirections()) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isInBounds(newRow, newCol, maze.getRows(), maze.getCols()) &&
                !visited[newRow][newCol] &&
                maze.canMove(row, col, dir[0], dir[1])) {
                if (dfsSolve(newRow, newCol, maze, solution, visited)) {
                    return true;
                }
            }
        }

        solution[row][col] = 0;
        return false;
    }

    private boolean isInBounds(int row, int col, int rows, int cols) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    private List<int[]> getShuffledDirections() {
        List<int[]> directions = getDirections();
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
}
