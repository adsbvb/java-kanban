package tracker.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        Assertions.assertNotNull(Managers.getDefault());
    }

    @Test
    void getDefaultHistory() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }
}