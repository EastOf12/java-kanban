package handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        method = httpExchange.getRequestMethod();
        patch = httpExchange.getRequestURI().getPath();

        switch (method) {
            case "GET":
                getTasks(httpExchange);
                break;
            case "POST":
                postTask(httpExchange);
                break;
            case "DELETE":
                deleteTask(httpExchange);
                break;
            default:
                sendAnswer(httpExchange, "Bad Request", 400, contentTypeText);
        }
    }

    @Override
    public void getTasks(HttpExchange httpExchange) throws IOException {
        String[] pathSegments = patch.split("/");

        if (pathSegments.length >= 3) {

            if (writeTaskID(httpExchange, pathSegments)) {
                return; // Некорректно подан id таска.
            }
            ;

            //Получаем таск.
            Task task = taskManager.getTask(taskId);

            //Отправляем ответ.
            if (task != null) {
                String json = gson.toJson(task);
                sendAnswer(httpExchange, json, 200, contentTypeJson);
            } else {
                sendAnswer(httpExchange, "Task not found", 404, contentTypeText);
            }

        } else {

            //Возвращаем список всех тасков.
            ArrayList<Task> tasks = taskManager.getAllTask();
            String json = gson.toJson(tasks);
            sendAnswer(httpExchange, json, 200, contentTypeJson);
        }
    } //Обработать Get запросы.

    @Override
    public void postTask(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task;

        //Проверяем корретность заполнения параметров для создания таска.
        try {
            task = gson.fromJson(body, Task.class);
        } catch (JsonSyntaxException e) {
            sendAnswer(httpExchange, "Bad Request", 400, contentTypeText);
            return;
        }

        String[] pathSegments = patch.split("/");

        if (pathSegments.length >= 3) {

            if (writeTaskID(httpExchange, pathSegments)) {
                return; // Некорректно подан id таска.
            }
            ;

            if (taskManager.getTask(taskId) != null) {
                //Нашли таск, который нужно обновить.
                task.setIdTask(taskId);
                boolean result = taskManager.updateTask(task);

                if (result) {
                    sendAnswer(httpExchange, "Request success", 201, contentTypeText);
                } else {
                    //Есть пересения с дургими задачами.
                    sendAnswer(httpExchange, "Time intersection with another task", 406, contentTypeText);
                }
            } else {
                //Нет таска, который нужно обновить.
                sendAnswer(httpExchange, "Task not found", 404, contentTypeText);
            }

        } else {
            //Создание нового таска
            int allTaskSize = taskManager.getPrioritizedTasks().size();
            taskManager.createTask(task);

            if (allTaskSize < taskManager.getPrioritizedTasks().size()) {
                sendAnswer(httpExchange, "Request success", 201, contentTypeText);
            } else {
                //Пересечение с другими тасками.
                sendAnswer(httpExchange, "Time intersection with another task", 406, contentTypeText);
            }
        }
    }

    @Override
    public void deleteTask(HttpExchange httpExchange) throws IOException {
        String[] pathSegments = patch.split("/");

        if (pathSegments.length >= 3) {

            if (writeTaskID(httpExchange, pathSegments)) {
                return; // Некорректно подан id таска.
            }
            ;

            //Отправляем ответ.
            if (taskManager.removalTask(taskId)) {
                sendAnswer(httpExchange, "Task deleted", 200, contentTypeText);
            } else {
                sendAnswer(httpExchange, "Task not found", 404, contentTypeText);
            }
        } else {
            //Не получили id таска, который нужно удалить
            sendAnswer(httpExchange, "Bad Request", 400, contentTypeText);
        }
    }
}
