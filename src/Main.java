import manager.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;

public class Main {

    public static void main(String[] args) {


        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Эпик для теста истории","Тестим", Status.NEW);
        Epic epic1 = new Epic("Эпик для теста истории2","Тестим1", Status.NEW);


        inMemoryTaskManager.createEpic(epic);
        inMemoryTaskManager.createEpic(epic1);

        System.out.println(inMemoryTaskManager.getAllEpic());
        Subtask subtask = new Subtask("","",Status.DONE, epic1.getId());

        inMemoryTaskManager.createSubtask(subtask, epic1.getId());

        //System.out.println(inMemoryTaskManager.getEpicById(epic.getId()).toString());
        System.out.println(inMemoryTaskManager.getEpicById(epic1.getId()).toString());
        //System.out.println(inMemoryTaskManager.getEpicById(epic1.getId()).toString());
        System.out.println(inMemoryTaskManager.getSubtaskById(subtask.getId()));

        System.out.println("История: " + inMemoryTaskManager.getHistory());

    }
}
