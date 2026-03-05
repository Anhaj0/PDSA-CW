package com.example.tasktool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskMinHeap {
    private List<Task> heap;
    private HashMap<String, Integer> indexMap;

    public TaskMinHeap() {
        this.heap = new ArrayList<>();
        this.indexMap = new HashMap<>();
    }

    public void insert(Task task) {
        if (indexMap.containsKey(task.getId())) {
            update(task.getId(), task.getEstimatedDuration(), task.getTimeRemaining());
            return;
        }
        heap.add(task);
        int index = heap.size() - 1;
        indexMap.put(task.getId(), index);
        siftUp(index);
    }

    public Task extractMin() {
        if (heap.isEmpty()) {
            return null;
        }
        Task min = heap.get(0);
        indexMap.remove(min.getId());
        Task last = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, last);
            indexMap.put(last.getId(), 0);
            siftDown(0);
        }
        return min;
    }

    public Task peek() {
        if (heap.isEmpty()) return null;
        return heap.get(0);
    }

    public Task remove(String id) {
        Integer index = indexMap.get(id);
        if (index == null) return null;

        Task removed = heap.get(index);
        indexMap.remove(id);

        int lastIndex = heap.size() - 1;
        Task last = heap.remove(lastIndex);

        if (index != lastIndex && !heap.isEmpty()) {
            heap.set(index, last);
            indexMap.put(last.getId(), index);
            siftUp(index);
            siftDown(index);
        }
        return removed;
    }

    public void update(String id, double newEstimatedDuration, double newTimeRemaining) {
        Integer index = indexMap.get(id);
        if (index == null) return;

        Task task = heap.get(index);
        task.setEstimatedDuration(newEstimatedDuration);
        task.setTimeRemaining(newTimeRemaining);

        siftUp(index);
        siftDown(index);
    }

    private void siftUp(int index) {
        int parentIndex = (index - 1) / 2;
        // True MIN HEAP: Smallest time remaining (closest deadline) bubbles to the root
        while (index > 0 && heap.get(index).getTimeRemaining() < heap.get(parentIndex).getTimeRemaining()) {
            swap(index, parentIndex);
            index = parentIndex;
            parentIndex = (index - 1) / 2;
        }
    }

    private void siftDown(int index) {
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;
        int smallest = index;

        if (leftChild < heap.size() && heap.get(leftChild).getTimeRemaining() < heap.get(smallest).getTimeRemaining()) {
            smallest = leftChild;
        }

        if (rightChild < heap.size() && heap.get(rightChild).getTimeRemaining() < heap.get(smallest).getTimeRemaining()) {
            smallest = rightChild;
        }

        if (smallest != index) {
            swap(index, smallest);
            siftDown(smallest);
        }
    }

    private void swap(int i, int j) {
        Task temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);

        indexMap.put(heap.get(i).getId(), i);
        indexMap.put(heap.get(j).getId(), j);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public void printHeap() {
        for (Task t : heap) {
            System.out.println(t);
        }
    }
}
