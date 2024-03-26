import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;


public class EpicTest {
    @Test
    public void shouldReturnPositiveWhenEqualEpic() {
        //Получаем объекты типа Epic и присваиваем им одинаковые ID.
        Epic epicActual = new Epic("Найти работу", "Найти работу с зарплатой 1000к");
        epicActual.setIdTask(1);
        Epic epicExpected = new Epic("Найти работу", "Найти работу с зарплатой 1000к");
        epicExpected.setIdTask(1);

        //Сраниваем таски.
        Assertions.assertEquals(epicExpected, epicActual, "Таски с одинаковыми id не равны");
    }

}