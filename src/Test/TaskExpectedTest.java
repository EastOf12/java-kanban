package Test;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class TaskExpectedTest {
    private static final TaskManager taskManager = Managers.getDefault();

    @BeforeEach
    public void BeforeEach() {
        Task taskExpected = new Task("Найти работу", "Найти работу с зарплатой 1000к");
        Epic epicExpected = new Epic("Построить мир", "Организовать мир во всем мире.");
        Subtask subtaskExpected = new Subtask("Убрать войны", "Убрать все оружие в мире", 2);

        taskManager.createTask(taskExpected);
        taskManager.createEpic(epicExpected);
        taskManager.createSubtask(subtaskExpected);
    }

    @Test
    public void shouldReturnPositiveWhenSimilarIdSubtaskNotEqualEpic() {
        //Получаем объекты Epic, Subtask
        Epic taskExpected = taskManager.getEpic(2);
        Subtask taskActual = taskManager.getSubtask(3);

        //Делаем их ID одинаковыми.
        taskExpected.setIdTask(3);

        //Сраниваем таск и эпик с одинаковым ID.
        Assertions.assertNotEquals(taskExpected, taskActual, "Таск равен эпику с одинаковым id.");
    }
}
