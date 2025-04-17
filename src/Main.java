import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.*;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        File file = null;
        try {
            file = File.createTempFile("save_", ".csv");
        } catch (IOException e) {
            System.out.println(e);
        }

        TaskManager taskManager = Managers.getDefault(file);

        taskManager.createTask(new Task("Task1", "Description1", 2025, 5, 12, 2, 30, 120));
        taskManager.createTask(new Task("Task2", "Description2", 2025, 6, 10, 13, 50, 60));
        taskManager.createTask(new Task("Task3", "Description3", 2025, 7, 13, 1, 35, 70));
        taskManager.createTask(new Task("Task4", "Description4", 2025, 8, 13, 1, 30, 140));

        taskManager.createEpic(new Epic("Epic5", "Description1"));
        taskManager.createEpic(new Epic("Epic6", "Description2"));
        taskManager.createEpic(new Epic("Epic7", "Description3"));

        taskManager.createSubtask(new Subtask("Subtask8", "Description1", 2025, 6, 1, 12, 14, 30, 5));
        taskManager.createSubtask(new Subtask("Subtask9", "Description2", 2025, 7, 1, 10, 14, 30, 5));
        taskManager.createSubtask(new Subtask("Subtask10", "Description3", 2025, 8, 1, 12, 14, 30, 6));
        taskManager.createSubtask(new Subtask("Subtask11", "Description4", 2025, 9, 22, 12, 19, 30, 7));
        taskManager.createSubtask(new Subtask("Subtask12", "Description5", 2025, 9, 22, 14, 14, 30, 7));

        taskManager.createTask(new Task("Task", "Task"));
        taskManager.updateTask(13, new Task("Task13", "Description4", 2025, 10, 13, 1, 30, 140));

        System.out.println(taskManager.getPrioritizedTasks());

    }
}
