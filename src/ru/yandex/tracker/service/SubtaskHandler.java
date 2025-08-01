package ru.yandex.tracker.service;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.tracker.model.SubTask;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            if (parts.length == 2) {
                handleCollectionRequest(exchange);
            } else if (parts.length == 3) {
                handleSingleRequest(exchange, Integer.parseInt(parts[2]));
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleCollectionRequest(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                String response = gson.toJson(taskManager.getSubtasks());
                sendText(exchange, response, 200);
                break;
            case "POST":
                SubTask subTask = readRequest(exchange.getRequestBody(), SubTask.class);
                try {
                    if (subTask.getUniqueId() == 0) {
                        taskManager.addNewSubTask(subTask);
                        sendCreated(exchange);
                    } else {
                        taskManager.updateSubTask(subTask);
                        sendSuccess(exchange);
                    }
                } catch (IllegalStateException e) {
                    sendNotAcceptable(exchange);
                }
                break;
            case "DELETE":
                taskManager.deleteSubTasks();
                sendSuccess(exchange);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleSingleRequest(HttpExchange exchange, int id) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                SubTask subTask = taskManager.getSubTask(id);
                if (subTask != null) {
                    sendText(exchange, gson.toJson(subTask), 200);
                } else {
                    sendNotFound(exchange);
                }
                break;
            case "DELETE":
                taskManager.deleteSubTask(id);
                sendSuccess(exchange);
                break;
            default:
                sendNotFound(exchange);
        }
    }
}