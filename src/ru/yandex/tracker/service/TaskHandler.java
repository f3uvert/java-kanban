package ru.yandex.tracker.service;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.tracker.model.Task;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }


    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGetTasks(exchange);
                    break;
                case "POST":
                    handlePostTask(exchange);
                    break;
                case "DELETE":
                    handleDeleteTasks(exchange);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getTasks());
        sendText(exchange, response, 200);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        Task task = readRequest(exchange.getRequestBody(), Task.class);
        try {
            if (task.getUniqueId() == 0) {
                taskManager.addNewTask(task);
                sendCreated(exchange);
            } else {
                taskManager.updateTask(task);
                sendSuccess(exchange);
            }
        } catch (IllegalStateException e) {
            sendNotAcceptable(exchange);
        }
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteTasks();
        sendSuccess(exchange);
    }
}