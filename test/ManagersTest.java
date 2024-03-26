import manager.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    public void shouldNotBeNullTaskManager() {
        Assertions.assertNotNull(Managers.getDefault());
    }

    @Test
    public void shouldNotBeNullHistoryManager() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }
}