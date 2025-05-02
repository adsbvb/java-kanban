package tracker.http_server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.controllers.TaskManager;
import tracker.model.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            System.out.println("Получен запрос: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGetEpics(exchange);
                    break;
                case "POST":
                    handlePostEpics(exchange);
                    break;
                case "DELETE":
                    handleDeleteEpics(exchange);
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

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] splitPath = path.split("/");
        if (splitPath.length == 3 && splitPath[2].matches("\\d+")) {
            int idEpic = Integer.parseInt(splitPath[2]);
            Optional<Epic> epicOptional = taskManager.getEpic(idEpic);
            if (epicOptional.isPresent()) {
                Epic epic = epicOptional.get();
                String jsonResponse = gson.toJson(epic);
                sendText(exchange, jsonResponse, 200);
            } else {
                sendNotFound(exchange);
            }
        } else {
            List<Epic> epics = taskManager.getEpics();
            String jsonResponse = gson.toJson(epics);
            sendText(exchange, jsonResponse, 200);
        }
    }

    private void handlePostEpics(HttpExchange exchange) throws IOException {
        try {
            InputStream bodyInputStream = exchange.getRequestBody();
            String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(body, Epic.class);
            if (epic == null) {
                sendText(exchange, "Некорректные данные задачи.", 400);
                return;
            }
            if (taskManager.getEpic(epic.getId()).isPresent()) {
                taskManager.updateEpic(epic);
                sendText(exchange, "Эпик обновлен.", 200);
            } else {
                taskManager.createEpic(epic);
                sendText(exchange, "Эпик добавлен.", 200);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(exchange, "Ошибка при обработке задачи: " + e.getMessage(), 400);
        }
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        if (taskManager.getTasks().isEmpty()) {
            sendNotFound(exchange);
        } else {
            taskManager.deleteEpics();
            sendText(exchange, "Все эпики удалены.", 200);
        }
    }
}
