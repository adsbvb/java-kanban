package tracker.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class ManagersTest {

    @Test
    void testGetDefault() throws IOException {
        File file = File.createTempFile("test_", ".csv");
        Assertions.assertNotNull(Managers.getDefault(file));
    }

    @Test
    void testGetDefaultHistory() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }
}