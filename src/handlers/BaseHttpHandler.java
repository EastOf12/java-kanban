package handlers;

import adapters.DurationTypeAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

abstract class BaseHttpHandler implements HttpHandler {
    protected TaskManager taskManager;
    protected String method;
    protected String patch;
    protected String contentTypeText = "text/plain; charset=UTF-8";
    protected String contentTypeJson = "application/json;charset=utf-8";
    protected int taskId;
    protected Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .create();

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendAnswer(HttpExchange httpExchange, String body, int rCode, String contentType) throws IOException {

        byte[] resp = body.getBytes(StandardCharsets.UTF_8);

        httpExchange.getResponseHeaders().add("Content-Type", contentType);
        httpExchange.sendResponseHeaders(rCode, resp.length);
        httpExchange.getResponseBody().write(resp);
        httpExchange.close();
    } //Отправляет клиенту ответ.

    protected boolean writeTaskID(HttpExchange httpExchange, String[] pathSegments) throws IOException {
        boolean result = true;

        try {
            taskId = Integer.parseInt(pathSegments[2]);
        } catch (NumberFormatException e) {
            sendAnswer(httpExchange, "Bad Request", 400, contentTypeText);
            result = false;
        }

        return !result;
    } //Проверяем корретность заполнения id Таска.
}
