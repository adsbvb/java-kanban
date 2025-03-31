package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    int createTask(Task task);

    int createEpic(Epic epic);

    int addSubtask(Subtask subtask);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void updateTask(int id, Task task);

    void updateEpic(int id, Epic epic);

    void updateSubtask(int id, Subtask subtask);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    ArrayList<Subtask> getSubtasksListOfEpic(int id);

    void updateEpicStatus(int id);

    List<Task> getHistory();

    void remove(int id);

}
