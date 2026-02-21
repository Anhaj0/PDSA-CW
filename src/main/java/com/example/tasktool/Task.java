package com.example.tasktool;

import java.time.LocalDateTime;
import java.util.UUID;

public class Task {
    private String id;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private String status; // "PENDING" or "COMPLETED"
    private double estimatedDurationHours; // Feature 3: Stress Score Input

    public Task() {
        this.id = UUID.randomUUID().toString();
        this.status = "PENDING";
        this.estimatedDurationHours = 1.0; // Default 1 hour
    }

    public Task(String title, String description, LocalDateTime deadline, double estimatedDurationHours) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = "PENDING";
        this.estimatedDurationHours = estimatedDurationHours;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public double getEstimatedDurationHours() {
        return estimatedDurationHours;
    }

    public void setEstimatedDurationHours(double estimatedDurationHours) {
        this.estimatedDurationHours = estimatedDurationHours;
    }

    // Feature 3: Novelty 1 - Stress Score
    // Formula: (Hours until deadline) / Estimated Duration
    // If < 1.0, it means you have less time than needed (High Stress!)
    public double getStressScore() {
        if (deadline == null) return 100.0; // No deadine, no stress
        
        java.time.Duration timeUntilDeadline = java.time.Duration.between(LocalDateTime.now(), deadline);
        double hoursLeft = timeUntilDeadline.toMinutes() / 60.0;
        
        if (hoursLeft <= 0) return 0.0; // Overdue = Max Stress (0 score)
        if (estimatedDurationHours <= 0) return 100.0; // No duration = No stress
        
        return hoursLeft / estimatedDurationHours;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", deadline=" + deadline +
                ", status='" + status + '\'' +
                '}';
    }
}
