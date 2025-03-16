package tracker.model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task{

    private ArrayList<Subtask> subtasks = new ArrayList<>();;

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

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

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

   @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }*/

}

