package com.sit223;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {

    public static void main(String[] args) throws IOException {
        TaskService taskService = new TaskService();

        taskService.addTask("Build Jenkins Pipeline");
        taskService.addTask("Run Automated Tests");
        taskService.addTask("Deploy Application");

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", exchange -> {
            String response =
                    "SIT223 DevOps Task Manager\n" +
                    "Total tasks: " + taskService.getTaskCount();

            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream output = exchange.getResponseBody()) {
                output.write(response.getBytes());
            }
        });

        server.createContext("/health", exchange -> {
            String response = "UP";

            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream output = exchange.getResponseBody()) {
                output.write(response.getBytes());
            }
        });

        server.start();

        System.out.println("SIT223 DevOps Task Manager started.");
        System.out.println("Application: http://localhost:8080");
        System.out.println("Health check: http://localhost:8080/health");
    }
}