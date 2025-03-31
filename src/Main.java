import tracker.controllers.FileBackedTaskManager;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.*;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        File file = null;
        try {
            file = File.createTempFile("save_", ".csv");
        } catch (IOException e) {
            System.out.println(e);
        }

        TaskManager taskManager = Managers.getDefault(file);

        int task1 = taskManager.createTask(new Task("Покупка.", Status.NEW, "Купить продукты."));
        int task2 = taskManager.createTask(new Task("Уборка.", Status.NEW, "Протереть пыль."));

        int epic1 = taskManager.createEpic(new Epic("Путевка.", Status.NEW, "Выбрать маршрут."));
        int subtask11 = taskManager.addSubtask(new Subtask("Билеты.", Status.NEW, "Купить.", 3));
        int subtask12 = taskManager.addSubtask(new Subtask("Личные вещи.", Status.NEW, "Собрать чемодан.", 3));

        int epic2 = taskManager.createEpic(new Epic("Изучить новую специальность.", Status.NEW, "Освоить язык программирования Java."));
        int subtask21 = taskManager.addSubtask(new Subtask("Учеба.", Status.NEW, "Пройти курс на Яндекс-Практикум.", 6));

        TaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        System.out.println(fileBackedTaskManager.getTasks());
        System.out.println(fileBackedTaskManager.getEpics());
        System.out.println(fileBackedTaskManager.getSubtasks());
    }
}
