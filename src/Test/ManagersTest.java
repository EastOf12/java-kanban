package Test;

import manager.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    public void ShouldNotBeNullTaskManager() {
        Assertions.assertNotNull(Managers.getDefault());
    }
}