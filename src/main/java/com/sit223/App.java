package com.sit223;

public class App {

    public static void main(String[] args) {
        TaskService taskService = new TaskService();

        taskService.addTask("Build Jenkins Pipeline");
        taskService.addTask("Run Automated Tests");
        taskService.addTask("Deploy Application");

        System.out.println("SIT223 DevOps Task Manager");
        System.out.println("--------------------------");

        for (String task : taskService.getTasks()) {
            System.out.println("- " + task);
        }

        System.out.println("--------------------------");
        System.out.println("Total tasks: " + taskService.getTaskCount());
    }
}