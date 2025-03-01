package tracker.controllers;

import org.junit.jupiter.api.*;
import tracker.model.Status;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();

    @Test
    void addHistory() {
     //   Managers.getDefaultHistory().getHistory().clear();
        int id1 = taskManager.createTask(new Task("Name", "Description", Status.NEW));
        taskManager.getTask(id1);
        int id2 = taskManager.createTask(new Task("NewName", "NewDescription", Status.NEW));
        taskManager.getTask(id2);
        Assertions.assertEquals(taskManager.getTask(id1), taskManager.getHistory().getFirst(), "Предыдущая версия задачи не сохранилась!");
    }

    @Test
    void checkLimitOfHistoryListInMemoryHistoryManager() {
        Task testTask = new Task("Name", "Description", Status.NEW);
        int id = taskManager.createTask(testTask);
        for (int i = 0; i <= 12; i++) {
            taskManager.getTask(id);
        }
        Assertions.assertEquals(10, taskManager.getHistory().size(), "Лимит списка истории просмотра задач превышен!");
    }
}