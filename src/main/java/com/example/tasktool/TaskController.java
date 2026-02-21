package com.example.tasktool;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin
public class TaskController {

    private final TaskMinHeap minHeap = new TaskMinHeap();
    private final List<Task> completedTasks = new ArrayList<>();

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
