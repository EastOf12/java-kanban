import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        //Создаем объект менеджера.
        TaskManager taskManager = Managers.getDefault();

        //Задачи.
        Task task1 = new Task("Найти работу", "Найти работу с зарплатой 1000к");
        Task task2 = new Task("Сходить в магазин", "Купить еду в магазине");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        //Эпики
        Epic epic1 = new Epic("Построить мир", "Организовать мир во всем мире.");
        Epic epic2 = new Epic("Полететь на марс", "Прилететь на марс и организовать там колонию.");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        //Подзадачи
        Subtask subtask1 = new Subtask("Убрать войны", "Убрать все оружие в мире", 3);
        Subtask subtask2 = new Subtask("Дать всем еды", "Накормить всех", 3);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        System.out.println("\nВсе таски " + taskManager.getAllTask());
        System.out.println("Все эпики " + taskManager.getAllEpic());
        System.out.println("Все подзадачи " + taskManager.getAllSubtask());

        System.out.println("Отображаем историю просмотра тасков.");
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getEpic(4);
        taskManager.getSubtask(6);
        System.out.println(taskManager.getHistory());

    }
}
