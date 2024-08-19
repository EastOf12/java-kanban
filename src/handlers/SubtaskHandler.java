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

public class SubtaskHandler extends BaseHttpHandler implements Handler {
    public SubtaskHandler(TaskManager taskManager) {
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
            Subtask subtask = taskManager.getSubtask(taskId);

            //Отправляем ответ.
            if (subtask != null) {
                String json = gson.toJson(subtask);
                sendAnswer(httpExchange, json, 200, contentTypeJson);
            } else {
                sendAnswer(httpExchange, "Subtask not found", 404, contentTypeText);
            }

        } else {

            //Возвращаем список всех тасков.
            ArrayList<Subtask> subtasks = taskManager.getAllSubtask();
            String json = gson.toJson(subtasks);
            sendAnswer(httpExchange, json, 200, contentTypeJson);
        }
    }

    @Override
    public void postTask(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask;

        //Проверяем корретность заполнения параметров для создания таска.
        try {
            subtask = gson.fromJson(body, Subtask.class);
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

            int idEpicSubtask = subtask.getIdEpic();

            if (checkIdEpic(httpExchange, idEpicSubtask)) {
                return;
            } //Полученный id эпика не корректен.

            if (taskManager.getSubtask(taskId) != null) {
                //Нашли таск, который нужно обновить.
                subtask.setIdTask(taskId);
                boolean result = taskManager.updateSubtask(subtask);

                if (result) {
                    sendAnswer(httpExchange, "Request success", 201, contentTypeText);
                } else {
                    //Есть пересения с дургими задачами.
                    sendAnswer(httpExchange, "Time intersection with another subtask", 406, contentTypeText);
                }
            } else {
                //Нет таска, который нужно обновить.
                sendAnswer(httpExchange, "Subtask not found", 404, contentTypeText);
            }

        } else {
            //Создание нового таска
            int allTaskSize = taskManager.getPrioritizedTasks().size();
            int idEpicSubtask = subtask.getIdEpic();

            if (checkIdEpic(httpExchange, idEpicSubtask)) {
                return;
            } //Полученный id эпика не корректен.

            taskManager.createSubtask(subtask);

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

            //Отправляем ответ.
            if (taskManager.removalSubtask(taskId)) {
                sendAnswer(httpExchange, "Subtask deleted", 200, contentTypeText);
            } else {
                sendAnswer(httpExchange, "Subtask not found", 404, contentTypeText);
            }

        } else {
            taskManager.clearAllSubtask();
            sendAnswer(httpExchange, "Subtasks deleted", 200, contentTypeText);
        }
    }

    private boolean checkIdEpic(HttpExchange httpExchange, int idEpicSubtask) throws IOException {
        for (Epic epic : taskManager.getAllEpic()) {
            if (epic.getIdTask() == idEpicSubtask) {
                return false;
            }
        }

        sendAnswer(httpExchange, "Epic for Subtask not found", 404, contentTypeText);
        return true;
    } // Проверяет, существует ли эпик с полученным id.
}
