package tracker.controllers;

import tracker.exceptions.ManagerSaveException;
import tracker.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,startTime,duration,epic");
            writer.newLine();
            for (Task task : getTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл!", e);
        }
    }

    private String toString(Task task) {
        if (task.getType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,%tY-%tm-%td %tH:%tM,%d,%d",
                    subtask.getId(),
                    TaskType.SUBTASK,
                    subtask.getName(),
                    subtask.getStatus(),
                    subtask.getDescription(),
                    subtask.getStartTime(),
                    subtask.getStartTime(),
                    subtask.getStartTime(),
                    subtask.getStartTime(),
                    subtask.getStartTime(),
                    subtask.getDuration().toMinutes(),
                    subtask.getEpicId());
        } else if (task.getType() == TaskType.EPIC) {
            Epic epic = (Epic) task;
            return String.format("%d,%s,%s,%s,%s,%tY-%tm-%td %tH:%tM,%d",
                    epic.getId(),
                    TaskType.EPIC,
                    epic.getName(),
                    epic.getStatus(),
                    epic.getDescription(),
                    epic.getStartTime(),
                    epic.getStartTime(),
                    epic.getStartTime(),
                    epic.getStartTime(),
                    epic.getStartTime(),
                    epic.getDuration().toMinutes());
        } else {
            return String.format("%d,%s,%s,%s,%s,%tY-%tm-%td %tH:%tM,%d",
                    task.getId(),
                    TaskType.TASK,
                    task.getName(),
                    task.getStatus(),
                    task.getDescription(),
                    task.getStartTime(),
                    task.getStartTime(),
                    task.getStartTime(),
                    task.getStartTime(),
                    task.getStartTime(),
                    task.getDuration().toMinutes());
        }
    }

    private Task fromString(String value) {
        String[] docket = value.split(",");
        int id = Integer.parseInt(docket[0]);
        TaskType type = TaskType.valueOf(docket[1]);
        String name = docket[2];
        Status status = Status.valueOf(docket[3]);
        String description = docket[4];
        LocalDateTime startTime = docket[5].equals("null-null-null null:null") ? null :
                LocalDateTime.parse(docket[5], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        Duration duration = Duration.ofMinutes(Long.parseLong(docket[6]));
        int epicId = docket.length > 7 ? Integer.parseInt(docket[7]) : 0;
        Task task;
        if (type.equals(TaskType.SUBTASK)) {
            task = new Subtask(id, name, description, status, startTime, duration, epicId);
        } else if (type.equals(TaskType.EPIC)) {
            task = new Epic(id, name, description, status, startTime, duration);
        } else {
            task = new Task(id, name, description, status, startTime, duration);
        }
        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines.subList(1, lines.size())) {
                Task task = manager.fromString(line);
                if (task.getType() == TaskType.SUBTASK) {
                    manager.addTask(task);
                } else if (task.getType() == TaskType.EPIC) {
                    manager.addTask(task);
                } else {
                    manager.addTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла!", e);
        }
        return manager;
    }

    @Override
    protected void addTask(Task task) {
        super.addTask(task);
    }

    @Override
    public int createTask(Task task) {
        int idTask = super.createTask(task);
        save();
        return idTask;
    }

    @Override
    public int createEpic(Epic epic) {
        int idEpic = super.createEpic(epic);
        save();
        return idEpic;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int idSubtask = super.createSubtask(subtask);
        save();
        return idSubtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }
}
