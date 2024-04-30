package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    //Методы tasks.Task
    ArrayList<Task> getAllTask(); // Получить все таски.


    void clearAllTask(); // Очистить все таски.


    void createTask(Task task); // Создать новый таск.


    Task getTask(int idTask); // Получить таск по id.


    boolean removalTask(int idTask); // Удаляем таск по id.


    boolean updateTask(Task task); // Обновляем таск.


    //Методы tasks.Epic
    ArrayList<Epic> getAllEpic(); // Получить все эпики.


    void clearAllEpic(); // Очистить все эпики.


    void createEpic(Epic epic); // Создать новый эпик.


    Epic getEpic(int idTask); // Получить эпик по id


    boolean removalEpic(int idTask);// Удалить эпик по id.


    boolean updateEpic(Epic epic); // Обновляем эпик.


    ArrayList<Subtask> getAllSubtask(); // Получить все подзадачи.


    void clearAllSubtask(); // Очистить все подзадачи.


    boolean createSubtask(Subtask subtask); // Создать новую подзадачу.


    Subtask getSubtask(int idTask); // Получить подзадачу по id.


    boolean removalSubtask(int idTask); //Удалить подзадачу по id.


    boolean updateSubtask(Subtask subtask); // Обновляем подзадачу.

    ArrayList<Subtask> getSubtaskEpic(int idEpic); //Получаем все задачи в эпике.

    public ArrayList<Task> getHistory(); //Возвращает исторю задач в виде списка
}
