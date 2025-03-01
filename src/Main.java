import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.Task;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Status;
import tracker.controllers.InMemoryTaskManager;

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

        /* System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks()); */

        taskManager.updateTask(2, new Task("Уборка.", "Пропылесосить ковер", Status.IN_PROGRESS));
        taskManager.updateEpic(3, new Epic("Путевка", "Спланировать отпуск", Status.DONE));
        taskManager.updateSubtask(5, new Subtask("Принадлежности для отдыха", "Купить походный инвентарь.", Status.DONE));

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        taskManager.getTask(1);
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getSubtask(4);
        taskManager.getTask(1);
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getSubtask(4);
        taskManager.getTask(1);
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getSubtask(4);

        System.out.println("История просмотров задач: " + taskManager.getHistory());

    }
}
