package tracker.http_server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.controllers.TaskManager;
import tracker.model.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetHistory(exchange);
                break;
            default:
                sendNotFound(exchange);
                break;
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> historyList = taskManager.getHistory();
        if (historyList.isEmpty()) {
            sendNotFound(exchange);
        } else {
            String jsonResponse = gson.toJson(historyList);
            sendText(exchange, jsonResponse, 200);
        }
    }
}
