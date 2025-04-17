package tracker.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Status;
import tracker.model.Subtask;
import tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    protected void setUp() {
        taskManager = createTaskManager();
    }

    protected abstract T createTaskManager();

    @Test
    void testCreateNewTask() {
        Task task = new Task("name", "description");
        int idTask = taskManager.createTask(task);

        assertNotNull(taskManager.getTask(idTask), "Задача не проинициализирована!");
    }

    @Test
    void testCreateNewEpic() {
        Epic epic = new Epic("name", "description");
        int idEpic = taskManager.createEpic(epic);

        assertNotNull(taskManager.getEpic(idEpic), "Задача не проинициализирована!");
    }

    @Test
    void testCreateNewSubtask() {
        Epic epic = new Epic("name", "description");
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("name", "description", idEpic);
        int idSubtask = taskManager.createSubtask(subtask);

        assertNotNull(taskManager.getSubtask(idSubtask), "Задача не проинициализирована!");
    }

    @Test
    protected void testTasksEqualsIfTheirIdIsEquals() {
        Task task1 = new Task("name1", "description1");
        Task task2 = new Task("name2", "description2");

        int idTask1 = taskManager.createTask(task1);
        int idTask2 = taskManager.createTask(task2);

        assertEquals(taskManager.getTask(1), taskManager.getTask(idTask1), "Экземпляры по номеру id не идентичны!");
        assertEquals(taskManager.getEpic(2), taskManager.getEpic(idTask2), "Экземпляры по номеру id не идентичны!");
    }

    @Test
    void subtaskObjectCannotBeMadeOwnEpic() {
        Epic epic = new Epic("epicName", "epicDescription");
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtaskName", "subtaskDescription", idEpic);
        int idSubtask = taskManager.createSubtask(subtask);

        assertNotEquals(idSubtask, idEpic, "Подзадача приняла id эпика!");
    }

    @Test
    void testDeleteAllTasks() {
        Task task = new Task("taskName", "taskDescription");
        taskManager.createTask(task);
        Epic epic = new Epic("epicName", "epicDescription");
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtaskName", "subtaskDescription", idEpic);
        taskManager.createSubtask(subtask);

        taskManager.deleteTasks();
        taskManager.deleteEpics();
        taskManager.deleteSubtasks();

        assertEquals(0, taskManager.getTasks().size(), "Список задач содержит объекты!");
        assertEquals(0, taskManager.getEpics().size(), "Список эпиков содержит объекты!");
        assertEquals(0, taskManager.getSubtasks().size(), "Список подзадач содержит объекты!");
    }

    @Test
    void testGivenIdAndGeneratedIdNotConflictInManager() {
        Task task1 = new Task(1, "Name", "Description", Status.NEW);
        int idTask1 = taskManager.createTask(task1);
        Task task2 = new Task(idTask1, "Name", "Description", Status.NEW);
        int idTask2 = taskManager.createTask(task2);

        assertNotEquals(idTask1, idTask2, "Конфликт id номеров в менеджере!");
    }

    @Test
    protected void testEpicStatusAllNew() {
        Epic epic = new Epic("EpicTestName", "EpicTestDescription");
        int idEpic = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(10, "SubtaskName1", "SubtaskDescription1", Status.NEW, idEpic);
        Subtask subtask2 = new Subtask(20, "SubtaskName2", "SubtaskDescription2", Status.NEW, idEpic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.NEW, taskManager.getEpic(idEpic).orElseThrow().getStatus());
    }

    @Test
    protected void testEpicStatusAllDone() {
        Epic epic = new Epic("EpicTestName", "EpicTestDescription");
        int idEpic = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(10, "SubtaskName1", "SubtaskDescription1", Status.NEW, idEpic);
        Subtask subtask2 = new Subtask(20, "SubtaskName2", "SubtaskDescription2", Status.NEW, idEpic);
        int idSubtask1 = taskManager.createSubtask(subtask1);
        int idSubtask2 = taskManager.createSubtask(subtask2);
        taskManager.updateSubtask(idSubtask1, new Subtask(10, "SubtaskName1", "SubtaskDescription1", Status.DONE, idEpic));
        taskManager.updateSubtask(idSubtask2, new Subtask(20, "SubtaskName1", "SubtaskDescription1", Status.DONE, idEpic));

        assertEquals(Status.DONE, taskManager.getEpic(idEpic).orElseThrow().getStatus());
    }

    @Test
    protected void testEpicStatusNewDone() {
        Epic epic = new Epic("EpicTestName", "EpicTestDescription");
        int idEpic = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(10, "SubtaskName1", "SubtaskDescription1", Status.NEW, idEpic);
        Subtask subtask2 = new Subtask(20, "SubtaskName2", "SubtaskDescription2", Status.DONE, idEpic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(idEpic).orElseThrow().getStatus());
    }

    @Test
    protected void testEpicStatusInProgress() {
        Epic epic = new Epic("EpicTestName", "EpicTestDescription");
        int idEpic = taskManager.createEpic(epic);

        Subtask subtask = new Subtask(10, "SubtaskName", "SubtaskDescription", Status.IN_PROGRESS, idEpic);
        taskManager.createSubtask(subtask);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(idEpic).orElseThrow().getStatus());
    }

    @Test
    void checkIrrelevantSubtaskInEpicList() {
        Epic epic = new Epic("epicName", "epicDescription");
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtaskName", "subtaskDescription", idEpic);
        int idSubtask = taskManager.createSubtask(subtask);

        Subtask testSubtask = taskManager.getSubtask(idSubtask).orElseThrow();
        taskManager.removeSubtaskById(idSubtask);

        assertFalse(taskManager.getSubtasksListOfEpic(idEpic).contains(testSubtask), "Неактуальная подзадача внутри эпика не удалена!");
    }

    @Test
    public void testSubtaskHasAnEpic() {
        Epic epic = new Epic("epicName", "epicDescription");
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("subtaskName", "subtaskDescription", idEpic);
        int idSubtask = taskManager.createSubtask(subtask);

        int idEpicInSubtask = taskManager.getSubtask(idSubtask).orElseThrow().getEpicId();
        Epic retrievedEpic = taskManager.getEpic(idEpicInSubtask).orElseThrow();

        assertNotNull(retrievedEpic, "Ожидаемый эпик не найден!");
        assertEquals(idEpic, idEpicInSubtask, "ID эпика и ID эпика в подзадаче не идентичны!");
    }

    @Test
    public void testTaskOverlapsInTime() {
        Task task1 = new Task(1, "taskName", "taskDescription", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(60));
        Task task2 = new Task(2, "taskName", "taskDescription", Status.NEW, LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(60));
        taskManager.createTask(task1);
        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createTask(task2);
        }, "Присутствует пересечение временных интервалов задач");
    }


}
