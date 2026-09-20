package com.sit223;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TaskServiceIntegrationTest {

    @Test
    void shouldCompleteFullTaskWorkflow() {

        TaskService taskService = new TaskService();

        assertTrue(taskService.addTask("Build Application"));
        assertTrue(taskService.addTask("Run Tests"));
        assertTrue(taskService.addTask("Deploy Application"));

        assertEquals(3, taskService.getTaskCount());

        assertTrue(taskService.containsTask("Run Tests"));

        assertTrue(taskService.removeTask("Run Tests"));

        assertEquals(2, taskService.getTaskCount());
        assertFalse(taskService.containsTask("Run Tests"));

        taskService.clearTasks();

        assertEquals(0, taskService.getTaskCount());
    }
}