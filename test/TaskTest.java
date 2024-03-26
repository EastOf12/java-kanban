import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class TaskTest {
    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        Task taskActual = new Task("Найти работу", "Найти работу с зарплатой 1000к");
        Epic epicActual = new Epic("Построить мир", "Организовать мир во всем мире.");
        Subtask subtaskActual = new Subtask("Убрать войны", "Убрать все оружие в мире", 2);

        taskManager.createTask(taskActual);
        taskManager.createEpic(epicActual);
        taskManager.createSubtask(subtaskActual);
    }

    @Test
    public void shouldReturnPositiveWhenEqualTask() {

        //Получаем объекты типа Task
        Task taskExpected = new Task("Найти работу", "Найти работу с зарплатой 1000к");
        taskExpected.setIdTask(1);
        Task taskActual = taskManager.getTask(1);

        //Сраниваем таски с одинаковым ID.
        Assertions.assertEquals(taskExpected, taskActual, "Таски с одинаковыми id не равны");
    }

}
