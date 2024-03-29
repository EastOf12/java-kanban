import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Task;

public class TaskTest {
    @Test
    public void shouldReturnPositiveWhenEqualTask() {
        Task taskActual = new Task("Найти работу", "Найти работу с зарплатой 1000к");
        taskActual.setIdTask(1);
        Task taskExpected = new Task("Найти работу", "Найти работу с зарплатой 1000к");
        taskExpected.setIdTask(1);
        Assertions.assertEquals(taskExpected, taskActual, "Таски с одинаковыми id не равны");
    }

}
