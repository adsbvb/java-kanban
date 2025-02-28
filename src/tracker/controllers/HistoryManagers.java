package tracker.controllers;

import tracker.model.Task;

import java.util.List;

public interface HistoryManagers {

    void add(Task task);

    List<Task> getHistory();
}
