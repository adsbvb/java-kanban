package tracker.controllers;

import tracker.model.Task;

import java.util.List;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static List<Task> getDefaultHistory() {
        return new InMemoryHistoryManager().getHistory();
    }
}
