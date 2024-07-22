package handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements Handler {
    public EpicHandler(TaskManager taskManager) {
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
                return; // Некорректно подан id.
            }

            Epic epic = taskManager.getEpic(taskId);

            //Отправляем ответ.
            if (epic != null) {
                if (pathSegments.length == 4 && pathSegments[3].equals("subtasks")) {
                    List<Subtask> subtasks = taskManager.getAllSubtask().stream()
                            .filter(subtask -> subtask.getIdEpic() == epic.getIdTask())
                            .toList();

                    String json = gson.toJson(subtasks);
                    sendAnswer(httpExchange, json, 200, contentTypeJson);
                } else if (pathSegments.length == 4) {
                    sendAnswer(httpExchange, "Bad Request", 400, contentTypeText);
                } else {
                    String json = gson.toJson(epic);
                    sendAnswer(httpExchange, json, 200, contentTypeJson);
                }
            } else {
                sendAnswer(httpExchange, "Epic not found", 404, contentTypeText);
            }

        } else {
            //Возвращаем список всех тасков.
            ArrayList<Epic> epics = taskManager.getAllEpic();
            String json = gson.toJson(epics);
            sendAnswer(httpExchange, json, 200, contentTypeJson);
        }
    }

    @Override
    public void postTask(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic;

        //Проверяем корретность заполнения параметров для создания таска.
        try {
            epic = gson.fromJson(body, Epic.class);
        } catch (JsonSyntaxException e) {
            sendAnswer(httpExchange, "Bad Request", 400, contentTypeText);
            return;
        }

        String[] pathSegments = patch.split("/");

        if (pathSegments.length >= 3) {

            if (writeTaskID(httpExchange, pathSegments)) {
                return; // Некорректно подан id эпика.
            }
            ;

            epic.setIdTask(taskId);
            boolean result = taskManager.updateEpic(epic);

            if (result) {
                sendAnswer(httpExchange, "Request success", 201, contentTypeText);
            } else {
                //Нет эпика, который нужно обновить.
                sendAnswer(httpExchange, "Epic not found", 404, contentTypeText);
            }
        } else {
            //Создание нового эпика
            taskManager.createEpic(new Epic(epic.getTitle(), epic.getDescription()));
            sendAnswer(httpExchange, "Request success", 201, contentTypeText);
        }
    }

    @Override
    public void deleteTask(HttpExchange httpExchange) throws IOException {
        String[] pathSegments = patch.split("/");

        if (pathSegments.length >= 3) {

            if (writeTaskID(httpExchange, pathSegments)) {
                return; // Некорректно подан id эпика.
            }

            //Отправляем ответ.
            if (taskManager.removalEpic(taskId)) {
                sendAnswer(httpExchange, "Epic deleted", 200, contentTypeText);
            } else {
                sendAnswer(httpExchange, "Epic not found", 404, contentTypeText);
            }
        } else {
            //Удаляем все эпики и их подзадачи.
            taskManager.clearAllEpic();
            sendAnswer(httpExchange, "Epics deleted", 200, contentTypeText);
        }
    }
}
