package tracker.http_server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.controllers.TaskManager;
import tracker.model.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetPrioritized(exchange);
                break;
            default:
                sendText(exchange, "Метод не предусмотрен.", 405);
                break;
        }
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        List<Task> prioritizedList = taskManager.getPrioritizedTasks();
        if (prioritizedList.isEmpty()) {
            sendNotFound(exchange);
        } else {
            String jsonResponse = gson.toJson(prioritizedList);
            sendText(exchange, jsonResponse, 200);
        }
    }
}
