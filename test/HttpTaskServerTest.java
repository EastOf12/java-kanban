import adapters.DurationTypeAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import handlers.*;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class HttpTaskServerTest {
    HttpServer httpServer;
    TaskManager taskManager;
    Gson gson;

    @BeforeEach
    public void beforeEach() throws IOException {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();

        int PORT = 8080;
        taskManager = new InMemoryTaskManager();
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        //Обрабатываем пути и запускаем сервер.
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpServer.stop(0);
    }

    //Тесты для tasks
    @Test
    public void shouldReturnPositiveWhenGetTasksIsCorrect() throws IOException, InterruptedException {

        //Создаем задачи
        Task task1 = new Task("Найти работу", "Найти работу с зарплатой 1000к",
                LocalDateTime.of(2024, 12, 1, 1, 1, 1), Duration.ofDays(2));
        Task task2 = new Task("Сходить в магазин", "Купить еду в магазине",
                LocalDateTime.of(2023, 12, 1, 1, 1, 1), Duration.ofDays(2));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        //Отправляем запрос.
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем ответ на корректность.
        Assertions.assertEquals(response.statusCode(), 200, "Код ответа должен быть равен 200.");

        Type taskListType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        ArrayList<Task> tasks = gson.fromJson(response.body(), taskListType);

        Assertions.assertEquals(tasks.getFirst(), taskManager.getTask(1), "Таски должны быть равны");
        Assertions.assertEquals(tasks.getLast(), taskManager.getTask(2), "Таски должны быть равны");
    }

    @Test
    public void shouldReturnPositiveWhenGetTasksFromIdIsCorrect() throws IOException, InterruptedException {
        //Создаем задачи
        Task task = new Task("Найти работу", "Найти работу с зарплатой 1000к",
                LocalDateTime.of(2024, 12, 1, 1, 1, 1), Duration.ofDays(2));

        taskManager.createTask(task);

        //Отправляем запрос и получаем успешный ответ.
        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем ответ на корректность.
        Assertions.assertEquals(response.statusCode(), 200, "Код ответа должен быть равен 200.");

        Task taskNew = gson.fromJson(response.body(), Task.class);
        Assertions.assertEquals(taskNew, taskManager.getTask(1), "Таски должны быть равны");

        checkGetErrorCodes("tasks", 3);
    }

    @Test
    public void shouldReturnPositiveWhenPostTasksIsCorrectCreate() throws IOException, InterruptedException {
        //Создаем задачи
        Task task = new Task("Найти работу", "Найти работу с зарплатой 1000к",
                LocalDateTime.of(2024, 12, 1, 1, 1, 1), Duration.ofDays(2));
        task.setIdTask(1);
        task.setStatus(TaskStatus.NEW);

        //Формируем Json с параметрами таска.
        String taskJson = "{ " +
                "\"startTime\": \"2024-12-01T01:01:01\"," +
                "\"duration\": \"PT48H\"," +
                "\"title\": \"Найти работу\"," +
                "\"description\": \"Найти работу с зарплатой 1000к\"" +
                " }";

        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем код на корректность.
        Assertions.assertEquals(response.statusCode(), 201, "Код ответа должен быть равен 201.");

        //Проверяем корректность созданного таска.
        Assertions.assertEquals(taskManager.getTask(1), task, "Таски должны быть равны");

        //Проверяем корректность ответа, при создании таска, который пересекается с другими.
        response = httpClient.send(httpRequest, handler);
        Assertions.assertEquals(response.statusCode(), 406, "Код ответа должен быть равен 406.");
    }

    @Test
    public void shouldReturnPositiveWhenPostTasksIsCorrectUpdate() throws IOException, InterruptedException {
        //Создаем задачи
        Task task = new Task("Найти работу", "Найти работу с зарплатой 1000к",
                LocalDateTime.of(2024, 12, 1, 1, 1, 1), Duration.ofDays(2));

        taskManager.createTask(task);

        //Формируем Json с обновленными параметрами таска.
        String taskJson = "{ " +
                "\"startTime\": \"2024-12-01T01:01:01\"," +
                "\"duration\": \"PT48H\"," +
                "\"title\": \"Найти работу\"," +
                "\"description\": \"Найти работу с зарплатой 1000к\"," +
                "\"status\": \"" + TaskStatus.IN_PROGRESS + "\"" +
                " }";

        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем код на корректность.
        Assertions.assertEquals(response.statusCode(), 201, "Код ответа должен быть равен 201.");

        //Воссоздаем корректно обновленный таск.
        Task updatedTask = new Task("Найти работу", "Найти работу с зарплатой 1000к",
                LocalDateTime.of(2024, 12, 1, 1, 1, 1), Duration.ofDays(2));
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        updatedTask.setIdTask(1);

        //Сравниваем с обновлением таска на сервере.
        Assertions.assertEquals(taskManager.getTask(1), updatedTask, "Таски должны быть равны.");
    }

    @Test
    public void shouldReturnPositiveWhenDeleteTasksIsCorrect() throws IOException, InterruptedException {
        //Создаем задачи
        Task task = new Task("Найти работу", "Найти работу с зарплатой 1000к",
                LocalDateTime.of(2024, 12, 1, 1, 1, 1), Duration.ofDays(2));

        taskManager.createTask(task);

        //Проверяем количество задач до удаления.
        Assertions.assertEquals(taskManager.getAllTask().size(), 1, "Должна быть одно задача");

        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем код на корректность.
        Assertions.assertEquals(response.statusCode(), 200, "Код ответа должен быть равен 200.");

        //Проверяем удалилиась ли задача
        Assertions.assertEquals(taskManager.getAllTask().size(), 0, "Не должно быть задач");

        //Проверяем коды ошибок.
        checkGetErrorCodes("tasks", 2);
    }

    @Test
    public void shouldReturnPositiveWhenDeleteAllTasksIsCorrect() throws IOException, InterruptedException {
        //Создаем задачи
        //Обычные задачи
        Task task1 = new Task("Найти работу", "Найти работу с зарплатой 1000к",
                LocalDateTime.of(2024, 12, 1, 1, 1, 1), Duration.ofDays(2));
        Task task2 = new Task("Сходить в магазин", "Купить еду в магазине",
                LocalDateTime.of(2023, 12, 1, 1, 1, 1), Duration.ofDays(2));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        //Проверяем количество задач до удаления.
        Assertions.assertEquals(taskManager.getAllTask().size(), 2, "Должна быть одно задача");

        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем код на корректность.
        Assertions.assertEquals(response.statusCode(), 200, "Код ответа должен быть равен 200.");

        //Проверяем удалилиась ли задача
        Assertions.assertEquals(taskManager.getAllTask().size(), 0, "Не должно быть задач");

        //Проверяем коды ошибок.
        checkGetErrorCodes("tasks", 2);
    }

    //Тесты для subtasks
    @Test
    public void shouldReturnPositiveWhenGetSubtasksIsCorrect() throws IOException, InterruptedException {

        //Создаем эпик
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");

        //Создаем подзадачи
        Subtask subtask1 = new Subtask("Убрать войны", "Убрать все оружие в мире",
                1, LocalDateTime.of(2025, 12, 1, 1, 1, 1), Duration.ofDays(2));
        Subtask subtask2 = new Subtask("Дать всем еды", "Накормить всех", 1,
                LocalDateTime.of(2021, 12, 1, 1, 1, 1), Duration.ofDays(2));

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        //Отправляем запрос.
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем код ответа.
        Assertions.assertEquals(response.statusCode(), 200, "Код ответа должен быть равен 200.");

        //Проверям корректность ответа.
        Type subtaskListType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        ArrayList<Subtask> subtasks = gson.fromJson(response.body(), subtaskListType);

        Assertions.assertEquals(subtasks.getFirst(), taskManager.getSubtask(2)
                , "Подзадачи должны быть равны");
        Assertions.assertEquals(subtasks.getLast(), taskManager.getSubtask(3)
                , "Подзадачи должны быть равны");
    }

    @Test
    public void shouldReturnPositiveWhenGetSubtasksFromIdIsCorrect() throws IOException, InterruptedException {
        //Создаем эпик
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");

        //Создаем подзадачу
        Subtask subtask = new Subtask("Убрать войны", "Убрать все оружие в мире",
                1, LocalDateTime.of(2025, 12, 1, 1, 1, 1), Duration.ofDays(2));

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем ответ на корректность.
        Assertions.assertEquals(response.statusCode(), 200, "Код ответа должен быть равен 200.");

        Subtask subtaskNew = gson.fromJson(response.body(), Subtask.class);
        Assertions.assertEquals(subtaskNew, taskManager.getSubtask(2), "Подзадачи должны быть равны");

        checkGetErrorCodes("subtasks", 15);
    }

    @Test
    public void shouldReturnPositiveWhenPostSubtasksIsCorrectCreate() throws IOException, InterruptedException {
        //Создаем задачи
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        taskManager.createEpic(epic);

        //Создаем подзадачу
        Subtask subtask = new Subtask("Убрать войны", "Убрать все оружие в мире",
                1, LocalDateTime.of(2025, 12, 1, 1, 1, 1), Duration.ofDays(2));
        subtask.setIdTask(2);

        //Формируем Json с параметрами таска.
        String taskJson = "{\"idEpic\": 1," +
                "\"startTime\": \"2025-12-01T01:01:01\"," +
                "\"duration\": \"PT48H\"," +
                "\"title\": \"Убрать войны\"," +
                "\"description\": \"Убрать все оружие в мире\"" +
                "}";

        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем код на корректность.
        Assertions.assertEquals(response.statusCode(), 201, "Код ответа должен быть равен 201.");
        subtask.setIdTask(2);

        //Проверяем корректность созданного таска.
        Assertions.assertEquals(taskManager.getSubtask(2), subtask, "Подзадача должны быть равны");

        //Проверяем корректность ответа при создании таска, который пересекается с другими.
        response = httpClient.send(httpRequest, handler);
        Assertions.assertEquals(response.statusCode(), 406, "Код ответа должен быть равен 406.");
    }

    @Test
    public void shouldReturnPositiveWhenPostSubtasksIsCorrectUpdate() throws IOException, InterruptedException {
        //Создаем задачи
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        taskManager.createEpic(epic);

        //Создаем подзадачу
        Subtask subtask = new Subtask("Убрать войны", "Убрать все оружие в мире",
                1, LocalDateTime.of(2025, 12, 1, 1, 1, 1), Duration.ofDays(2));
        taskManager.createSubtask(subtask);

        //Формируем Json с обновленными параметрами таска.
        String taskJson = "{\"idEpic\": 1," +
                "\"startTime\": \"2025-12-01T01:01:01\"," +
                "\"duration\": \"PT48H\"," +
                "\"title\": \"Убрать войны\"," +
                "\"description\": \"Убрать все оружие в мире\"," +
                "\"status\": \"" + TaskStatus.IN_PROGRESS + "\"" +
                "}";

        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем код на корректность.
        Assertions.assertEquals(response.statusCode(), 201, "Код ответа должен быть равен 201.");

        //Воссоздаем корректно обновленный таск.
        Subtask subtaskUpdate = new Subtask("Убрать войны", "Убрать все оружие в мире",
                1, LocalDateTime.of(2025, 12, 1, 1, 1, 1), Duration.ofDays(2));
        subtaskUpdate.setStatus(TaskStatus.IN_PROGRESS);
        subtaskUpdate.setIdTask(2);

        //Сравниваем с обновлением таска на сервере.
        Assertions.assertEquals(taskManager.getSubtask(2), subtaskUpdate, "Таски должны быть равны.");


    }

    @Test
    public void shouldReturnPositiveWhenDeleteSubtasksIsCorrect() throws IOException, InterruptedException {
        //Создаем задачи
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        taskManager.createEpic(epic);

        //Создаем подзадачу
        Subtask subtask = new Subtask("Убрать войны", "Убрать все оружие в мире",
                1, LocalDateTime.of(2025, 12, 1, 1, 1, 1), Duration.ofDays(2));
        taskManager.createSubtask(subtask);

        //Проверяем количество задач до удаления.
        Assertions.assertEquals(taskManager.getAllSubtask().size(), 1, "Должна быть одно подзадача.");

        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //Проверяем код на корректность.
        Assertions.assertEquals(response.statusCode(), 200, "Код ответа должен быть равен 200.");

        //Проверяем удалилиась ли задача
        Assertions.assertEquals(taskManager.getAllSubtask().size(), 0, "Не должно быть подзадач");

        //Проверяем коды ошибок.
        checkGetErrorCodes("subtasks", 3);
    }

    @Test
    public void shouldReturnPositiveWhenDeleteAllSubtasksIsCorrect() throws IOException, InterruptedException {
        //Создаем задачи
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        taskManager.createEpic(epic);

        //Создаем подзадачи
        Subtask subtask1 = new Subtask("Убрать войны", "Убрать все оружие в мире",
                1, LocalDateTime.of(2025, 12, 1, 1, 1, 1), Duration.ofDays(2));
        Subtask subtask2 = new Subtask("Дать всем еды", "Накормить всех", 1,
                LocalDateTime.of(2021, 12, 1, 1, 1, 1), Duration.ofDays(2));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        //Проверяем количество задач до удаления.
        Assertions.assertEquals(taskManager.getAllSubtask().size(), 2, "Должно быть две подзадачи.");

        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //Проверяем код на корректность.
        Assertions.assertEquals(response.statusCode(), 200, "Код ответа должен быть равен 200.");

        //Проверяем удалилиась ли задача
        Assertions.assertEquals(taskManager.getAllSubtask().size(), 0, "Не должно быть подзадач");

        //Проверяем коды ошибок.
        checkGetErrorCodes("subtasks", 3);
    }

    //Тесты для epics
    @Test
    public void shouldReturnPositiveWhenGetEpicsIsCorrect() throws IOException, InterruptedException {

        //Создаем эпики
        Epic epic1 = new Epic("Построить мир", "Организовать мир во всем мире.");
        Epic epic2 = new Epic("Полететь на марс", "Прилететь на марс и организовать там колонию.");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);


        //Отправляем запрос.
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем код ответа.
        Assertions.assertEquals(response.statusCode(), 200, "Код ответа должен быть равен 200.");

        //Проверям корректность ответа.
        Type epicListType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        ArrayList<Epic> epics = gson.fromJson(response.body(), epicListType);
        Assertions.assertEquals(epics.getFirst(), taskManager.getEpic(1)
                , "Эпики должны быть равны");
        Assertions.assertEquals(epics.getLast(), taskManager.getEpic(2)
                , "Эпики должны быть равны");
    }

    @Test
    public void shouldReturnPositiveWhenGetEpicsFromIdIsCorrect() throws IOException, InterruptedException {
        //Создаем эпики
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        taskManager.createEpic(epic);

        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/epics/1");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем ответ на корректность.
        Assertions.assertEquals(response.statusCode(), 200, "Код ответа должен быть равен 200.");

        Epic epicNew = gson.fromJson(response.body(), Epic.class);
        Assertions.assertEquals(epicNew, taskManager.getEpic(1), "Эпики должны быть равны");

        checkGetErrorCodes("epics", 15);
    }

    @Test
    public void shouldReturnPositiveWhenPostEpicsIsCorrectCreate() throws IOException, InterruptedException {
        //Создаем задачи
        Epic epic = new Epic("Полететь на марс", "Прилететь на марс и организовать там колонию.");
        epic.setIdTask(1);


        //Формируем Json с параметрами эпика.
        String taskJson = "{"
                + "\"subtasks\": [],"
                + "\"endTime\": \"2024-07-21T16:19:25\","
                + "\"startTime\": \"2024-07-21T16:19:25\","
                + "\"duration\": \"PT0S\","
                + "\"title\": \"Полететь на марс\","
                + "\"description\": \"Прилететь на марс и организовать там колонию.\""
                + "}";

        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем код на корректность.
        Assertions.assertEquals(response.statusCode(), 201, "Код ответа должен быть равен 201.");

        //Проверяем корректность созданного эпика.
        Assertions.assertEquals(taskManager.getEpic(1), epic, "Эпики должны быть равны");
    }

    @Test
    public void shouldReturnPositiveWhenPostEpicsIsCorrectUpdate() throws IOException, InterruptedException {
        //Создаем задачи
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        taskManager.createEpic(epic);


        //Формируем Json с обновленными параметрами эпика.
        String taskJson = "{"
                + "\"subtasks\": [],"
                + "\"endTime\": \"2024-07-21T16:19:25\","
                + "\"startTime\": \"2024-07-21T16:19:25\","
                + "\"duration\": \"PT0S\","
                + "\"title\": \"Полететь на луну\","
                + "\"description\": \"Прилететь на луну и организовать там колонию.\""
                + "}";

        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/epics/1");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем код на корректность.
        Assertions.assertEquals(response.statusCode(), 201, "Код ответа должен быть равен 201.");

        //Воссоздаем корректно обновленный таск.
        Epic epicUpdate = new Epic("Полететь на луну", "Прилететь на луну и организовать там колонию.");
        epicUpdate.setIdTask(1);

        //Сравниваем с обновлением таска на сервере.
        Assertions.assertEquals(taskManager.getEpic(1), epicUpdate, "Эпики должны быть равны.");
    }

    @Test
    public void shouldReturnPositiveWhenDeleteEpicsIsCorrect() throws IOException, InterruptedException {
        //Создаем задачи
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        taskManager.createEpic(epic);

        //Проверяем количество задач до удаления.
        Assertions.assertEquals(taskManager.getAllEpic().size(), 1, "Должна быть одно подзадача.");

        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/epics/1");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //Проверяем код на корректность.
        Assertions.assertEquals(response.statusCode(), 200, "Код ответа должен быть равен 200.");

        //Проверяем удалился ли эпик
        Assertions.assertEquals(taskManager.getAllEpic().size(), 0, "Не должно быть эпиков");

        //Проверяем коды ошибок.
        checkGetErrorCodes("epics", 2);
    }

    @Test
    public void shouldReturnPositiveWhenDeleteAllEpicsIsCorrect() throws IOException, InterruptedException {
        //Создаем задачи
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        taskManager.createEpic(epic);

        //Подзадачи
        Subtask subtask1 = new Subtask("Убрать войны", "Убрать все оружие в мире",
                1, LocalDateTime.of(2025, 12, 1, 1, 1, 1), Duration.ofDays(2));
        Subtask subtask2 = new Subtask("Дать всем еды", "Накормить всех", 1,
                LocalDateTime.of(2021, 12, 1, 1, 1, 1), Duration.ofDays(2));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        //Проверяем количество задач до удаления.
        Assertions.assertEquals(taskManager.getAllEpic().size(), 1, "Должен быть один эпик.");
        Assertions.assertEquals(taskManager.getAllSubtask().size(), 2, "Должно быть 2 подзадачи.");

        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //Проверяем код на корректность.
        Assertions.assertEquals(response.statusCode(), 200, "Код ответа должен быть равен 200.");

        //Проверяем удалился ли эпик
        Assertions.assertEquals(taskManager.getAllEpic().size(), 0, "Не должно быть эпиков");
        Assertions.assertEquals(taskManager.getAllSubtask().size(), 0, "Должно быть подзадач");

        //Проверяем коды ошибок.
        checkGetErrorCodes("epics", 2);
    }

    @Test
    public void shouldReturnPositiveWhenGetEpicsSubtasksIsCorrect() throws IOException, InterruptedException {

        //Создаем эпики
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        taskManager.createEpic(epic);

        //Подзадачи
        Subtask subtask1 = new Subtask("Убрать войны", "Убрать все оружие в мире",
                1, LocalDateTime.of(2025, 12, 1, 1, 1, 1), Duration.ofDays(2));
        Subtask subtask2 = new Subtask("Дать всем еды", "Накормить всех", 1,
                LocalDateTime.of(2021, 12, 1, 1, 1, 1), Duration.ofDays(2));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);


        //Отправляем запрос.
        URI uri = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем код ответа.
        Assertions.assertEquals(response.statusCode(), 200, "Код ответа должен быть равен 200.");

        //Проверям корректность ответа.
        Type subtasksListType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();

        ArrayList<Subtask> subtasks = gson.fromJson(response.body(), subtasksListType);
        Assertions.assertEquals(subtasks.getFirst(), taskManager.getSubtask(2)
                , "Подзадачи должны быть равны");
        Assertions.assertEquals(subtasks.getLast(), taskManager.getSubtask(3)
                , "Подзадачи должны быть равны");

        //Отправляем запрос.
        uri = URI.create("http://localhost:8080/epics/1/subtasksErorror");
        httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        httpClient = HttpClient.newHttpClient();
        handler = HttpResponse.BodyHandlers.ofString();

        response = httpClient.send(httpRequest, handler);

        //Проверяем код ответа.
        Assertions.assertEquals(response.statusCode(), 400, "Код ответа должен быть равен 400.");
    }

    //Тесты для history
    @Test
    public void shouldReturnPositiveWhenGetHistoryIsCorrect() throws IOException, InterruptedException {

        //Создаем задачи.
        Task task1 = new Task("Найти работу", "Найти работу с зарплатой 1000к",
                LocalDateTime.of(2024, 12, 1, 1, 1, 1), Duration.ofDays(2));
        Task task2 = new Task("Сходить в магазин", "Купить еду в магазине",
                LocalDateTime.of(2023, 12, 1, 1, 1, 1), Duration.ofDays(2));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTask(1);
        taskManager.getTask(2);


        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/history");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //Проверяем код на корректность.
        Assertions.assertEquals(response.statusCode(), 200, "Код ответа должен быть равен 200.");

        //Проверяем историю на корректность.
        Type taskListType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        ArrayList<Task> tasks = gson.fromJson(response.body(), taskListType);

        Assertions.assertEquals(tasks.getFirst(), taskManager.getHistory().getFirst()
                , "Первые задачи должны быть равны");
        Assertions.assertEquals(tasks.getLast(), taskManager.getHistory().getLast()
                , "Последние задачи должны быть равны");
    }

    //Тесты для prioritized
    @Test
    public void shouldReturnPositiveWhenGetPrioritizedIsCorrect() throws IOException, InterruptedException {

        //Создаем задачи.
        Task task1 = new Task("Найти работу", "Найти работу с зарплатой 1000к",
                LocalDateTime.of(2024, 12, 1, 1, 1, 1), Duration.ofDays(2));
        Task task2 = new Task("Сходить в магазин", "Купить еду в магазине",
                LocalDateTime.of(2023, 12, 1, 1, 1, 1), Duration.ofDays(2));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        //Отправляем запрос и получаем ответ.
        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //Проверяем код на корректность.
        Assertions.assertEquals(response.statusCode(), 200, "Код ответа должен быть равен 200.");

        //Проверяем историю на корректность.
        Type taskListType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        ArrayList<Task> tasks = gson.fromJson(response.body(), taskListType);

        Assertions.assertEquals(tasks.getFirst(), taskManager.getPrioritizedTasks().getFirst()
                , "Первые задачи должны быть равны");
        Assertions.assertEquals(tasks.getLast(), taskManager.getPrioritizedTasks().getLast()
                , "Последние задачи должны быть равны");
    }

    public void checkGetErrorCodes(String way, int emptyTaskId) throws IOException, InterruptedException {
        //Запрос на несуществующий таск.
        URI uri = URI.create("http://localhost:8080/" + way + "/" + emptyTaskId);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = httpClient.send(httpRequest, handler);

        //Проверяем ответ на корректность.
        Assertions.assertEquals(response.statusCode(), 404, "Код ответа должен быть равен 200.");

        //Некорректный запрос
        uri = URI.create("http://localhost:8080/" + way + "/sadasda");
        httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        httpClient = HttpClient.newHttpClient();
        handler = HttpResponse.BodyHandlers.ofString();
        response = httpClient.send(httpRequest, handler);

        //Проверяем ответ на корректность.
        Assertions.assertEquals(response.statusCode(), 400, "Код ответа должен быть равен 200.");
    }
}
