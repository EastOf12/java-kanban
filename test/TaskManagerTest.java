import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNull;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager() throws IOException;

    @BeforeEach
    public void beforeEach() throws IOException {
        taskManager = createTaskManager();
        Task task1 = new Task("Найти работу", "Найти работу с зарплатой 1000к"
                , LocalDateTime.of(2024, 12, 31, 23, 59)
                , Duration.ofDays(2));
        Task task2 = new Task("Сходить в магазин", "Купить еду в магазине"
                , LocalDateTime.of(2015, 12, 31, 23, 59)
                , Duration.ofDays(2));
        Epic epic1 = new Epic("Построить мир", "Организовать мир во всем мире.");
        Epic epic2 = new Epic("Полететь на марс", "Прилететь на марс и организовать там колонию.");
        Subtask subtask1 = new Subtask("Убрать войны", "Убрать все оружие в мире", 3
                , LocalDateTime.of(2024, 5, 15, 23, 59)
                , Duration.ofDays(2));
        Subtask subtask2 = new Subtask("Дать всем еды", "Накормить всех", 3
                , LocalDateTime.of(2023, 5, 15, 23, 59)
                , Duration.ofDays(2));


        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
    }

    @Test
    public void shouldReturnPositiveWhenCheckTaskIntersectionsIsCorrect() throws IOException {
        //Тут нужно будет проверять, пытаясь создать задачи. Если дата корректная, то создалось нормально и тд.
        T taskManager = createTaskManager();
        Task task = new Task("Найти работу", "Найти работу с зарплатой 1000к"
                , LocalDateTime.of(2025, 2, 5, 0, 0)
                , Duration.ofDays(2));
        taskManager.createTask(task);

        //Проверяем что есть один созданный таск в списке приоритетов.
        Assertions.assertEquals(taskManager.getPrioritizedTasks().size(), 1, "Должен быть 1 таск.");

        //Пытаемся создать таск в начале.
        taskManager.createTask(new Task("Построить дом", "Построить дом из мрамора"
                , LocalDateTime.of(2025, 2, 5, 0, 0)
                , Duration.ofDays(2)));

        //Пытаемся создать таск в середине.
        taskManager.createTask(new Task("Построить дом", "Построить дом из мрамора"
                , LocalDateTime.of(2025, 2, 6, 0, 1)
                , Duration.ofDays(2)));

        //Пытаемся создать таск в конце.
        taskManager.createTask(new Task("Построить дом", "Построить дом из мрамора"
                , LocalDateTime.of(2025, 2, 7, 0, 0)
                , Duration.ofDays(2)));

        //Проверяем что нет созданных тасков
        Assertions.assertEquals(taskManager.getPrioritizedTasks().size(), 1, "Должен быть 1 таск.");

        //Создаем таск, который не пересекается по времени.
        taskManager.createTask(new Task("Построить дом", "Построить дом из мрамора"
                , LocalDateTime.of(2025, 2, 7, 0, 1)
                , Duration.ofDays(2)));

        //Проверяем что теперь 2 созданных таска в списке приоритетов.
        Assertions.assertEquals(taskManager.getPrioritizedTasks().size(), 2, "Должно быть 2 таска.");
    }

    @Test
    public void shouldReturnPositiveWhenEpicStatusNewCorrect() {
        Assertions.assertEquals(taskManager.getSubtask(5).getStatus(), TaskStatus.NEW
                , "Подзадача 1 должна быть в статусе NEW");
        Assertions.assertEquals(taskManager.getSubtask(6).getStatus(), TaskStatus.NEW
                , "Подзадача 2 должна быть в статусе NEW");
        Assertions.assertEquals(taskManager.getEpic(3).getStatus(), TaskStatus.NEW
                , "Эпик должен быть в статусе NEW");
    }

    @Test
    public void shouldReturnPositiveWhenEpicStatusDoneCorrect() {
        taskManager.getSubtask(5).setStatus(TaskStatus.DONE);
        taskManager.getSubtask(6).setStatus(TaskStatus.DONE);

        taskManager.updateSubtask(taskManager.getSubtask(5));
        taskManager.updateSubtask(taskManager.getSubtask(6));

        Assertions.assertEquals(taskManager.getSubtask(5).getStatus(), TaskStatus.DONE
                , "Подзадача 1 должна быть в статусе DONE");
        Assertions.assertEquals(taskManager.getSubtask(6).getStatus(), TaskStatus.DONE
                , "Подзадача 2 должна быть в статусе DONE");
        Assertions.assertEquals(taskManager.getEpic(3).getStatus(), TaskStatus.DONE
                , "Эпик должен быть в статусе DONE");
    }

    @Test
    public void shouldReturnPositiveWhenEpicStatusInProgressCorrect() {
        taskManager.getSubtask(5).setStatus(TaskStatus.DONE);

        taskManager.updateSubtask(taskManager.getSubtask(5));
        taskManager.updateSubtask(taskManager.getSubtask(6));

        Assertions.assertEquals(taskManager.getSubtask(5).getStatus(), TaskStatus.DONE
                , "Подзадача 1 должна быть в статусе DONE");
        Assertions.assertEquals(taskManager.getSubtask(6).getStatus(), TaskStatus.NEW
                , "Подзадача 2 должна быть в статусе NEW");
        Assertions.assertEquals(taskManager.getEpic(3).getStatus(), TaskStatus.IN_PROGRESS
                , "Эпик должен быть в статусе IN_PROGRESS");

        taskManager.getSubtask(5).setStatus(TaskStatus.IN_PROGRESS);
        taskManager.getSubtask(6).setStatus(TaskStatus.IN_PROGRESS);

        Assertions.assertEquals(taskManager.getSubtask(5).getStatus(), TaskStatus.IN_PROGRESS
                , "Подзадача 1 должна быть в статусе IN_PROGRESS");
        Assertions.assertEquals(taskManager.getSubtask(6).getStatus(), TaskStatus.IN_PROGRESS
                , "Подзадача 2 должна быть в статусе IN_PROGRESS");
        Assertions.assertEquals(taskManager.getEpic(3).getStatus(), TaskStatus.IN_PROGRESS
                , "Эпик должен быть в статусе IN_PROGRESS");
    }

    @Test
    public void shouldReturnPositiveWhenSubtaskHasEpic() {
        Assertions.assertEquals(taskManager.getSubtask(5).getIdEpic(), 3
                , "Subtask должен быть привязан к эпику 3");
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
        Task expectedTask = new Task("Открыть бизнес", "Открыть бизнес с доходом в 100500к"
                , LocalDateTime.of(2034, 5, 15, 23, 59)
                , Duration.ofDays(2));
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
        Subtask expectedSubtask = new Subtask("Научиться плавать", "Сходить в бассейн.", 3
                , LocalDateTime.of(2002, 5, 15, 23, 59)
                , Duration.ofDays(2));
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

    @Test
    public void shouldReturnPositiveWhenSubtaskDontAddSubtask() {
        Subtask subtask3 = new Subtask("Дать всем еды", "Накормить всех", 5
                , LocalDateTime.of(2004, 5, 15, 23, 59)
                , Duration.ofDays(2));
        Assertions.assertFalse(taskManager.createSubtask(subtask3));
    }
}
