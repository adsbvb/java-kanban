package tracker.controllers;

import tracker.model.Status;
import tracker.model.Task;
import tracker.model.Epic;
import tracker.model.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected int count;

    public int setNewId() {
        count++;
        return count;
    }

    public int createTask(Task task) {
        int newId = setNewId();
        task.setId(newId);
        tasks.put(newId, task);
        return newId;
    }

    public int createEpic(Epic epic) {
        int newId = setNewId();
        epic.setId(newId);
        epics.put(newId, epic);
        return newId;
    }

    public int addSubtask(Subtask subtask) {
        int newId = setNewId();
        subtask.setId(newId);
        subtasks.put(newId, subtask);
        int epicIdFromSubtask = subtask.getEpicId();
        Epic epic = epics.get(epicIdFromSubtask);
        if (epic != null) {
            epic.getSubtasksIds().add(newId);
            updateEpicStatus(epicIdFromSubtask);
        }
        return newId;
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteTask() {
        tasks.clear();
    }

    public void deleteEpic() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            task.setId(id);
            tasks.put(id, task);
        }
    }

    public void updateEpic(int id, Epic epic) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subtasksIds = epics.get(id).getSubtasksIds();
            epic.setId(id);
            epic.setSubtasksIds(subtasksIds);
            epics.put(id, epic);
            updateEpicStatus(id);
        }
    }

    public void updateSubtask(int id, Subtask subtask) {
        if (subtasks.containsKey(id)) {
            if (!epics.containsKey(subtask.getEpicId())) {
                subtask.setEpicId(subtasks.get(id).getEpicId());
            }
            subtask.setId(id);
            subtasks.put(id, subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            for (int subtaskId : epics.get(id).getSubtasksIds()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            ArrayList<Integer> subtasksIds = epics.get(epicId).getSubtasksIds();
            subtasksIds.remove((Integer) id);
            subtasks.remove(id);
            updateEpicStatus(epicId);
        }
    }

    public ArrayList<Subtask> getSubtasksListOfEpic(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Subtask> subtasksList = new ArrayList<>();
            for (int subtaskId : epics.get(id).getSubtasksIds()) {
                subtasksList.add(subtasks.get(subtaskId));
            }
            return subtasksList;
        }
        return null;

    }

    public void updateEpicStatus(int id) {
            int countNew = 0;
            int countDone = 0;
            Status status = Status.IN_PROGRESS;
            if (epics.containsKey(id)) {
                ArrayList<Integer> subtasksIds = epics.get(id).getSubtasksIds();
                if (subtasksIds != null) {
                    for (int idSubtask : subtasksIds) {
                        switch (subtasks.get(idSubtask).getStatus()) {
                            case DONE -> {
                                countDone++;
                                break;
                            }
                            case NEW -> {
                                countNew++;
                                break;
                            }
                        }
                    }
                    if (subtasksIds.size() == countDone) {
                        status = Status.DONE;
                    }
                    if (subtasksIds.size() == countNew) {
                        status = Status.NEW;
                    }
                } else {
                    status = Status.NEW;
                }
            }
            epics.get(id).setStatus(status);
    }
}


