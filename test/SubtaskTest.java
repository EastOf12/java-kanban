import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class SubtaskTest {
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
    public void shouldReturnPositiveWhenEqualSubtask() {

        //Получаем объекты типа Task
        Subtask subtaskExpected = new Subtask("Убрать войны", "Убрать все оружие в мире", 2);
        subtaskExpected.setIdTask(3);
        Subtask subtaskActual = taskManager.getSubtask(3);

        //Сраниваем таски с одинаковым ID.
        Assertions.assertEquals(subtaskExpected, subtaskActual, "Таски с одинаковыми id не равны");
    }

}
