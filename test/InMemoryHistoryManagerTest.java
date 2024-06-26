import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

class InMemoryHistoryManagerTest {
    private static InMemoryHistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        Task task = new Task("Найти работу", "Найти работу с зарплатой 1000к"
                , LocalDateTime.of(2024, 12, 31, 23, 59)
                , Duration.ofDays(2));
        task.setIdTask(1);

        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        epic.setIdTask(2);


        Subtask subtask = new Subtask("Убрать войны", "Убрать все оружие в мире", 2
                , LocalDateTime.of(2012, 12, 31, 23, 59)
                , Duration.ofDays(2));
        subtask.setIdTask(3);


        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
    }

    @Test
    public void shouldReturnPositiveWhenStoryCantHasDuplicate() {

        Assertions.assertEquals(historyManager.getHistory().size(), 3, "В истории должно быть 3 задачи.");

        Task taskNew = new Task("Найти работу", "Найти работу с зарплатой 1000к"
                , LocalDateTime.of(2024, 12, 31, 23, 59)
                , Duration.ofDays(2));
        taskNew.setIdTask(1);

        historyManager.add(taskNew);
        Assertions.assertEquals(historyManager.getHistory().size(), 3, "Таск не должен быть добавлен" +
                ", тк он уже есть в истории");
    }

    @Test
    public void shouldReturnPositiveWhenStorySequenceIsCorrect() {
        ArrayList<Task> history = (ArrayList<Task>) historyManager.getHistory();
        Assertions.assertNotNull(history, "История не должна быть пустой.");
        Assertions.assertEquals(history.get(0).getIdTask(), 3, "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(1).getIdTask(), 2, "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(2).getIdTask(), 1, "Неправильный порядок в истории");
    }

    @Test
    public void shouldReturnPositiveWhenStoryRemoveBeginIsCorrect() {
        ArrayList<Task> history = (ArrayList<Task>) historyManager.getHistory();
        Assertions.assertEquals(history.size(), 3, "Должно быть 3 задач в истории.");

        historyManager.remove(1);

        history = (ArrayList<Task>) historyManager.getHistory();
        Assertions.assertEquals(history.size(), 2, "Должно быть 2 задачи в истории.");
        Assertions.assertEquals(history.get(0).getIdTask(), 3, "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(1).getIdTask(), 2, "Неправильный порядок в истории");
    }

    @Test
    public void shouldReturnPositiveWhenStoryRemoveEndIsCorrect() {
        ArrayList<Task> history = (ArrayList<Task>) historyManager.getHistory();
        Assertions.assertEquals(history.size(), 3, "Должно быть 3 задач в истории.");

        historyManager.remove(3);

        history = (ArrayList<Task>) historyManager.getHistory();
        Assertions.assertEquals(history.get(0).getIdTask(), 2, "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(1).getIdTask(), 1, "Неправильный порядок в истории");
    }

    @Test
    public void shouldReturnPositiveWhenStoryRemoveMiddleIsCorrect() {
        ArrayList<Task> history = (ArrayList<Task>) historyManager.getHistory();
        Assertions.assertEquals(history.size(), 3, "Должно быть 3 задач в истории.");

        historyManager.remove(2);
        history = (ArrayList<Task>) historyManager.getHistory();

        Assertions.assertEquals(history.get(0).getIdTask(), 3, "Неправильный порядок в истории");
        Assertions.assertEquals(history.get(1).getIdTask(), 1, "Неправильный порядок в истории");
    }

    @Test
    public void shouldReturnPositiveWhenStoryEpicRemoveIsCorrect() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        Subtask subtask1 = new Subtask("Убрать войны", "Убрать все оружие в мире", 1
                , LocalDateTime.of(2012, 12, 31, 23, 59)
                , Duration.ofDays(2));
        Subtask subtask2 = new Subtask("Дать всем еды", "Накормить всех", 1
                , LocalDateTime.of(2013, 12, 31, 23, 59)
                , Duration.ofDays(2));
        Task task = new Task("Найти работу", "Найти работу с зарплатой 1000к"
                , LocalDateTime.of(2024, 12, 31, 23, 59)
                , Duration.ofDays(2));

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