package com.example.tasktool;

import java.util.ArrayList;
import java.util.List;

public class TaskMinHeap {
    private List<Task> heap;

    public TaskMinHeap() {
        this.heap = new ArrayList<>();
    }

    // Insert a new task and maintain heap property
    public void insert(Task task) {
        heap.add(task);
        heapifyUp(heap.size() - 1);
    }

    // Extract the task with the earliest deadline (root)
    public Task extractMin() {
        if (heap.isEmpty()) {
            return null;
        }
        Task min = heap.get(0);
        Task last = heap.remove(heap.size() - 1);
        
        if (!heap.isEmpty()) {
            heap.set(0, last);
            heapifyDown(0);
        }
        return min;
    }

    // Peek at the top task without removing
    public Task peek() {
        if (heap.isEmpty()) return null;
        return heap.get(0);
    }

    // Remove a specific task by ID
    public Task remove(String id) {
        for (int i = 0; i < heap.size(); i++) {
            if (heap.get(i).getId().equals(id)) {
                Task removed = heap.get(i);
                Task last = heap.remove(heap.size() - 1);
                // If we removed the last element, we are done
                if (i == heap.size()) return removed;

                // Replace removed element with last element
                heap.set(i, last);
                // We might need to bubble up or down depending on the value
                // Simplification: Try both since only one will trigger
                heapifyDown(i);
                heapifyUp(i); 
                return removed;
            }
        }
        return null;
    }
    
    // Returns a sorted list of pending tasks (non-destructive)
    public List<Task> getSortedTasks() {
        List<Task> sorted = new ArrayList<>();
        List<Task> copy = new ArrayList<>(heap); // Clone the list
        
        // We need a temporary heap to extract from
        TaskMinHeap tempHeap = new TaskMinHeap();
        tempHeap.heap = copy; // Inject the copy
        // Note: Copying the ArrayList doesn't clone the Tasks, but that's fine for reading.
        // However, we need to extract from the tempHeap structure.
        // The structure of 'copy' is already a valid heap.
        // So we can just repeatedly call extractMin on the tempHeap.
        
        while (tempHeap.peek() != null) {
            sorted.add(tempHeap.extractMin());
        }
        return sorted;
    }

    // Feature 4: Novelty 2 - Auto-Reschedule
    // Updates the deadline of a task and fixes the heap property
    public void updateDeadline(String id, java.time.LocalDateTime newDeadline) {
        for (int i = 0; i < heap.size(); i++) {
            if (heap.get(i).getId().equals(id)) {
                Task task = heap.get(i);
                java.time.LocalDateTime oldDeadline = task.getDeadline();
                task.setDeadline(newDeadline);

                // If new deadline is earlier, it might need to move UP (higher priority)
                if (newDeadline.isBefore(oldDeadline)) {
                    heapifyUp(i);
                } 
                // If new deadline is later, it might need to move DOWN (lower priority)
                else {
                    heapifyDown(i);
                }
                return;
            }
        }
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    // Helper: Move element up to restore heap property
    private void heapifyUp(int index) {
        int parentIndex = (index - 1) / 2;
        while (index > 0 && heap.get(index).getDeadline().isBefore(heap.get(parentIndex).getDeadline())) {
            swap(index, parentIndex);
            index = parentIndex;
            parentIndex = (index - 1) / 2;
        }
    }

    // Helper: Move element down to restore heap property
    private void heapifyDown(int index) {
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;
        int smallest = index;

        if (leftChild < heap.size() && heap.get(leftChild).getDeadline().isBefore(heap.get(smallest).getDeadline())) {
            smallest = leftChild;
        }

        if (rightChild < heap.size() && heap.get(rightChild).getDeadline().isBefore(heap.get(smallest).getDeadline())) {
            smallest = rightChild;
        }

        if (smallest != index) {
            swap(index, smallest);
            heapifyDown(smallest);
        }
    }

    private void swap(int i, int j) {
        Task temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}
