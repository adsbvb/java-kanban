package tracker.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

class InMemoryTaskManagerTest {

    TaskManager taskManager = Managers.getDefault();
    Task testTask = new Task(1, "TaskName", "TaskDescription", Status.NEW);
    final int idTask = taskManager.createTask(testTask);
    Epic testEpic = new Epic(2, "EpicName", "EpicDescription", Status.NEW);
    final int idEpic = taskManager.createEpic(testEpic);
    Subtask testSubtask = new Subtask(3, "SubtaskName", "SubtaskDescription", Status.NEW, 2);
    final int idSubtask = taskManager.addSubtask(testSubtask);

    @Test
    void instancesTaskClassIsEqualIfTheirIdIsEqual() {
        Assertions.assertEquals(taskManager.getTask(1), taskManager.getTask(idTask), "Экземпляры по номеру id не идентичны!");
        Assertions.assertEquals(taskManager.getEpic(2), taskManager.getEpic(idEpic), "Экземпляры по номеру id не идентичны!");
        Assertions.assertEquals(taskManager.getSubtask(3), taskManager.getSubtask(idSubtask), "Экземпляры по номеру id не идентичны!");
    }

    @Test
    void subtaskObjectCannotBeMadeItsOwnEpic() {
        Subtask subtask = taskManager.getSubtask(3);
        Assertions.assertNotEquals(3, subtask.getEpicId(), "Подзадача приняла id эпика!");
    }

    @Test
    void addNewTask() {
        Assertions.assertNotNull(taskManager.getTask(idTask), "Задача не проинициализирована!");
        Assertions.assertEquals(testTask, taskManager.getTask(idTask), "Задачи не совпадают");
        Assertions.assertEquals(1, taskManager.getTasks().size(), "Неверное количество задач!");
    }

    @Test
    void addNewEpic() {
        Assertions.assertNotNull(taskManager.getEpic(idEpic), "Эпик не проинициализирован!");
        Assertions.assertEquals(testEpic, taskManager.getEpic(idEpic), "Задачи не совпадают");
        Assertions.assertEquals(1, taskManager.getEpics().size(), "Неверное количество задач!");
    }

    @Test
    void addNewSubtask() {
        Assertions.assertEquals(taskManager.getSubtask(3), taskManager.getSubtask(idSubtask), "Экземпляры по номеру id не идентичны!");
        Assertions.assertEquals(testSubtask, taskManager.getSubtask(idSubtask), "Задачи не совпадают");
        Assertions.assertEquals(1, taskManager.getSubtasks().size(), "Неверное количество задач!");
    }

    @Test
    void deleteAllTask() {
        taskManager.deleteTask();
        Assertions.assertEquals(0, taskManager.getTasks().size());
        taskManager.deleteEpic();
        Assertions.assertEquals(0, taskManager.getEpics().size());
        taskManager.deleteSubtasks();
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void givenIdAndGeneratedIdDoNotConflictInManager() {
        Task testTask1 = new Task("Task1", "Task1", Status.NEW);
        int id1 = taskManager.createTask(testTask1);
        Task testTask2 = new Task("Task2", "Task2", Status.NEW);
        int id2 = taskManager.createTask(testTask2);
        Assertions.assertNotEquals(id1, id2, "Конфликт id номеров в менеджере!");
    }

    @Test
    void checkIrrelevantSubtaskInEpicList() {
        taskManager.deleteSubtaskById(idSubtask);
        Assertions.assertEquals(0, taskManager.getSubtasksListOfEpic(idEpic).size(), "Неактуальная подзадача внутри эпика не удалена!");
    }
}