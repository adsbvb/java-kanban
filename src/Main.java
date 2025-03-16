import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Status;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        /*int task1 = taskManager.createTask(new Task(1, "Покупка.", "Купить продукты.", Status.NEW));
        int task2 = taskManager.createTask(new Task(2,"Уборка.", "Протереть пыль.", Status.NEW));

        int epic1 = taskManager.createEpic(new Epic(3,"Путевка.", "Выбрать маршрут.", Status.NEW));
        int subtask1_1 = taskManager.addSubtask(new Subtask(4,"Билеты.", "Купить.", Status.NEW, 3));
        int subtask1_2 = taskManager.addSubtask(new Subtask(5,"Личные вещи.", "Собрать чемодан.", Status.NEW, 3));

        int epic2 = taskManager.createEpic(new Epic(6,"Изучить новую специальность.", "Освоить язык программирования Java.", Status.NEW));
        int subtask2_1 = taskManager.addSubtask(new Subtask(7,"Учеба.", "Пройти курс на Яндекс-Практикум.", Status.NEW, 6));

        taskManager.updateTask(2, new Task("Уборка.", "Пропылесосить ковер.", Status.IN_PROGRESS));
        taskManager.updateEpic(3, new Epic("Путевка.", "Спланировать отпуск.", Status.DONE));
        taskManager.updateSubtask(5, new Subtask("Принадлежности для отдыха.", "Купить походный инвентарь.", Status.DONE, 3));

        System.out.println(taskManager.getTasks());
        System.out.println();
        System.out.println(taskManager.getEpics());
        System.out.println();
        System.out.println(taskManager.getSubtasks());
        System.out.println();*/

        taskManager.createEpic(new Epic(1, "Путевка.", "Выбрать маршрут.", Status.NEW));
        taskManager.addSubtask(new Subtask(2,"Билеты.", "Купить.", Status.NEW, 1));
        taskManager.addSubtask(new Subtask(3,"Личные вещи.", "Собрать чемодан.", Status.NEW, 1));
        taskManager.addSubtask(new Subtask(4,"Такси.", "вызвать до аэропорта.", Status.NEW, 1));
        taskManager.createEpic(new Epic(5,"Изучить новую специальность.", "Освоить язык программирования Java.", Status.NEW));

        taskManager.getEpic(1);
        System.out.println("История просмотров задач: " + taskManager.getHistory());
        taskManager.getEpic(5);
        System.out.println("История просмотров задач: " + taskManager.getHistory());
        taskManager.getSubtask(3);
        System.out.println("История просмотров задач: " + taskManager.getHistory());
        taskManager.getSubtask(4);
        System.out.println("История просмотров задач: " + taskManager.getHistory());
        taskManager.getEpic(5);
        System.out.println("История просмотров задач: " + taskManager.getHistory());
        taskManager.getSubtask(2);
        System.out.println("История просмотров задач: " + taskManager.getHistory());
        taskManager.getSubtask(3);
        System.out.println("История просмотров задач: " + taskManager.getHistory());
        System.out.println();

        taskManager.remove(2);
        System.out.println("История просмотров задач: " + taskManager.getHistory());
        System.out.println();

        taskManager.deleteEpicById(1);
        System.out.println("История просмотров задач: " + taskManager.getHistory());

    }
}
