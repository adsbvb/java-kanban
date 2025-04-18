package tracker.controllers;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void testGetDefaultManager() throws IOException {
        File file = File.createTempFile("test_", ".csv");
        assertNotNull(Managers.getDefault(file));
    }

    @Test
    void testGetDefaultHistory() {
        assertNotNull(Managers.getDefaultHistory());
    }
}