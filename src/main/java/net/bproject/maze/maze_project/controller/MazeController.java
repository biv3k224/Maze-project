package net.bproject.maze.maze_project.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import net.bproject.maze.maze_project.model.Maze;
import net.bproject.maze.maze_project.service.MazeService;

@RestController
@RequestMapping("/maze")
public class MazeController {

    @Autowired
    private MazeService mazeService;

    private Maze currentMaze;

    // Generate a new maze and return it as JSON
    @PostMapping("/generate")
    public ResponseEntity<Maze> generateMaze(@RequestParam int rows, @RequestParam int cols) {
        currentMaze = mazeService.generateMaze(rows, cols);
        return ResponseEntity.ok(currentMaze); // Return the Maze object
    }

    // Solve the maze and return the solution as a 2D array
    @PostMapping("/solve")
    public ResponseEntity<int[][]> solveMaze() {
        if (currentMaze == null) {
            return ResponseEntity.badRequest().body(null); // Handle case when no maze is generated
        }
        int[][] solution = mazeService.solveMaze(currentMaze);
        return ResponseEntity.ok(solution);
    }
}