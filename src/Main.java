import tracker.controllers.Managers;

import tracker.http_server.HttpTaskServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

       try {
            HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
            httpTaskServer.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
