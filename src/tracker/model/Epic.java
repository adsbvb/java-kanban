package tracker.model;

import java.util.ArrayList;

public class Epic extends Task{

    protected ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", subtasksIds=" + subtasksIds +
                '}';
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(ArrayList<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    public void clearSubtaskIds() {
        subtasksIds.clear();
    }

}

