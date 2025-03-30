import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.Task;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Status;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        int task1 = taskManager.createTask(new Task("Покупка.", "Купить продукты.", Status.NEW));
        int task2 = taskManager.createTask(new Task("Уборка.", "Протереть пыль.", Status.NEW));

        int epic1 = taskManager.createEpic(new Epic("Путевка.", "Выбрать маршрут.", Status.NEW));
        int subtask1_1 = taskManager.addSubtask(new Subtask("Билеты.", "Купить.", Status.NEW, 3));
        int subtask1_2 = taskManager.addSubtask(new Subtask("Личные вещи.", "Собрать чемодан.", Status.NEW, 3));

        int epic2 = taskManager.createEpic(new Epic("Изучить новую специальность.", "Освоить язык программирования Java.", Status.NEW));
        int subtask2_1 = taskManager.addSubtask(new Subtask("Учеба.", "Пройти курс на Яндекс-Практикум.", Status.NEW, 6));

    }
}
