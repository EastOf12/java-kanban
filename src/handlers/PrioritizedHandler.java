package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void getTasks(HttpExchange httpExchange) throws IOException {
        String json = gson.toJson(taskManager.getPrioritizedTasks());
        sendAnswer(httpExchange, json, 200, contentTypeJson);
    }

    @Override
    protected void postTask(HttpExchange httpExchange) throws IOException {
    }

    @Override
    protected void deleteTask(HttpExchange httpExchange) throws IOException {
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
