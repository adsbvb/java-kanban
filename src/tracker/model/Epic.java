package tracker.model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(int id, String name, Status status, String description) {
        super(id, name, status, description);
    }

    public Epic(String name, Status status, String description) {
        super(name, status, description);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void clearSubtask() {
        subtasks.clear();
    }
}

