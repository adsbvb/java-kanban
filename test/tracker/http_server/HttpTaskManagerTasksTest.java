package tracker.http_server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.controllers.InMemoryTaskManager;
import tracker.controllers.TaskManager;
import tracker.model.Status;
import tracker.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class HttpTaskManagerTasksTest {

    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;

    public HttpTaskManagerTasksTest() throws IOException {
        this.manager = new InMemoryTaskManager();
        this.taskServer = new HttpTaskServer(manager);
        this.gson = BaseHttpHandler.getGson();
    }

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        taskServer.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stopServer();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(1, "Test", "Testing task", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test", tasksFromManager.get(0).getName(), "Некорректное имя задачи");

    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task = new Task(1, "Test", "Testing task", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks");

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();

        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> receivedTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertNotNull(receivedTasks);
        assertEquals(task, receivedTasks.get(0));
    }

    @Test
    public void testDeleteAllTasks() throws IOException, InterruptedException {
        Task task = new Task(1, "Test", "Testing task", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI deleteUrl = URI.create("http://localhost:8080/tasks");

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(deleteUrl)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> remainingTasks = manager.getTasks();

        assertTrue(remainingTasks.isEmpty());
    }

}