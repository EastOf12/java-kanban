import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;


public class EpicTest {
    @Test
    public void shouldReturnPositiveWhenEqualEpic() {
        Epic epicActual = new Epic("Найти работу", "Найти работу с зарплатой 1000к");
        epicActual.setIdTask(1);
        Epic epicExpected = new Epic("Найти работу", "Найти работу с зарплатой 1000к");
        epicExpected.setIdTask(1);
        Assertions.assertEquals(epicExpected, epicActual, "Таски с одинаковыми id не равны");
    }
}