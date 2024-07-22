package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    protected void getTasks(HttpExchange httpExchange) throws IOException {
        String json = gson.toJson(taskManager.getHistory());
        sendAnswer(httpExchange, json, 200, contentTypeJson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        method = httpExchange.getRequestMethod();
        patch = httpExchange.getRequestURI().getPath();

        if (method.equals("GET")) {
            getTasks(httpExchange);
        } else {
            sendAnswer(httpExchange, "Bad Request", 400, contentTypeText);
        }
    }
}
