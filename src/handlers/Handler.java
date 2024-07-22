package handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface Handler {
    void getTasks(HttpExchange httpExchange) throws IOException;

    void postTask(HttpExchange httpExchange) throws IOException;

    void deleteTask(HttpExchange httpExchange) throws IOException;
}
