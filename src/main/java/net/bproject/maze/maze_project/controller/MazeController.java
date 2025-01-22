package net.bproject.maze.maze_project.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import net.bproject.maze.maze_project.model.Maze;
import net.bproject.maze.maze_project.service.MazeService;

import java.util.*;

@RestController
@RequestMapping("/maze")
public class MazeController {

    @Autowired
    private MazeService mazeService;

    private Maze currentMaze;

    @PostMapping("/generate")
    public ResponseEntity<Maze> generateMaze(@RequestParam int rows, @RequestParam int cols) {
        currentMaze = mazeService.generateMaze(rows, cols);
        return ResponseEntity.ok(currentMaze);
    }

    @PostMapping("/solve")
    public ResponseEntity<Map<String, Object>> solveMaze(@RequestParam String algorithm, @RequestBody Maze maze) {
        int[][] solution;
        switch (algorithm.toLowerCase()) {
            case "bfs":
                solution = mazeService.solveMazeBFS(maze);
                break;
            case "dijkstra":
                solution = mazeService.solveMazeDijkstra(maze);
                
                break;
            case "astar":
                solution = mazeService.solveMazeAStar(maze);
                break;
            case "dfs":
            default:
                solution = mazeService.solveMazeDFS(maze);
        }
        // Convert solution to a list of coordinates
        List<int[]> path = convertSolutionToPath(solution);
        Map<String, Object> response = new HashMap<>();
        response.put("path", path);
        return ResponseEntity.ok(response);
    }

    private List<int[]> convertSolutionToPath(int[][] solution) {
        List<int[]> path = new ArrayList<>();
        for (int row = 0; row < solution.length; row++) {
            for (int col = 0; col < solution[row].length; col++) {
                if (solution[row][col] == 1) {
                    path.add(new int[]{row, col});
                }
            }
        }
        return path;
    }

}
