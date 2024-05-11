import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

class InMemoryHistoryManagerTest {
    private static InMemoryHistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Найти работу", "Найти работу с зарплатой 1000к");
        Task task2 = new Task("Сходить в магазин", "Купить еду в магазине");
        Task task3 = new Task("Выспаться", "Купить еду в магазине");
        task1.setIdTask(1);
        task2.setIdTask(2);
        task3.setIdTask(3);

        Epic epic1 = new Epic("Построить мир", "Организовать мир во всем мире.");
        Epic epic2 = new Epic("Полететь на марс", "Прилететь на марс и организовать там колонию.");
        Epic epic3 = new Epic("Посадить дерево", "Найти саженец и воткнуть в землю.");
        epic1.setIdTask(4);
        epic2.setIdTask(5);
        epic3.setIdTask(6);

        Subtask subtask1 = new Subtask("Убрать войны", "Убрать все оружие в мире", 4);
        Subtask subtask2 = new Subtask("Дать всем еды", "Накормить всех", 4);
        Subtask subtask3 = new Subtask("Попить чай", "Налить воды в чайник", 4);
        subtask1.setIdTask(7);
        subtask2.setIdTask(8);
        subtask3.setIdTask(9);

        epic1.setSubtasks(7);
        epic1.setSubtasks(8);
        epic1.setSubtasks(9);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(epic1);
        historyManager.add(epic2);
        historyManager.add(epic3);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        historyManager.add(subtask3);
    }

    @Test
    public void shouldReturnPositiveWhenStorySequenceIsCorrect() {
        ArrayList<Task> history = (ArrayList<Task>) historyManager.getHistory();
        Assertions.assertNotNull(history, "История не должна быть пустой.");
        Assertions.assertEquals(history.get(0).getIdTask(), 9, "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(1).getIdTask(), 8, "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(2).getIdTask(), 7, "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(3).getIdTask(), 6, "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(4).getIdTask(), 5, "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(5).getIdTask(), 4, "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(6).getIdTask(), 3, "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(7).getIdTask(), 2, "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(8).getIdTask(), 1, "Неправильный порядок в истории");
    }


    @Test
    public void shouldReturnPositiveWhenStoryRemoveIsCorrect() {
        ArrayList<Task> history = (ArrayList<Task>) historyManager.getHistory();
        Assertions.assertEquals(history.size(), 9, "Должно быть 9 задач в истории.");

        //Удаляем первый таск в истории.
        historyManager.remove(9);
        history = (ArrayList<Task>) historyManager.getHistory();
        Assertions.assertEquals(history.size(), 8, "Должно быть 8 задач в истории.");
        Assertions.assertEquals(history.get(0).getIdTask(), 8
                , "Первый таск удален некорректно.");

//        //Удаляем последний таск в истории.
        historyManager.remove(1);
        history = (ArrayList<Task>) historyManager.getHistory();
        Assertions.assertEquals(history.size(), 7, "Должно быть 7 задач в истории.");
        Assertions.assertEquals(history.get(6).getIdTask(), 2
                , "Последний таск удален некорректно.");

//        //Удаляем средний таск в истории.
        Assertions.assertEquals(history.get(2).getIdTask(), 6, "Удаляемый таск не находится не там");
        historyManager.remove(6);
        history = (ArrayList<Task>) historyManager.getHistory();

        Assertions.assertEquals(history.size(), 6, "Должно быть 6 задач в истории.");
        Assertions.assertNotEquals(history.get(2).getIdTask(), 6, "Средний таск удален некорректно.");
    }

    @Test
    public void shouldReturnPositiveWhenStoryEpicRemoveIsCorrect() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        Subtask subtask1 = new Subtask("Убрать войны", "Убрать все оружие в мире", 1);
        Subtask subtask2 = new Subtask("Дать всем еды", "Накормить всех", 1);
        Task task = new Task("Найти работу", "Найти работу с зарплатой 1000к");

        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createSubtask(subtask1);
        inMemoryTaskManager.createSubtask(subtask2);
        inMemoryTaskManager.createTask(task);

        inMemoryTaskManager.getEpic(1);
        inMemoryTaskManager.getSubtask(2);
        inMemoryTaskManager.getSubtask(3);
        inMemoryTaskManager.getTask(4);
        Assertions.assertEquals(inMemoryTaskManager.getHistory().size(), 4, "Должно быть 3" +
                " задачи в истории.");

        inMemoryTaskManager.removalEpic(1);
        Assertions.assertEquals(inMemoryTaskManager.getHistory().size(), 1, "В истории должна остаться" +
                " только одна задача");
    }
}