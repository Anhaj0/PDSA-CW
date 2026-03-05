package com.example.tasktool;

import org.springframework.web.bind.annotation.*;
import java.util.*;

// Marks this class as a REST Controller (handles HTTP requests)
@RestController
    
// Base URL mapping for all endpoints in this controller
@RequestMapping("/api/tasks")

// Allows cross-origin requests (for frontend like React/Angular)
@CrossOrigin
public class TaskController {

    // MinHeap to store pending tasks based on priority/deadline
    private final TaskMinHeap minHeap = new TaskMinHeap();

    // List to store completed tasks separately
    private final List<Task> completedTasks = new ArrayList<>();

     // GET endpoint to retrieve both pending and completed tasks
    @GetMapping
    public Map<String, List<Task>> getTasks() {

        // Create response map
        Map<String, List<Task>> response = new HashMap<>();

        // Get sorted pending tasks from MinHeap
        response.put("pending", minHeap.getSortedTasks());

        // Get completed tasks
        response.put("completed", completedTasks);
        return response;
    }

    // POST endpoint to add a new task
    @PostMapping
    public Task addTask(@RequestBody Task task) {

        // Set default status as PENDING
        task.setStatus("PENDING");
        
        // Insert task into MinHeap
        minHeap.insert(task);
        return task;
    }

    // PUT endpoint to mark a task as completed
    @PutMapping("/{id}/complete")
    public Task completeTask(@PathVariable String id) {

        // Remove task from MinHeap using task ID
        Task task = minHeap.remove(id);

        // If task exists, update status and move to completed list
        if (task != null) {
            task.setStatus("COMPLETED");
            completedTasks.add(task);
            return task;
        }

        // Throw error if task not found
        throw new RuntimeException("Task not found with id " + id);
    }

    // Feature 4: Novelty 2 - Auto-Reschedule Endpoint
    @PutMapping("/{id}/reschedule")
    public void rescheduleTask(@PathVariable String id, @RequestParam String newDeadline) {
        minHeap.updateDeadline(id, java.time.LocalDateTime.parse(newDeadline));
    }
}
