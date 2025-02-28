package tracker.controllers;

import tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManagers {

    private final static List<Task> TASK_HISTORY_LIST = new ArrayList<>();

    @Override
    public void add(Task task) {
        int maxHistorySize = 10;
        if (TASK_HISTORY_LIST.size() == maxHistorySize) {
            TASK_HISTORY_LIST.removeFirst();
            TASK_HISTORY_LIST.add(task);
        } else {
            TASK_HISTORY_LIST.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return TASK_HISTORY_LIST;
    }

}
