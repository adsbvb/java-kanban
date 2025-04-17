package tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Epic extends Task {

    private ArrayList<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, Epic epic) {
        super(id, epic);
    }

    public Epic(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public void updateStatus() {
        ArrayList<Subtask> subtasksList = getSubtasks();
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
        setStatus(status);
    }

    private void setStatus(Status status) {
        this.status = status;
    }

    public void setStartTime() {
        List<Subtask> subtaskListByDateTime = getSubtasks().stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .sorted(Comparator.comparing(Subtask::getStartTime))
                .toList();
        if (!subtaskListByDateTime.isEmpty()) {
            this.startTime = subtaskListByDateTime.getFirst().getStartTime();
            this.endTime = subtaskListByDateTime.getLast().getEndTime();
            this.duration = subtaskListByDateTime.stream()
                    .map(Subtask::getDuration)
                    .reduce(Duration.ZERO, Duration::plus);
        } else {
            this.startTime = null;
            this.endTime = null;
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}

