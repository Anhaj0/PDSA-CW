package com.example.tasktool;

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskMinHeap pendingHeap = new TaskMinHeap();
    private final List<TaskDTO> completedTasks = new ArrayList<>();
    
    // Assuming tasks are uniquely identifiable by an incrementing ID
    private int idCounter = 1;

    // Inner class to match frontend JSON structure
    static class TaskDTO {
        public String id;
        public String title;
        public String description;
        public String deadline; // ISO string
        public double estimatedDurationHours;
        public double stressScore;

        public TaskDTO() {}

        public TaskDTO(String id, String title, String description, String deadline, double estimatedDurationHours, double stressScore) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.deadline = deadline;
            this.estimatedDurationHours = estimatedDurationHours;
            this.stressScore = stressScore;
        }
    }
    
    // To preserve the string deadline and description (since Task doesn't have them)
    private final Map<String, TaskDTO> taskDataStore = new HashMap<>();

    @GetMapping
    public Map<String, Object> getTasks() {
        // Build pending list from the min heap without destroying it
        List<Task> pendingCore = new ArrayList<>();
        TaskMinHeap tempHeap = new TaskMinHeap();
        
        // Peek/extract all to sort, then put back
        while (!pendingHeap.isEmpty()) {
            pendingCore.add(pendingHeap.extractMin());
        }
        
        List<TaskDTO> pendingDTOs = new ArrayList<>();
        // Restore heap and build DTOs
        for (Task t : pendingCore) {
            pendingHeap.insert(t); // put it back
            TaskDTO dto = taskDataStore.get(t.getId());
            // dynamically update time remaining and stress score
            double currentTR = calculateTimeRemaining(dto.deadline);
            t.setTimeRemaining(currentTR);
            dto.stressScore = t.getPressureScore();
            pendingDTOs.add(dto);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("pending", pendingDTOs);
        response.put("completed", completedTasks);
        return response;
    }

    @PostMapping
    public TaskDTO addTask(@RequestBody TaskDTO dto) {
        dto.id = String.valueOf(idCounter++);
        
        double timeRemaining = calculateTimeRemaining(dto.deadline);
        
        Task task = new Task(dto.id, dto.title, dto.estimatedDurationHours, timeRemaining);
        dto.stressScore = task.getPressureScore();
        
        taskDataStore.put(dto.id, dto);
        pendingHeap.insert(task);
        
        return dto;
    }

    @PutMapping("/{id}/complete")
    public void completeTask(@PathVariable String id) {
        Task removed = pendingHeap.remove(id);
        if (removed != null) {
            TaskDTO dto = taskDataStore.remove(id);
            if (dto != null) {
                completedTasks.add(dto);
            }
        }
    }

    @PutMapping("/{id}/reschedule")
    public void rescheduleTask(@PathVariable String id, @RequestParam String newDeadline) {
        TaskDTO dto = taskDataStore.get(id);
        if (dto != null) {
            dto.deadline = newDeadline;
            double timeRemaining = calculateTimeRemaining(newDeadline);
            pendingHeap.update(id, dto.estimatedDurationHours, timeRemaining);
            
            // Recalculate stress score based on updated task
            Task updatedTask = new Task(id, dto.title, dto.estimatedDurationHours, timeRemaining);
            dto.stressScore = updatedTask.getPressureScore();
        }
    }
    
    private double calculateTimeRemaining(String deadlineStr) {
        try {
            LocalDateTime deadline = LocalDateTime.parse(deadlineStr);
            LocalDateTime now = LocalDateTime.now();
            long minutes = ChronoUnit.MINUTES.between(now, deadline);
            return minutes / 60.0;
        } catch (Exception e) {
            return 1.0;
        }
    }
}
