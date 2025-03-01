package tracker.controllers;

import tracker.model.Status;
import tracker.model.Task;
import tracker.model.Epic;
import tracker.model.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private int count;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.count = 0;
        this.historyManager = Managers.getDefaultHistory();;
    }

    public int setNewId() {
        count++;
        return count;
    }

    @Override
    public int createTask(Task task) {
        int newId = setNewId();
        task.setId(newId);
        tasks.put(newId, task);
        return newId;
    }

    @Override
    public int createEpic(Epic epic) {
        int newId = setNewId();
        epic.setId(newId);
        epics.put(newId, epic);
        return newId;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            return 0;
        }
        int newId = setNewId();
        subtask.setId(newId);
        subtasks.put(newId, subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasks().add(subtask);
        updateEpicStatus(subtask.getEpicId());
        return newId;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteTask() {
        tasks.clear();
    }

    @Override
    public void deleteEpic() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtask();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            task.setId(id);
            tasks.put(id, task);
        }
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        if (epics.containsKey(id)) {
            ArrayList<Subtask> subtasksList = epics.get(id).getSubtasks();
            epic.setId(id);
            epic.setSubtasks(subtasksList);
            epics.put(id, epic);
            updateEpicStatus(id);
        }
    }

    @Override
    public void updateSubtask(int id, Subtask subtask) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            subtask.setEpicId(epicId);
            subtask.setId(id);
            epics.get(epicId).getSubtasks().remove(subtasks.get(id));
            epics.get(epicId).getSubtasks().add(subtask);
            subtasks.put(id, subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            for (Subtask subtask : epics.get(id).getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
            epics.remove(id);
        }

    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            epics.get(epicId).getSubtasks().remove(subtasks.get(id));
            subtasks.remove(id);
            updateEpicStatus(epicId);
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksListOfEpic(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id).getSubtasks();
        }
        return null;

    }

    @Override
    public void updateEpicStatus(int id) {
            int countNew = 0;
            int countDone = 0;
            Status status = Status.IN_PROGRESS;
            if (epics.containsKey(id)) {
                ArrayList<Subtask> subtasksList = epics.get(id).getSubtasks();
                if (subtasksList != null) {
                    for (Subtask subtask : subtasksList) {
                        switch (subtask.getStatus()) {
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
                    if (subtasksList.size() == countDone) {
                        status = Status.DONE;
                    }
                    if (subtasksList.size() == countNew) {
                        status = Status.NEW;
                    }
                } else {
                    status = Status.NEW;
                }
            }
            epics.get(id).setStatus(status);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}


