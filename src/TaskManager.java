import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public class TaskManager {
    private HashMap<Integer, Task> allTasks = new HashMap<>();
    private HashMap<Integer, ArrayList> epicSubtaskIDs = new HashMap<>();
    private int count;
    private int epicID;

    public int setNewId() {
        count++;
        return count;
    }

    public int createTask(Task task) {
        int newId = setNewId();
        task.setID(newId);
        allTasks.put(newId, task);
        return newId;
    }

    public int createEpic(Epic epic) {
        int newId = setNewId();
        epic.setID(newId);
        epicID = newId;
        epicSubtaskIDs.put(newId, new ArrayList<>());
        allTasks.put(newId, epic);
        return newId;
    }

    public int addSubtask(Subtask subtask) {
        int newId = setNewId();
        subtask.setID(newId);
        ArrayList list = epicSubtaskIDs.get(epicID);
        list.add(newId);
        epicSubtaskIDs.put(epicID, list);
        allTasks.put(newId, subtask);
        return newId;
    }

    public Collection<Task> getTasks() {
        return allTasks.values();
    }

    public void clearAll() {
        allTasks.clear();
    }

    public Task getTaskByID(int id) {
        return allTasks.get(id);
    }

    public void updateTask(int id, Task task) {
        task.setID(id);
        allTasks.put(id, task);
        setEpicStatus(id);
        updateStatusEpicAfterSubtask (id);
    }

    public void removeByID(int id) {
        for (int identificator : epicSubtaskIDs.keySet()) {
            if (identificator == id) {
                ArrayList idListOfSubtasks = epicSubtaskIDs.get(id);
                for (Object element : idListOfSubtasks) {
                    allTasks.remove(element);
                }
            }
        }
        allTasks.remove(id);
    }

    public ArrayList getAllSubtasksOfEpic(int id) {
        ArrayList listOfSubtasks = new ArrayList<>();
        ArrayList idListSubtasks = epicSubtaskIDs.get(id);
        for(Object identificator : idListSubtasks) {
            listOfSubtasks.add(allTasks.get(identificator));
        }
        return listOfSubtasks;
    }

    public void updateStatusEpicAfterSubtask (int id) {
        for (Integer epicID : epicSubtaskIDs.keySet()) {
            ArrayList listSubtasksID = epicSubtaskIDs.get(epicID);
            for (Object element : listSubtasksID) {
                if (element.equals(id)) {
                    setEpicStatus(epicID);
                }
            }
        }
    }

    public void setEpicStatus(int id) {
        Status status = Status.IN_PROGRESS;
        int countNew = 0;
        int countDone = 0;
        ArrayList idListSubtasks = epicSubtaskIDs.get(id);
        if (idListSubtasks == null) {
            return;
        }
        if (idListSubtasks.size() == 0) {
            status = Status.NEW;
            return;
        }
        int countSubtasks = idListSubtasks.size();
        for(Object identificator : idListSubtasks) {
            Task subtask = allTasks.get(identificator);
            if (subtask.status.equals(Status.IN_PROGRESS)) {
                break;
            }
            if (subtask.status.equals(Status.DONE)) {
                countDone++;
            }
            if (subtask.status.equals(Status.NEW)) {
                countNew++;
            }
            if (countSubtasks == countDone) {
                status = Status.DONE;
            }
            if (countSubtasks == countNew) {
                status = Status.NEW;
            }
        }
        Task newEpic = allTasks.get(id);
        newEpic.setStatus(status);
        allTasks.put(id, newEpic);
    }
}

