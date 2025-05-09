package tracker.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tracker.model.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = File.createTempFile("test_", ".csv");
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void cleanup() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        assertEquals(0, taskManager.getTasks().size(), "Список содержит элементы!");
        assertEquals(0, taskManager.getEpics().size(), "Список содержит элементы!");
        assertEquals(0, taskManager.getSubtasks().size(), "Список содержит элементы!");
    }

    @Test
    void testSaveAndLoadMultipleTasks() {
        Task task1 = new Task( "name1", "description1");
        Task task2 = new Task("name2","description2");
        int idTask1 = taskManager.createTask(task1);
        int idTask2 = taskManager.createTask(task2);

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> loadedTasks = fileBackedTaskManager.getTasks();

        assertTrue(loadedTasks.stream().anyMatch(task -> task.getId() == idTask1));
        assertTrue(loadedTasks.stream().anyMatch(task -> task.getId() == idTask2));
    }
}
