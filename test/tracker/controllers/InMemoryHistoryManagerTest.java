package tracker.controllers;

import org.junit.jupiter.api.*;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.File;
import java.io.IOException;

class InMemoryHistoryManagerTest {

    private File file;
    private TaskManager taskManager;
    private Task testTask;
    private Epic testEpic;
    private Subtask testSubtask;
    private int idTask;
    private int idEpic;
    private int idSubtask;

    @BeforeEach
    void setUp() throws IOException{
        file = File.createTempFile("test_", ".csv");
        taskManager = Managers.getDefault(file);
        Assertions.assertNotNull(taskManager);

        testTask = new Task(1, "TaskName", Status.NEW, "TaskDescription");
        idTask = taskManager.createTask(testTask);

        testEpic = new Epic(2, "EpicName", Status.NEW, "EpicDescription");
        idEpic = taskManager.createEpic(testEpic);

        testSubtask = new Subtask(3, "SubtaskName", Status.NEW, "SubtaskDescription", idEpic);
        idSubtask = taskManager.addSubtask(testSubtask);
    }

    @Test
    void testAddHistory() {
        taskManager.getTask(idTask);
        taskManager.getEpic(idEpic);
        taskManager.getSubtask(idSubtask);
        Assertions.assertEquals(taskManager.getHistory().getFirst(), taskManager.getTask(idTask), "Предыдущая задача не сохранилась!");
    }

    @Test
    void testRemove() {
        taskManager.getTask(idTask);
        taskManager.remove(idTask);
        Assertions.assertEquals(0, taskManager.getHistory().size(), "Задача не была удалена из списка!");
    }

    @Test
    void checkReEntryTaskInHistoryList() {
        taskManager.getTask(idTask);
        taskManager.getEpic(idEpic);
        taskManager.getSubtask(idSubtask);
        taskManager.getTask(idTask);
        Assertions.assertNotEquals(taskManager.getHistory().getFirst(), taskManager.getTask(idTask), "Задача была записана дважды!");
        Assertions.assertEquals(taskManager.getHistory().getLast(), taskManager.getTask(idTask), "Задача не была перезаписана в список!");
    }
}