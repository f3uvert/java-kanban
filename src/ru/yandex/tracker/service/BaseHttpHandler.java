package ru.yandex.tracker.service;

import com.google.gson.Gson;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    protected BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new Gson();
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendSuccess(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"message\":\"Operation completed successfully\"}", 200);
    }

    protected void sendCreated(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"message\":\"Resource created successfully\"}", 201);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Resource not found\"}", 404);
    }

    protected void sendNotAcceptable(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Task time conflict\"}", 406);
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\":\"Internal server error\"}", 500);
    }

    protected <T> T readRequest(InputStream input, Class<T> clazz) throws IOException {
        String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        return gson.fromJson(body, clazz);
    }
}