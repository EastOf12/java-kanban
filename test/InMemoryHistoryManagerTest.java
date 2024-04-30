import manager.InMemoryHistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

class InMemoryHistoryManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        Task task1 = new Task("Найти работу", "Найти работу с зарплатой 1000к");
        Task task2 = new Task("Сходить в магазин", "Купить еду в магазине");
        Task task3 = new Task("Выспаться", "Купить еду в магазине");
        Epic epic1 = new Epic("Построить мир", "Организовать мир во всем мире.");
        Epic epic2 = new Epic("Полететь на марс", "Прилететь на марс и организовать там колонию.");
        Epic epic3 = new Epic("Посадить дерево", "Найти саженец и воткнуть в землю.");
        Subtask subtask1 = new Subtask("Убрать войны", "Убрать все оружие в мире", 4);
        Subtask subtask2 = new Subtask("Дать всем еды", "Накормить всех", 4);
        Subtask subtask3 = new Subtask("Попить чай", "Налить воды в чайник", 4);


        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createEpic(epic3);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
    }


    @Test
    public void shouldReturnPositiveWhenStorySequenceIsCorrect() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        ArrayList<Task> history = (ArrayList<Task>) inMemoryHistoryManager.getHistory();
        Assertions.assertEquals(history, new ArrayList<>(), "История должна быть пустой.");

        inMemoryHistoryManager.add(taskManager.getTask(1));
        inMemoryHistoryManager.add(taskManager.getTask(2));
        inMemoryHistoryManager.add(taskManager.getTask(3));
        inMemoryHistoryManager.add(taskManager.getEpic(4));
        inMemoryHistoryManager.add(taskManager.getEpic(5));
        inMemoryHistoryManager.add(taskManager.getEpic(6));
        inMemoryHistoryManager.add(taskManager.getSubtask(7));
        inMemoryHistoryManager.add(taskManager.getSubtask(8));
        inMemoryHistoryManager.add(taskManager.getSubtask(9));

        history = (ArrayList<Task>) inMemoryHistoryManager.getHistory();
        Assertions.assertNotNull(history, "История не должна быть пустой.");

        Assertions.assertEquals(history.get(0), taskManager.getTask(1), "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(1), taskManager.getTask(2), "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(2), taskManager.getTask(3), "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(3), taskManager.getEpic(4), "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(4), taskManager.getEpic(5), "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(5), taskManager.getEpic(6), "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(6), taskManager.getSubtask(7), "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(7), taskManager.getSubtask(8), "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(8), taskManager.getSubtask(9), "Неправильный порядок в истории");
    }


    @Test
    public void shouldReturnPositiveWhenStoryRemoveIsCorrect() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        ArrayList<Task> history = (ArrayList<Task>) inMemoryHistoryManager.getHistory();
        Assertions.assertEquals(history, new ArrayList<>(), "История должна быть пустой.");

        inMemoryHistoryManager.add(taskManager.getTask(1));
        inMemoryHistoryManager.add(taskManager.getTask(2));
        inMemoryHistoryManager.add(taskManager.getTask(3));
        inMemoryHistoryManager.add(taskManager.getEpic(4));
        inMemoryHistoryManager.add(taskManager.getEpic(5));
        inMemoryHistoryManager.add(taskManager.getEpic(6));
        inMemoryHistoryManager.add(taskManager.getSubtask(7));
        inMemoryHistoryManager.add(taskManager.getSubtask(8));
        inMemoryHistoryManager.add(taskManager.getSubtask(9));

        history = (ArrayList<Task>) inMemoryHistoryManager.getHistory();
        Assertions.assertEquals(history.size(), 9, "Должно быть 9 задач в истории.");

        //Удаляем первый таск в истории.
        inMemoryHistoryManager.remove(1);
        history = (ArrayList<Task>) inMemoryHistoryManager.getHistory();
        Assertions.assertEquals(history.size(), 8, "Должно быть 8 задач в истории.");
        Assertions.assertEquals(history.get(0), taskManager.getTask(2), "Первый таск удален некорректно.");

        //Удаляем последний таск в истории.
        inMemoryHistoryManager.remove(9);
        history = (ArrayList<Task>) inMemoryHistoryManager.getHistory();
        Assertions.assertEquals(history.size(), 7, "Должно быть 7 задач в истории.");
        Assertions.assertEquals(history.get(6), taskManager.getSubtask(8), "Последний таск удален некорректно.");

        //Удаляем средний таск в истории.
        inMemoryHistoryManager.remove(6);
        history = (ArrayList<Task>) inMemoryHistoryManager.getHistory();
        Assertions.assertEquals(history.size(), 6, "Должно быть 6 задач в истории.");
        Assertions.assertEquals(history.get(4), taskManager.getSubtask(7), "Средний таск удален некорректно.");
    }

    @Test
    public void shouldReturnPositiveWhenStoryEpicRemoveIsCorrect() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        ArrayList<Task> history = (ArrayList<Task>) inMemoryHistoryManager.getHistory();
        Assertions.assertEquals(history, new ArrayList<>(), "История должна быть пустой.");

        inMemoryHistoryManager.add(taskManager.getEpic(4));
        inMemoryHistoryManager.add(taskManager.getSubtask(7));
        inMemoryHistoryManager.add(taskManager.getSubtask(8));
        inMemoryHistoryManager.add(taskManager.getSubtask(9));

        history = (ArrayList<Task>) inMemoryHistoryManager.getHistory();
        Assertions.assertEquals(history.size(), 4, "Должно быть 4 задачи в истории.");

        inMemoryHistoryManager.remove(4);

        history = (ArrayList<Task>) inMemoryHistoryManager.getHistory();
        Assertions.assertEquals(history.size(), 0, "Список должен быть пуст.");

    }
}