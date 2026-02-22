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
        Map<String, List<Task>> response = new HashMap<>();
        response.put("pending", minHeap.getSortedTasks());
        response.put("completed", completedTasks);
        return response;
    }

    @PostMapping
    public Task addTask(@RequestBody Task task) {
        task.setStatus("PENDING");
        minHeap.insert(task);
        return task;
    }

    @PutMapping("/{id}/complete")
    public Task completeTask(@PathVariable String id) {
        Task task = minHeap.remove(id);
        if (task != null) {
            task.setStatus("COMPLETED");
            completedTasks.add(task);
            return task;
        }
        throw new RuntimeException("Task not found with id " + id);
    }

    // Feature 4: Novelty 2 - Auto-Reschedule Endpoint
    @PutMapping("/{id}/reschedule")
    public void rescheduleTask(@PathVariable String id, @RequestParam String newDeadline) {
        minHeap.updateDeadline(id, java.time.LocalDateTime.parse(newDeadline));
    }
}
