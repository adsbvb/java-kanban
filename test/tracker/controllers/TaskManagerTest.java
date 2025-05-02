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
        Task task = new Task(1, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int idTask = taskManager.createTask(task);

        assertNotNull(taskManager.getTask(idTask), "Задача не проинициализирована!");
    }

    @Test
    void testCreateNewEpic() {
        Epic epic = new Epic(1, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int idEpic = taskManager.createEpic(epic);

        assertNotNull(taskManager.getEpic(idEpic), "Задача не проинициализирована!");
    }

    @Test
    void testCreateNewSubtask() {
        Epic epic = new Epic(1, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(2, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), idEpic);
        int idSubtask = taskManager.createSubtask(subtask);

        assertNotNull(taskManager.getSubtask(idSubtask), "Задача не проинициализирована!");
    }

    @Test
    protected void testTasksEqualsIfTheirIdIsEquals() {
        Task task1 = new Task(1, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        int idTask1 = taskManager.createTask(task1);

        assertEquals(taskManager.getTask(1), taskManager.getTask(idTask1), "Экземпляры по номеру id не идентичны!");
    }

    @Test
    void subtaskObjectCannotBeMadeOwnEpic() {
        Epic epic = new Epic(1, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(2, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), idEpic);
        int idSubtask = taskManager.createSubtask(subtask);

        assertNotEquals(idSubtask, idEpic, "Подзадача приняла id эпика!");
    }

    @Test
    void testDeleteAllTasks() {
        Task task = new Task(1, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.createTask(task);
        Epic epic = new Epic(2, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(3, "Name", "Description", Status.NEW, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(5), idEpic);
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
        Task task1 = new Task(1, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int idTask1 = taskManager.createTask(task1);
        Task task2 = new Task(2, "Name", "Description", Status.NEW, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(5));
        int idTask2 = taskManager.createTask(task2);

        assertNotEquals(idTask1, idTask2, "Конфликт id номеров в менеджере!");
    }

    @Test
    protected void testEpicStatusAllNew() {
        Epic epic = new Epic(1, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int idEpic = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(2, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), idEpic);
        Subtask subtask2 = new Subtask(3, "Name", "Description", Status.NEW, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(5), idEpic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.NEW, taskManager.getEpic(idEpic).orElseThrow().getStatus());
    }

    @Test
    protected void testEpicStatusAllDone() {
        Epic epic = new Epic(1, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int idEpic = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(2, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), idEpic);
        Subtask subtask2 = new Subtask(3, "Name", "Description", Status.NEW, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(5), idEpic);
        int idSubtask1 = taskManager.createSubtask(subtask1);
        int idSubtask2 = taskManager.createSubtask(subtask2);
        taskManager.updateSubtask(new Subtask(idSubtask1, "Name", "Description", Status.DONE, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5), idEpic));
        taskManager.updateSubtask(new Subtask(idSubtask2, "Name", "Description", Status.DONE, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(5), idEpic));

        assertEquals(Status.DONE, taskManager.getEpic(idEpic).orElseThrow().getStatus());
    }

    @Test
    protected void testEpicStatusNewDone() {
        Epic epic = new Epic(1, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int idEpic = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(2, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), idEpic);
        Subtask subtask2 = new Subtask(3, "Name", "Description", Status.DONE, LocalDateTime.now().plusMinutes(5), Duration.ofMinutes(5), idEpic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(idEpic).orElseThrow().getStatus());
    }

    @Test
    protected void testEpicStatusInProgress() {
        Epic epic = new Epic(1, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int idEpic = taskManager.createEpic(epic);

        Subtask subtask = new Subtask(2, "Name", "Description", Status.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(5), idEpic);
        taskManager.createSubtask(subtask);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(idEpic).orElseThrow().getStatus());
    }

    @Test
    void checkIrrelevantSubtaskInEpicList() {
        Epic epic = new Epic(1, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(2, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), idEpic);
        int idSubtask = taskManager.createSubtask(subtask);

        Subtask testSubtask = taskManager.getSubtask(idSubtask).orElseThrow();
        taskManager.removeSubtaskById(idSubtask);

        assertFalse(taskManager.getSubtasksListOfEpic(idEpic).contains(testSubtask), "Неактуальная подзадача внутри эпика не удалена!");
    }

    @Test
    public void testSubtaskHasAnEpic() {
        Epic epic = new Epic(1, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int idEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask(2, "Name", "Description", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5), idEpic);
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
