package tracker.http_server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.controllers.TaskManager;
import tracker.model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            System.out.println("Получен запрос: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetSubtasks(exchange);
                break;
            case "POST":
                handlePostSubtasks(exchange);
                break;
            case "DELETE":
                handleDeleteSubtasks(exchange);
                break;
            default:
                sendNotFound(exchange);
                break;
        }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "Internal Server Error: " + e.getMessage(), 500);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        if (splitPath.length == 3 && splitPath[2].matches("\\d+")) {
            int idSubtask = Integer.parseInt(splitPath[2]);
            Optional<Subtask> subtaskOptional = taskManager.getSubtask(idSubtask);
            if (subtaskOptional.isPresent()) {
                Subtask subtask = subtaskOptional.get();
                String jsonResponse = gson.toJson(subtask);
                sendText(exchange, jsonResponse, 200);
            } else {
                sendNotFound(exchange);
            }
        } else {
            List<Subtask> subtasks = taskManager.getSubtasks();
            String jsonResponse = gson.toJson(subtasks);
            sendText(exchange, jsonResponse, 200);
        }
    }

    private void handlePostSubtasks(HttpExchange exchange) throws IOException {
        try {
            InputStream bodyInputStream = exchange.getRequestBody();
            String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (subtask == null) {
                sendHasInteractions(exchange, "Некорректные данные задачи.");
                return;
            }
            if (taskManager.taskOverlapsInTime(subtask)) {
                sendHasInteractions(exchange, "Созданная задача пересекается с существующей задачей!");
                return;
            }
            if (taskManager.getSubtask(subtask.getId()).isPresent()) {
                taskManager.updateSubtask(subtask);
                sendText(exchange, "Подзадача обновлена!", 201);
            } else {
                taskManager.createSubtask(subtask);
                sendText(exchange, "Подзадача добавлена!", 201);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendHasInteractions(exchange, "Ошибка при обработке задачи: " + e.getMessage());
        }
    }

    private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteSubtasks();
        sendText(exchange, "Все подзадачи удалены.", 200);
    }
}


