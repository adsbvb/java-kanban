package tracker.controllers;

import org.junit.jupiter.api.*;
import tracker.model.Status;
import tracker.model.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager history;

    @BeforeEach
    void setUp() {
        history = new InMemoryHistoryManager();
    }

    @Test
    void testAddToHistory() {
        Task task1 = new Task(1, "name1" , "description1", Status.NEW);
        Task task2 = new Task(2, "name2" , "description2", Status.NEW);
        history.add(task1);
        history.add(task2);
        assertEquals(task1, history.getHistory().getFirst(), "Задача №1 не была записана в историю задач!");
        assertEquals(task2, history.getHistory().getLast(), "Задача №2 не была записана в историю задач!");
    }

    @Test
    void checkReentryTaskInHistory() {
        Task task = new Task("Name", "Description");
        history.add(task);
        history.add(task);
        assertEquals(1, history.getHistory().size(), "Задача была записана дважды в историю задач!");
    }

    @Test
    void testReturnEmptyListHistory() {
        assertTrue(history.getHistory().isEmpty(), "История задач не пуста!");
    }

    @Test
    void testRemoveFromHistoryStart() {
        Task task1 = new Task(1, "Name", "Description", Status.NEW);
        Task task2 = new Task(2, "Name", "Description", Status.NEW);
        history.add(task1);
        history.add(task2);
        history.remove(task1.getId());
        assertFalse(history.getHistory().contains(task1), "Задача не была удалена с начала списка истории задач!");
    }

    @Test
    void testRemoveFromHistoryMiddle() {
        Task task1 = new Task(1, "Name", "Description", Status.NEW);
        Task task2 = new Task(2, "Name", "Description", Status.NEW);
        Task task3 = new Task(3, "Name", "Description", Status.NEW);
        history.add(task1);
        history.add(task2);
        history.add(task3);
        history.remove(task2.getId());
        assertFalse(history.getHistory().contains(task2), "Задача не была удалена из середины списка истории задач!");
    }

    @Test
    void testRemoveFromHistoryEnd() {
        Task task1 = new Task(1, "Name", "Description", Status.NEW);
        Task task2 = new Task(2, "Name", "Description", Status.NEW);
        history.add(task1);
        history.add(task2);
        history.remove(task2.getId());
        assertFalse(history.getHistory().contains(task2), "Задача не была удалена в конце списка истории задач!");
    }
}