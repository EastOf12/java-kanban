import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Subtask;


public class SubtaskTest {
    @Test
    public void shouldReturnPositiveWhenEqualSubtask() {
        Subtask subtaskActual = new Subtask("Найти работу", "Найти работу с зарплатой 1000к", 1);
        subtaskActual.setIdTask(1);
        Subtask subtaskExpected = new Subtask("Найти работу", "Найти работу с зарплатой 1000к", 1);
        subtaskExpected.setIdTask(1);
        Assertions.assertEquals(subtaskExpected, subtaskActual, "Таски с одинаковыми id не равны");
    }

}