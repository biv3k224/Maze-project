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

    private Maze originalMaze; // Store the original maze
    private Maze currentMaze;  // Store the current (potentially modified) maze

    @PostMapping("/generate")
    public ResponseEntity<Maze> generateMaze(@RequestParam int rows, @RequestParam int cols) {
        originalMaze = mazeService.generateMaze(rows, cols);
        currentMaze = originalMaze; // Reset to original when generating
        return ResponseEntity.ok(currentMaze);
    }

    @PostMapping("/reset")
    public ResponseEntity<Maze> resetMaze() {
        currentMaze = originalMaze; // Reset to the original maze
        return ResponseEntity.ok(currentMaze);
    }

    @PostMapping("/solve")
    public ResponseEntity<Map<String, Object>> solveMaze(@RequestParam String algorithm) {
        // Use the current maze (not the request body)
        int[][] solution = mazeService.solveUsingAlgorithm(currentMaze, algorithm);
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
