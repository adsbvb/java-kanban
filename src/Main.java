public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        int task1 = taskManager.createTask(new Task("Покупка.", "Купить продукты.", Status.NEW));
        int task2 = taskManager.createTask(new Task("Уборка.", "Протереть пыль.", Status.NEW));

        int epic1 = taskManager.createEpic(new Epic("Путевка.", "Выбрать маршрут.", Status.NEW));
        int subtask1_1 = taskManager.addSubtask(new Subtask("Билеты.", "Купить.", Status.NEW));
        int subtask1_2 = taskManager.addSubtask(new Subtask("Личные вещи.", "Собрать чемодан.", Status.NEW));

        int epic2 = taskManager.createEpic(new Epic("Изучить новую специальность.", "Освоить язык программирования Java.", Status.NEW));
        int subtask2_1 = taskManager.addSubtask(new Subtask("Учеба.", "Пройти курс на Яндекс-Практикум.", Status.NEW));

        System.out.println(taskManager.getTasks());

        taskManager.updateTask(2, new Task("Уборка.", "Пропылесосить ковер", Status.IN_PROGRESS));
        taskManager.updateTask(3, new Epic("Путевка", "Спланировать отпуск", Status.IN_PROGRESS));
        taskManager.updateTask(5, new Subtask("Принадлежности для отдыха", "Купить походные инвентарь.", Status.DONE));

        System.out.println(taskManager.getTasks());

        taskManager.removeByID(2);
        taskManager.removeByID(3);

        System.out.println(taskManager.getTasks());



    }
}
