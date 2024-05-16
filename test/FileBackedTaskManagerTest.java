import manager.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    private static FileBackedTaskManager fileBackedTaskManager;
    File tempFile;

    @BeforeEach
    public void beforeEach() throws IOException {
        fileBackedTaskManager = new FileBackedTaskManager();
        tempFile = File.createTempFile("myTempFile", ".txt");
        fileBackedTaskManager.setFile(tempFile);
    }


    @Test
    public void shouldReturnPositiveWhenLoadEmptyFile() throws IOException {
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
    public void shouldReturnPositiveWhenLoadNotEmptyFile() throws IOException {
        Task task = new Task("Найти работу", "Найти работу с зарплатой 1000к");
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        Subtask subtask = new Subtask("Убрать войны", "Убрать все оружие в мире", 2);
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createSubtask(subtask);

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        fileBackedTaskManager.setFile(tempFile);

        assertEquals(fileBackedTaskManager.getAllTask().getFirst(), task, "Таски должны быть одинаковы.");
        assertEquals(fileBackedTaskManager.getAllEpic().getFirst(), epic, "Эпики должны быть одинаковы.");
        assertEquals(fileBackedTaskManager.getAllSubtask().getFirst(), subtask, "Подзадачи должны быть одинаковы.");
    }


    @Test
    public void shouldReturnPositiveWhenSaveNotEmptyFile() {
        Task task = new Task("Найти работу", "Найти работу с зарплатой 1000к");
        Epic epic = new Epic("Построить мир", "Организовать мир во всем мире.");
        Subtask subtask = new Subtask("Убрать войны", "Убрать все оружие в мире", 2);

        try {
            assertEquals(0, tempFile.length(), "Размер файла больше 0");

            fileBackedTaskManager.createTask(task);
            fileBackedTaskManager.createEpic(epic);
            fileBackedTaskManager.createSubtask(subtask);

            List<String> lines = Files.readAllLines(tempFile.toPath());
            assertEquals("id,type,name,status,description,epic", lines.get(0), "Первая " +
                    "строка некорректна");
            assertEquals("1,TASK,Найти работу,NEW,Найти работу с зарплатой 1000к", lines.get(1),
                    "Вторая строка некорректна");
            assertEquals("2,EPIC,Построить мир,NEW,Организовать мир во всем мире.", lines.get(2),
                    "Третья строка некорректна");
            assertEquals("3,SUBTASK,Убрать войны,NEW,Убрать все оружие в мире,2", lines.get(3),
                    "Четвертая строка некорректна");

        } catch (IOException e) {
            System.out.println("Ошибка при создании временного файла: " + e.getMessage());
        }
    }
}
