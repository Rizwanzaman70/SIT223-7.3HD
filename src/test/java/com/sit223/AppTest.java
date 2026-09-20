package com.sit223;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppTest {

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService();
    }

    @Test
    void shouldAddTask() {
        boolean result = taskService.addTask("Build Jenkins Pipeline");

        assertTrue(result);
        assertEquals(1, taskService.getTaskCount());
        assertTrue(taskService.containsTask("Build Jenkins Pipeline"));
    }

    @Test
    void shouldRejectEmptyTask() {
        boolean result = taskService.addTask("");

        assertFalse(result);
        assertEquals(0, taskService.getTaskCount());
    }

    @Test
    void shouldRejectNullTask() {
        boolean result = taskService.addTask(null);

        assertFalse(result);
        assertEquals(0, taskService.getTaskCount());
    }

    @Test
    void shouldRemoveTask() {
        taskService.addTask("Run Tests");

        boolean result = taskService.removeTask("Run Tests");

        assertTrue(result);
        assertEquals(0, taskService.getTaskCount());
    }

    @Test
    void shouldCountTasks() {
        taskService.addTask("Build");
        taskService.addTask("Test");
        taskService.addTask("Deploy");

        assertEquals(3, taskService.getTaskCount());
    }

    @Test
    void shouldClearTasks() {
        taskService.addTask("Build");
        taskService.addTask("Deploy");

        taskService.clearTasks();

        assertEquals(0, taskService.getTaskCount());
    }
}