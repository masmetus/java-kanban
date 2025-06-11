import manager.InMemoryTaskManager;
import model.Epic;
import model.Status;

public class Main {

    public static void main(String[] args) {

        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Основной","Основной", Status.NEW);
        inMemoryTaskManager.createEpic(epic);


        Epic epic1 = new Epic("Для теста обновления","Обновление", Status.NEW);
        epic1.setId(epic.getId());

        inMemoryTaskManager.updateEpic(epic1);

        System.out.println(inMemoryTaskManager.getAllEpic());

    }
}
