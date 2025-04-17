import java.util.HashMap;

public class Main {

    public static void main(String[] args) {


        TaskManager taskManager = new TaskManager();
        Epic epic = new Epic("Жопа","Ахахахахха жопа");

       taskManager.createEpic(epic);

       System.out.println(taskManager.getAllEpic().toString());



        System.out.println("Поехали!");
    }
}
