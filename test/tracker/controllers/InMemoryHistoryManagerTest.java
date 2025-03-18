package tracker.controllers;

import org.junit.jupiter.api.*;
import tracker.model.Status;
import tracker.model.Task;

class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();

    Task testTask1 = new Task("Name", "Description", Status.NEW);
    Task testTask2 = new Task("NewName", "NewDescription", Status.NEW);
    Task testTask3 = new Task("NewNewName", "NewNewDescription", Status.NEW);
    int id1 = taskManager.createTask(testTask1);
    int id2 = taskManager.createTask(testTask2);
    int id3 = taskManager.createTask(testTask3);

    @Test
    void addHistory() {
        taskManager.getTask(id1);
        taskManager.getTask(id2);
        Assertions.assertEquals(taskManager.getHistory().getFirst(), taskManager.getTask(id1), "Предыдущая версия задачи не сохранилась!");
    }

    @Test
    void checkReEntryTaskInHistoryList() {
        taskManager.getTask(id1);
        taskManager.getTask(id2);
        taskManager.getTask(id3);
        taskManager.getTask(id1);
        Assertions.assertNotEquals(taskManager.getHistory().getFirst(), taskManager.getTask(id1), "Задача была записана дважды!");
        Assertions.assertEquals(taskManager.getHistory().getLast(), taskManager.getTask(id1), "Задача не была перезаписана в список!");
    }
}