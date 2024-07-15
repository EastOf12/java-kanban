import manager.FileBackedTaskManager;
import manager.ManagerSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static FileBackedTaskManager fileBackedTaskManager;
    File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() throws IOException {
        tempFile = File.createTempFile("myTempFile", ".txt");
        fileBackedTaskManager = new FileBackedTaskManager(tempFile);
        return fileBackedTaskManager;
    }

    @BeforeEach
    public void beforeEachFile() throws IOException {
        createTaskManager();
    }


    @Test
    public void shouldReturnPositiveWhenLoadEmptyFile() {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(0, fileBackedTaskManager.getAllTask().size(), "Не должно быть задач");
        assertEquals(0, fileBackedTaskManager.getAllEpic().size(), "Не должно быть задач");
        assertEquals(0, fileBackedTaskManager.getAllSubtask().size(), "Не должно быть задач");

    }

    @Test
    public void shouldReturnPositiveWhenSaveEmptyFile() {
        fileBackedTaskManager.save(tempFile);
        assertEquals(0, tempFile.length(), "Размер файла больше 0");
    }

    @Test
    public void shouldReturnPositiveWhenSaveLoadFileIsCorrect() {
        Task task = new Task("Найти работу", "Найти работу с зарплатой 1000к"
                , LocalDateTime.of(2024, 12, 31, 23, 59)
                , Duration.ofDays(2));
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        Subtask subtask = new Subtask("Убрать войны", "Убрать все оружие в мире", 2
                , LocalDateTime.of(2019, 12, 31, 23, 59)
                , Duration.ofDays(2));
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createSubtask(subtask);

        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(fileBackedTaskManager.getAllTask(), newFileBackedTaskManager.getAllTask(), "Таски " +
                "должны быть одинаковыми.");
        assertEquals(fileBackedTaskManager.getAllEpic(), newFileBackedTaskManager.getAllEpic(), "Эпики " +
                "должны быть одинаковыми.");
        assertEquals(fileBackedTaskManager.getAllSubtask(), newFileBackedTaskManager.getAllSubtask(),
                "Подзадачи должны быть одинаковыми.");
        assertEquals(fileBackedTaskManager.getPrioritizedTasks(), newFileBackedTaskManager.getPrioritizedTasks(),
                "Отсортированные по приоритету списки должны быть равны");
    }

    @Test
    public void testSaveMethod() throws IOException {
        File tempFile = new File("test");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);

        // Проверка для несуществующего пути
        assertThrows(ManagerSaveException.class, () -> {
            fileBackedTaskManager.save(tempFile);
        });
    }
}

