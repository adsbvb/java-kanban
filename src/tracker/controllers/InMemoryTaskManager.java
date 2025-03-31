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
        this.historyManager = Managers.getDefaultHistory();
    }

    public int setNewId() {
        count++;
        return count;
    }

    @Override
    public int createTask(Task task) {
        int newId = setNewId();
        Task newTask = new Task(newId, task.getName(), task.getStatus(), task.getDescription());
        tasks.put(newId, newTask);
        return newId;
    }

    @Override
    public int createEpic(Epic epic) {
        int newId = setNewId();
        Epic newEpic = new Epic(newId, epic.getName(), epic.getStatus(), epic.getDescription());
        epics.put(newId, newEpic);
        return newId;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            return 0;
        }
        int epicId = subtask.getEpicId();
        int newId = setNewId();
        Subtask newSubtask = new Subtask(newId, subtask.getName(), subtask.getStatus(), subtask.getDescription(), epicId);
        subtasks.put(newId, newSubtask);
        epics.get(epicId).getSubtasks().add(newSubtask);
        updateEpicStatus(epicId);
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
    public void deleteTasks() {
        deleteAllTasksFromHistory(tasks);
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        deleteAllTasksFromHistory(epics);
        deleteAllTasksFromHistory(subtasks);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        deleteAllTasksFromHistory(subtasks);
        for (Epic epic : epics.values()) {
            epic.clearSubtask();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    public <T> void deleteAllTasksFromHistory(Map<Integer, T> tasksMap) {
        for (int id : tasksMap.keySet()) {
            historyManager.remove(id);
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(tasks.get(id));
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epics.get(id));
        }
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtasks.get(id));
        }
        return subtask;
    }

    @Override
    public void updateTask(int id, Task task) {
        if (!tasks.containsKey(id) || task == null) {
            return;
        }
        Task newTask = new Task(id, task.getName(), task.getStatus(), task.getDescription());
        tasks.put(id, newTask);
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        if (!epics.containsKey(id) || epic == null) {
            return;
        }
        Epic newEpic = new Epic(id, epic.getName(), epic.getStatus(), epic.getDescription());
        newEpic.setSubtasks(epics.get(id).getSubtasks());
        epics.put(id, newEpic);
        updateEpicStatus(id);
    }

    @Override
    public void updateSubtask(int id, Subtask subtask) {
        if (!subtasks.containsKey(id) || subtask == null) {
            return;
        }
        int epicId = subtasks.get(id).getEpicId();
        Subtask newSubtask = new Subtask(id, subtask.getName(), subtask.getStatus(), subtask.getDescription(), epicId);
        epics.get(epicId).getSubtasks().remove(subtasks.get(id));
        epics.get(epicId).getSubtasks().add(newSubtask);
        subtasks.put(id, newSubtask);
        updateEpicStatus(epicId);
    }

    @Override
    public void deleteTaskById(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (!epics.containsKey(id)) {
            return;
        }
        for (Subtask subtask : epics.get(id).getSubtasks()) {
            historyManager.remove(subtask.getId());
            subtasks.remove(subtask.getId());
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            return;
        }
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).getSubtasks().remove(subtasks.get(id));
        historyManager.remove(id);
        subtasks.remove(id);
        updateEpicStatus(epicId);
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
        if (!epics.containsKey(id)) {
            return;
        }
        ArrayList<Subtask> subtasksList = epics.get(id).getSubtasks();
        Status status = Status.NEW;
        if (subtasksList != null && !subtasksList.isEmpty()) {
            int countDone = 0;
            int countNew = 0;
            for (Subtask subtask : subtasksList) {
                switch (subtask.getStatus()) {
                    case DONE -> countDone++;
                    case NEW -> countNew++;
                }
            }
            if (subtasksList.size() == countDone) {
                status = Status.DONE;
            } else if (subtasksList.size() == countNew) {
                status = Status.NEW;
            } else {
                status = Status.IN_PROGRESS;
            }
        }
        epics.get(id).setStatus(status);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void remove(int id) {
        historyManager.remove(id);
    }
}


