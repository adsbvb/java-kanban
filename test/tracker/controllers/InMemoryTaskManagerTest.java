package tracker.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.*;

import java.io.File;
import java.io.IOException;

class InMemoryTaskManagerTest {

    private File file;
    private TaskManager taskManager;
    private Task testTask;
    private Epic testEpic;
    private Subtask testSubtask;
    private int idTask;
    private int idEpic;
    private int idSubtask;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("test_", ".csv");
        taskManager = Managers.getDefault(file);
        Assertions.assertNotNull(taskManager, "TaskManager не должен быть null!");

        testTask = new Task(1, "TaskName", Status.NEW, "TaskDescription");
        idTask = taskManager.createTask(testTask);

        testEpic = new Epic(2, "EpicName", Status.NEW, "EpicDescription");
        idEpic = taskManager.createEpic(testEpic);

        testSubtask = new Subtask(3, "SubtaskName", Status.NEW, "SubtaskDescription", idEpic);
        idSubtask = taskManager.addSubtask(testSubtask);
    }

    @Test
    void instancesTaskClassIsEqualIfTheirIdIsEqual() {
        Assertions.assertEquals(taskManager.getTask(1), taskManager.getTask(idTask), "Экземпляры по номеру id не идентичны!");
        Assertions.assertEquals(taskManager.getEpic(2), taskManager.getEpic(idEpic), "Экземпляры по номеру id не идентичны!");
        Assertions.assertEquals(taskManager.getSubtask(3), taskManager.getSubtask(idSubtask), "Экземпляры по номеру id не идентичны!");
    }

    @Test
    void subtaskObjectCannotBeMadeItsOwnEpic() {
        Subtask subtask = taskManager.getSubtask(idSubtask);
        Assertions.assertNotEquals(idSubtask, subtask.getEpicId(), "Подзадача приняла id эпика!");
    }

    @Test
    void testAddNewTask() {
        Assertions.assertNotNull(taskManager.getTask(idTask), "Задача не проинициализирована!");
        Assertions.assertEquals(testTask, taskManager.getTask(idTask), "Задачи не совпадают");
        Assertions.assertEquals(1, taskManager.getTasks().size(), "Неверное количество задач!");
    }

    @Test
    void testAddNewEpic() {
        Assertions.assertNotNull(taskManager.getEpic(idEpic), "Эпик не проинициализирован!");
        Assertions.assertEquals(testEpic, taskManager.getEpic(idEpic), "Задачи не совпадают");
        Assertions.assertEquals(1, taskManager.getEpics().size(), "Неверное количество задач!");
    }

    @Test
    void testAddNewSubtask() {
        Assertions.assertEquals(taskManager.getSubtask(3), taskManager.getSubtask(idSubtask), "Экземпляры по номеру id не идентичны!");
        Assertions.assertEquals(testSubtask, taskManager.getSubtask(idSubtask), "Задачи не совпадают");
        Assertions.assertEquals(1, taskManager.getSubtasks().size(), "Неверное количество задач!");
    }

    @Test
    void testDeleteAllTask() {
        taskManager.deleteTasks();
        Assertions.assertEquals(0, taskManager.getTasks().size());
        taskManager.deleteEpics();
        Assertions.assertEquals(0, taskManager.getEpics().size());
        taskManager.deleteSubtasks();
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void givenIdAndGeneratedIdDoNotConflictInManager() {
        Task testTask2 = new Task("Task2", Status.NEW, "Task2");
        int idTask2 = taskManager.createTask(testTask2);
        Assertions.assertNotEquals(idTask, idTask2, "Конфликт id номеров в менеджере!");
    }

    @Test
    void checkIrrelevantSubtaskInEpicList() {
        taskManager.deleteSubtaskById(idSubtask);
        Assertions.assertEquals(0, taskManager.getSubtasksListOfEpic(idEpic).size(), "Неактуальная подзадача внутри эпика не удалена!");
    }
}