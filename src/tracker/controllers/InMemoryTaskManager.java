package tracker.controllers;

import tracker.model.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private int count;
    private final HistoryManager historyManager;
    private final Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.count = 0;
        this.historyManager = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    public int generateId() {
        count++;
        return count;
    }

    @Override
    public int createTask(Task task) {
        if (taskOverlapsInTime(task)) {
            throw new IllegalArgumentException("Созданная задача \'" + task.getName() + "\' пересекается с существущей задачей!");
        }
        int id = generateId();
        Task newTask = new Task(id, task);
        tasks.put(id, newTask);
        if (newTask.getStartTime() != null) {
            prioritizedTasks.add(newTask);
        }
        return id;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteTasks() {
        removeAllTasksFromHistory(tasks);
        List<Task> tasksToRemove = prioritizedTasks.stream()
                .filter(task -> task.getType().equals(TaskType.TASK))
                .toList();
        tasksToRemove.forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public Optional<Task> getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
            return Optional.of(task);
        }
        return Optional.empty();
    }

    @Override
    public void updateTask(int id, Task task) {
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Задача с заданным ID " + id + " не найдена!");
        }
        if (taskOverlapsInTime(task)) {
            throw new IllegalArgumentException("Обновленная задача \'" + task.getName() + "\' пересекается с существущей задачей!");
        }
        Task newTask = new Task(id, task);
        prioritizedTasks.remove(task);
        if (newTask.getStartTime() != null) {
            prioritizedTasks.add(newTask);
        }
        tasks.put(id, newTask);
    }

    @Override
    public void removeTaskById(int id) {
        if (!tasks.containsKey(id)) {
            throw new IllegalArgumentException("Задача с заданным ID " + id + " не найдена!");
        }
        removeFromHistoryList(id);
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public int createEpic(Epic epic) {
        int id = generateId();
        Epic newEpic = new Epic(id, epic);
        updateEpicStartTime(newEpic);
        epics.put(id, newEpic);
        return id;
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteEpics() {
        removeAllTasksFromHistory(epics);
        removeAllTasksFromHistory(subtasks);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Optional<Epic> getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epics.get(id));
            return Optional.of(epic);
        }
        return Optional.empty();
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        if (!epics.containsKey(id)) {
            throw new IllegalArgumentException("Эпик с заданным ID " + id + " не найден!");
        }
        Epic newEpic = new Epic(id, epic);
        newEpic.addSubtasks(epics.get(id).getSubtasks());
        epics.put(id, newEpic);
        updateEpicStatus(epic);
    }

    @Override
    public void removeEpicById(int id) {
        if (!epics.containsKey(id)) {
            throw new IllegalArgumentException("Эпик с заданным ID " + id + " не найден!");
        }
        for (Subtask subtask : epics.get(id).getSubtasks()) {
            removeFromHistoryList(subtask.getId());
            subtasks.remove(subtask.getId());
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        if (!epics.containsValue(epic)) {
            throw new IllegalArgumentException("Эпик с заданным ID " + epic.getId() + " не найден!");
        }
        epic.updateStatus();
    }

    public int createSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Данная подзадача содержит id несуществующего эпика!");
        }
        if (taskOverlapsInTime(subtask)) {
            throw new IllegalArgumentException("Созданная подзадача \'" + subtask.getName() + "\' пересекается с существущей задачей!");
        }
        int id = generateId();
        Epic epic = epics.get(subtask.getEpicId());
        Subtask newSubtask = new Subtask(id, subtask);
        subtasks.put(id, newSubtask);
        epic.getSubtasks().add(newSubtask);
        updateEpicStatus(epic);
        if (newSubtask.getStartTime() != null) {
            prioritizedTasks.add(newSubtask);
            updateEpicStartTime(epic);
        }
        return id;
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteSubtasks() {
        removeAllTasksFromHistory(subtasks);
        epics.values().forEach(
                epic -> {
                    epic.clearSubtasks();
                    updateEpicStatus(epic);
                    updateEpicStartTime(epic);
                }
        );
        List<Task> tasksToRemove = prioritizedTasks.stream()
                .filter(subtask -> subtask.getType().equals(TaskType.SUBTASK))
                .toList();
        tasksToRemove.forEach(prioritizedTasks::remove);
        subtasks.clear();
    }

    @Override
    public Optional<Subtask> getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtasks.get(id));
            return Optional.of(subtask);
        }
        return Optional.empty();
    }

    @Override
    public void updateSubtask(int id, Subtask subtask) {
        if (!subtasks.containsKey(id)) {
            throw new IllegalArgumentException("Позадача с заданным ID " + id + " не найдена!");
        }
        if (taskOverlapsInTime(subtask)) {
            throw new IllegalArgumentException("Обновленная подзадача \'" + subtask.getName() + "\' пересекается с существущей задачей!");
        }
        Epic epic = epics.get(subtask.getEpicId());
        Subtask newSubtask = new Subtask(id, subtask);
        epic.getSubtasks().remove(subtasks.get(id));
        epic.getSubtasks().add(newSubtask);
        updateEpicStatus(epic);
        updateEpicStartTime(epic);
        prioritizedTasks.remove(subtask);
        if (newSubtask.getStartTime() != null) {
            prioritizedTasks.add(newSubtask);
        }
        subtasks.put(id, newSubtask);
    }

    @Override
    public void removeSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            throw new IllegalArgumentException("Позадача с заданным ID " + id + " не найдена!");
        }
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.getSubtasks().remove(subtasks.get(id));
        updateEpicStartTime(epic);
        removeFromHistoryList(id);
        prioritizedTasks.remove(subtasks.get(id));
        subtasks.remove(id);
        updateEpicStatus(epic);
    }

    @Override
    public ArrayList<Subtask> getSubtasksListOfEpic(int id) {
        return Optional.ofNullable(epics.get(id))
                .map(Epic::getSubtasks)
                .orElse(new ArrayList<>());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void removeFromHistoryList(int id) {
        historyManager.remove(id);
    }

    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void removeAllTasksFromHistory(Map<Integer, ? extends Task> tasksMap) {
        for (int id : tasksMap.keySet()) {
            removeFromHistoryList(id);
        }
    }

    protected void addTask(Task task) {
        switch (task.getType()) {
            case TASK -> tasks.put(task.getId(), task);
            case EPIC -> epics.put(task.getId(), (Epic) task);
            case SUBTASK -> subtasks.put(task.getId(), (Subtask) task);
        }
    }

    private void updateEpicStartTime(Epic epic) {
        epic.setStartTime();
    }

    private Boolean taskOverlapsInTime(Task task) {
        if (task.getStartTime() == null) {
            return false;
        }
        if (prioritizedTasks.isEmpty()) {
            return false;
        }
        return prioritizedTasks.stream()
                .anyMatch(element -> element.getStartTime().isBefore(task.getEndTime()) &&
                        element.getEndTime().isAfter(task.getStartTime()));

    }
}


