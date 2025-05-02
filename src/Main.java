import tracker.controllers.InMemoryTaskManager;
import tracker.controllers.Managers;

import tracker.controllers.TaskManager;
import tracker.http_server.HttpTaskServer;
import tracker.model.Status;
import tracker.model.Subtask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {

       try {
            HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
            httpTaskServer.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*TaskManager manager = new InMemoryTaskManager();
        Subtask subtask = new Subtask(1, "Name", "D", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10), 1);
        System.out.println();
        if (manager.getEpic(subtask.getEpicId()).isEmpty()) {
            System.out.println("pust");
        } else {
            System.out.println("error");
        }*/

    }
}
