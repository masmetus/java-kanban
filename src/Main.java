import java.util.HashMap;

public class Main {

    public static void main(String[] args) {


        System.out.println(" ");
        TaskManager taskManager = new TaskManager();
        Epic epic = new Epic("Тест1","Ахахахахха Тест1", Status.NEW);
        Epic epic2 = new Epic("Тест2","Ахахахахха Тест2", Status.IN_PROGRESS);

        Subtask subtask = new Subtask("Таска","Пакса", Status.DONE);

       taskManager.createEpic(epic);
       taskManager.createEpic(epic2);

        taskManager.createSubtask(subtask);

        System.out.println(taskManager.getAllEpics().toString());
        System.out.println(taskManager.getAllSubtasks().toString());
        System.out.println(" ");
        System.out.println("---".repeat(20));
        System.out.println(" ");

        System.out.println(taskManager.getEpicById(2) + " Это ваш эпик");
        System.out.println("Я только что удалил его!");
        taskManager.deleteEpicById(2);

        System.out.println(" ");
        System.out.println("---".repeat(20));
        System.out.println(" ");
        System.out.println("Видишь, его тут нет: " + taskManager.getAllEpics().toString());


        System.out.println(" ");
        System.out.println("---".repeat(20));
        System.out.println(" ");
        Epic epic3 = new Epic("Тест3","Ахахахахха Тест3", Status.IN_PROGRESS);
        taskManager.createEpic(epic3);
        System.out.println("И создал новый. Теперь в списке другой эпик: " + taskManager.getAllEpics().toString());


    }
}
