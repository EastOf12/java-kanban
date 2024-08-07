import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        //Создаем объект менеджера.
        TaskManager taskManager = Managers.getDefault();

        //Задачи.
        Task task1 = new Task("Найти работу", "Найти работу с зарплатой 1000к",
                LocalDateTime.of(2024, 12, 1, 1, 1, 1), Duration.ofDays(2));
        Task task2 = new Task("Сходить в магазин", "Купить еду в магазине",
                LocalDateTime.of(2023, 12, 1, 1, 1, 1), Duration.ofDays(2));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        //Эпики
        Epic epic1 = new Epic("Построить мир", "Организовать мир во всем мире.");
        Epic epic2 = new Epic("Полететь на марс", "Прилететь на марс и организовать там колонию.");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        //Подзадачи
        Subtask subtask1 = new Subtask("Убрать войны", "Убрать все оружие в мире",
                3, LocalDateTime.of(2025, 12, 1, 1, 1, 1), Duration.ofDays(2));
        Subtask subtask2 = new Subtask("Дать всем еды", "Накормить всех", 3,
                LocalDateTime.of(2021, 12, 1, 1, 1, 1), Duration.ofDays(2));
        Subtask subtask3 = new Subtask("Раздать конфеты", "Сделать мир слаще", 3,
                LocalDateTime.of(2020, 12, 1, 1, 1, 1), Duration.ofDays(2));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);


        taskManager.getEpic(4);
        taskManager.getSubtask(5);
        taskManager.getSubtask(6);
        taskManager.getSubtask(7);
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getEpic(3);

        System.out.println(taskManager.getAllEpic());

    }
}