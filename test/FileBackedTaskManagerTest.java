import manager.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    private static FileBackedTaskManager fileBackedTaskManager;
    File tempFile;

    @BeforeEach
    public void beforeEach() throws IOException {
        tempFile = File.createTempFile("myTempFile", ".txt");
        fileBackedTaskManager = new FileBackedTaskManager(tempFile);

    }


    @Test
    public void shouldReturnPositiveWhenLoadEmptyFile() throws IOException {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile();
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
        Task task = new Task("Найти работу", "Найти работу с зарплатой 1000к");
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        Subtask subtask = new Subtask("Убрать войны", "Убрать все оружие в мире", 2);
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createSubtask(subtask);

        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile();

        assertEquals(fileBackedTaskManager.getAllTask(), newFileBackedTaskManager.getAllTask(), "Таски " +
                "должны быть одинаковыми.");
        assertEquals(fileBackedTaskManager.getAllEpic(), newFileBackedTaskManager.getAllEpic(), "Эпики " +
                "должны быть одинаковыми.");
        assertEquals(fileBackedTaskManager.getAllSubtask(), newFileBackedTaskManager.getAllSubtask(),
                "Подзадачи должны быть одинаковыми.");
    }
}
