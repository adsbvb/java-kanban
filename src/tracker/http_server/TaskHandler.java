package tracker.http_server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.controllers.TaskManager;
import tracker.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            System.out.println("Получен запрос: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGetTasks(exchange);
                    break;
                case "POST":
                    handlePostTasks(exchange);
                    break;
                case "DELETE":
                    handleDeleteTasks(exchange);
                    break;
                default:
                    sendText(exchange, "Метод не предусмотрен.", 405);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "Internal Server Error: " + e.getMessage(), 500);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        if (splitPath.length == 3 && splitPath[2].matches("\\d+")) {
            int idTask = Integer.parseInt(splitPath[2]);
            Optional<Task> taskOptional = taskManager.getTask(idTask);
            if (taskOptional.isPresent()) {
                Task task = taskOptional.get();
                String jsonResponse = gson.toJson(task);
                sendText(exchange, jsonResponse, 200);
            } else {
                sendNotFound(exchange);
            }
        } else {
            List<Task> tasks = taskManager.getTasks();
            String jsonResponse = gson.toJson(tasks);
            sendText(exchange, jsonResponse, 200);
        }
    }

    private void handlePostTasks(HttpExchange exchange) throws IOException {
        try {
            InputStream bodyInputStream = exchange.getRequestBody();
            String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, Task.class);
            if (task == null) {
                sendText(exchange, "Некорректные данные задачи.", 400);
                return;
            }
            if (taskManager.taskOverlapsInTime(task)) {
                sendHasInteractions(exchange);
                return;
            }
            if (taskManager.getTask(task.getId()).isPresent()) {
                taskManager.updateTask(task);
                sendText(exchange, "Задача обновлена", 201);
            } else {
                taskManager.createTask(task);
                sendText(exchange, "Задача добавлена", 201);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "Ошибка при обработке задачи: " + e.getMessage(), 400);
        }
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteTasks();
        sendText(exchange, "Все задачи удалены.", 200);
    }
}
