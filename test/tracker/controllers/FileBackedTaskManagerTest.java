package tracker.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Status;
import tracker.model.Task;

import java.io.File;
import java.io.IOException;

class FileBackedTaskManagerTest {

    private File file;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("test_", ".csv");
        manager = new FileBackedTaskManager(file);
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        manager.save();
        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(file);
        Assertions.assertEquals(0, loadManager.getTasks().size(), "Список содержит элементы!");
        Assertions.assertEquals(0, loadManager.getEpics().size(), "Список содержит элементы!");
        Assertions.assertEquals(0, loadManager.getSubtasks().size(), "Список содержит элементы!");
    }

    @Test
    void testSaveAndLoadMultipleTasks() {
        Task testTask1 = new Task( 1,"TaskName", Status.NEW, "TaskDescription");
        Task testTask2 = new Task(2, "TaskName2", Status.NEW, "TaskDescription2");

        manager.createTask(testTask1);
        manager.createTask(testTask2);

        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(file);
        Assertions.assertEquals(2, loadManager.getTasks().size());
        Assertions.assertEquals("TaskName", loadManager.getTask(testTask1.getId()).getName());
        Assertions.assertEquals("TaskName2", loadManager.getTask(testTask2.getId()).getName());
    }

}