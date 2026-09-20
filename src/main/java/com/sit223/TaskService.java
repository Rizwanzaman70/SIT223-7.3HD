package com.sit223;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskService {

    private final List<String> tasks = new ArrayList<>();

    public boolean addTask(String task) {
        if (task == null || task.trim().isEmpty()) {
            return false;
        }

        tasks.add(task.trim());
        return true;
    }

    public boolean removeTask(String task) {
        return tasks.remove(task);
    }

    public List<String> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public int getTaskCount() {
        return tasks.size();
    }

    public boolean containsTask(String task) {
        return tasks.contains(task);
    }

    public void clearTasks() {
        tasks.clear();
    }
}