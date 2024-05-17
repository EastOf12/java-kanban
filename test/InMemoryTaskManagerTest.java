import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        Task task1 = new Task("Найти работу", "Найти работу с зарплатой 1000к");
        Task task2 = new Task("Сходить в магазин", "Купить еду в магазине");
        Epic epic1 = new Epic("Построить мир", "Организовать мир во всем мире.");
        Epic epic2 = new Epic("Полететь на марс", "Прилететь на марс и организовать там колонию.");
        Subtask subtask1 = new Subtask("Убрать войны", "Убрать все оружие в мире", 3);
        Subtask subtask2 = new Subtask("Дать всем еды", "Накормить всех", 3);


        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
    }

    @Test
    public void shouldReturnPositiveWhenSubtaskDontAddSubtask() {
        Subtask subtask3 = new Subtask("Дать всем еды", "Накормить всех", 5);
        Assertions.assertFalse(taskManager.createSubtask(subtask3));
    }

    @Test
    public void shouldReturnPositiveWhenFindTask() {
        Task task = taskManager.getTask(1);
        Assertions.assertNotNull(task);
    }

    @Test
    public void shouldReturnPositiveWhenFindEpic() {
        Epic epic = taskManager.getEpic(3);
        Assertions.assertNotNull(epic);
    }

    @Test
    public void shouldReturnPositiveWhenFindSubtask() {
        Subtask subtask = taskManager.getSubtask(5);
        Assertions.assertNotNull(subtask);
    }

    @Test
    public void shouldReturnPositiveWhenSaveHistory() {
        Assertions.assertTrue(taskManager.getHistory().isEmpty(), "Список не пуст.");

        taskManager.getTask(1);
        Epic epic = taskManager.getEpic(3);
        taskManager.getSubtask(5);

        Assertions.assertEquals(taskManager.getHistory().size(), 3
                , "Количество просмотренных задач не соотвествует ожидаемому результату.");

        Assertions.assertEquals(taskManager.getHistory().get(1), epic
                , "После добавления новой задачи, данные в предыдущей изменились.");
    }

    @Test
    public void shouldReturnPositiveWhenTaskImmeasurable() {
        String title = "Найти работу";
        String description = "Найти работу с зарплатой 1000к";

        Task task = taskManager.getTask(1);

        Assertions.assertEquals(task.getTitle(), title, "Название при создании отличаются");
        Assertions.assertEquals(task.getDescription(), description, "Описание при создании отличаются");
    }

    @Test
    public void shouldReturnPositiveWhenEpicImmeasurable() {
        String title = "Построить мир";
        String description = "Организовать мир во всем мире.";

        Epic epic = taskManager.getEpic(3);

        Assertions.assertEquals(epic.getTitle(), title, "Название при создании отличаются");
        Assertions.assertEquals(epic.getDescription(), description, "Описание при создании отличаются");
    }

    @Test
    public void shouldReturnPositiveWhenSubtaskImmeasurable() {
        String title = "Убрать войны";
        String description = "Убрать все оружие в мире";

        Subtask subtask = taskManager.getSubtask(5);

        Assertions.assertEquals(subtask.getTitle(), title, "Название при создании отличаются");
        Assertions.assertEquals(subtask.getDescription(), description, "Описание при создании отличаются");
    }

    @Test
    public void shouldReturnPositiveWhenTaskUpdated() {

        Task expectedTask = new Task("Открыть бизнес", "Открыть бизнес с доходом в 100500к");
        expectedTask.setIdTask(1);

        Task actualTask = taskManager.getTask(1);

        taskManager.updateTask(expectedTask);

        Assertions.assertNotEquals(expectedTask, actualTask, "Не обновили таск");

    }

    @Test
    public void shouldReturnPositiveWhenEpicUpdated() {

        Epic expectedEpic = new Epic("Захватить мир", "Захватить мир игрушек");
        expectedEpic.setIdTask(3);

        Epic actualTask = taskManager.getEpic(3);

        taskManager.updateTask(expectedEpic);

        Assertions.assertNotEquals(expectedEpic, actualTask, "Не обновили таск");
    }

    @Test
    public void shouldReturnPositiveWhenSubtaskUpdated() {

        Subtask expectedSubtask = new Subtask("Научиться плавать", "Сходить в бассейн.", 3);
        expectedSubtask.setIdTask(5);

        Subtask actualSubtask = taskManager.getSubtask(5);

        taskManager.updateTask(expectedSubtask);

        Assertions.assertNotEquals(expectedSubtask, actualSubtask, "Не обновили таск");

    }

    @Test
    public void shouldReturnPositiveWhenTaskRemoved() {
        Assertions.assertNotNull(taskManager.getTask(1));

        taskManager.removalTask(1);

        assertNull(taskManager.getTask(1), "Таск не удалили");
    }

    @Test
    public void shouldReturnPositiveWhenEpicRemoved() {
        Assertions.assertNotNull(taskManager.getEpic(3));

        taskManager.removalEpic(3);

        assertNull(taskManager.getEpic(3), "Эпик не удалили");
    }

    @Test
    public void shouldReturnPositiveWhenSubtaskRemoved() {
        Assertions.assertNotNull(taskManager.getSubtask(5));

        taskManager.removalSubtask(5);

        assertNull(taskManager.getSubtask(5), "Подзадачу не удалили");
    }
}