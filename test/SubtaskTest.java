import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;


public class SubtaskTest {
    @Test
    public void shouldReturnPositiveWhenEqualSubtask() {
        Subtask subtaskActual = new Subtask("Найти работу", "Найти работу с зарплатой 1000к", 1
                , LocalDateTime.of(2023, 12, 31, 23, 59)
                , Duration.ofDays(2));
        subtaskActual.setIdTask(1);
        Subtask subtaskExpected = new Subtask("Найти работу", "Найти работу с зарплатой 1000к", 1
                , LocalDateTime.of(2023, 12, 31, 23, 59)
                , Duration.ofDays(2));
        subtaskExpected.setIdTask(1);
        Assertions.assertEquals(subtaskExpected, subtaskActual, "Таски с одинаковыми id не равны");
    }

}