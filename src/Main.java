import java.util.HashMap;

public class Main {

    public static void main(String[] args) {


        TaskManager taskManager = new TaskManager();
        Epic epic = new Epic("Жопа", "Ахахахахха жопа", Status.NEW);
        Epic epic1 = new Epic("Жопа2", "Ахахахахха жопа2", Status.NEW);

        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);

        Subtask subtask = new Subtask("Переезд", "Тудасюда", Status.NEW, epic1.getId());
        //Subtask subtask2 = new Subtask("Переезд2", "Тудасюда2", Status.NEW, 99999);

        taskManager.createSubtask(subtask, epic1.getId());
        //taskManager.createSubtask(subtask2, epic.getId());

        //System.out.println("До удаления все эпики " + taskManager.getAllEpic().toString());
        //System.out.println("До удаления все подзадачи " + taskManager.getAllSubtask().toString());

        //System.out.println("До удаления " + taskManager.getEpicById(2));
        //System.out.println("До удаления " + taskManager.getSubtaskById(3));

        System.out.println("До удаления " + taskManager.getAllSubtaskOfEpic(2));

        //taskManager.removeAllSubtask();
        taskManager.removeSubtaskById(3);


        //taskManager.deleteEpicById(2);

        //System.out.println("До удаления " + taskManager.getAllSubtaskOfEpic(666));

        System.out.println("После удаления все эпики " + taskManager.getAllEpic().toString());
        System.out.println("После удаления все подзадачи " + taskManager.getAllSubtask().toString());

        //System.out.println("После удаления эпика " + taskManager.getEpicById(2));

        //System.out.println(taskManager.getEpicById(255));
    }
}
