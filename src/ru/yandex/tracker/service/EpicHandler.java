package ru.yandex.tracker.service;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.tracker.model.Epic;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager taskManager) {
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
            } else if (parts.length == 4 && "subtasks".equals(parts[3])) {
                handleEpicSubtasks(exchange, Integer.parseInt(parts[2]));
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
                String response = gson.toJson(taskManager.getEpics());
                sendText(exchange, response, 200);
                break;
            case "POST":
                Epic epic = readRequest(exchange.getRequestBody(), Epic.class);
                if (epic.getUniqueId() == 0) {
                    taskManager.addNewEpic(epic);
                    sendCreated(exchange);
                } else {
                    taskManager.updateEpic(epic);
                    sendSuccess(exchange);
                }
                break;
            case "DELETE":
                taskManager.deleteEpics();
                sendSuccess(exchange);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleSingleRequest(HttpExchange exchange, int id) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                Epic epic = taskManager.getEpic(id);
                if (epic != null) {
                    sendText(exchange, gson.toJson(epic), 200);
                } else {
                    sendNotFound(exchange);
                }
                break;
            case "DELETE":
                taskManager.deleteEpic(id);
                sendSuccess(exchange);
                break;
            default:
                sendNotFound(exchange);
        }
    }

    private void handleEpicSubtasks(HttpExchange exchange, int epicId) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            String response = gson.toJson(taskManager.getEpicSubtasks(epicId));
            sendText(exchange, response, 200);
        } else {
            sendNotFound(exchange);
        }
    }
}