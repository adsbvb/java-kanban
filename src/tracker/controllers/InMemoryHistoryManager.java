package tracker.controllers;

import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node<T> {
        Task task;
        Node<T> prev;
        Node<T> next;

        public Node(Task task) {
            this.task = task;
        }
    }

    private Node<Task> head;
    private Node<Task> tail;
    private HashMap<Integer, Node<Task>> taskMap = new HashMap<>();

    public void linkLast(Task task) {
        Node<Task> newNode = new Node<>(task);
        if (tail == null) {
            head = tail = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }
        taskMap.put(task.getId(), newNode);
    }

    public void removeNode(Node<Task> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        taskMap.remove(node.task.getId());
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> listTasks = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            listTasks.add(node.task);
            node = node.next;
        }
        return listTasks;
    }

    @Override
    public void add(Task task) {
        Node<Task> node = taskMap.get(task.getId());
        if (node != null) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = taskMap.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

}
