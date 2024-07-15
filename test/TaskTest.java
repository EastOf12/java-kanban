import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskTest {
    @Test
    public void shouldReturnPositiveWhenEqualTask() {
        Task taskActual = new Task("Найти работу", "Найти работу с зарплатой 1000к"
                , LocalDateTime.of(2024, 12, 31, 23, 59)
                , Duration.ofDays(2));
        taskActual.setIdTask(1);

        Task taskExpected = new Task("Найти работу", "Найти работу с зарплатой 1000к"
                , LocalDateTime.of(2024, 12, 31, 23, 59)
                , Duration.ofDays(2));
        taskExpected.setIdTask(1);

        Assertions.assertEquals(taskExpected, taskActual, "Таски с одинаковыми id не равны");
    }

}
