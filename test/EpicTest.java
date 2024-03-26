import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class EpicTest {
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
    public void shouldReturnPositiveWhenEqualEpic() {

        //Получаем объекты типа Epic
        Epic epicExpected = new Epic("Построить мир", "Организовать мир во всем мире.");
        epicExpected.setIdTask(2);
        epicExpected.setSubtasks(3);
        Epic epicActual = taskManager.getEpic(2);

        //Сраниваем эпики с одинаковым ID.
        Assertions.assertEquals(epicExpected, epicActual, "Эпики с одинаковыми id не равны");
    }

}
