import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;

public class Main {

    public static void main(String[] args) {


        TaskManager taskManager = new TaskManager();
        //model.Epic epic = new model.Epic("Эпик для проверки обновления статусов у подзадачи", "Тест тестыч", model.Status.NEW);
        Epic epic1 = new Epic("Эпик для проверки обновления самого эпика", "Тест тестыч", Status.NEW);
        Epic epic2 = new Epic("А я уже обновил свои поля", "И описание!", Status.NEW);

        //taskManager.createEpic(epic);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        Subtask subtask = new Subtask("Проверка создания таски","Я созданная таска",Status.NEW, epic1.getId());
        Subtask subtask1 = new Subtask("Проверка обновления таски","Я обновленная таска",Status.NEW, epic1.getId());


        taskManager.createSubtask(subtask, epic1.getId());
        taskManager.createSubtask(subtask1, epic1.getId());
        System.out.println("До обновления все эпики " + taskManager.getAllEpic().toString());
        System.out.println("До обновления все подзадачи " + taskManager.getAllSubtask().toString());

        taskManager.removeAllEpic();
        System.out.println("До обновления все эпики " + taskManager.getAllEpic().toString());
        System.out.println("До обновления все подзадачи " + taskManager.getAllSubtask().toString());

        /*System.out.println("До обновления все эпики " + taskManager.getAllEpic().toString());
        System.out.println("До обновления нужный эпики " + taskManager.getEpicById(epic1.getId()));
        System.out.println("До обновления все подзадачи " + taskManager.getAllSubtask().toString());

        taskManager.updateEpic(epic2);
        //taskManager.removeEpicById(epic1.getId());
        //taskManager.removeSubtaskById(subtask.getId());
        //taskManager.removeSubtaskById(subtask1.getId());

        //System.out.println("После обновления нужный эпики " + taskManager.getEpicById(epic1.getId()));
        //System.out.println("После обновления все подзадачи " + taskManager.getAllSubtask().toString());


        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask);

        System.out.println("До обновления нужный эпики " + taskManager.getEpicById(epic1.getId()));
        System.out.println("До обновления все подзадачи " + taskManager.getAllSubtask().toString());

        Subtask subtask2 = new Subtask("Я новый смешарик","лосяш!",Status.NEW, epic1.getId());


        taskManager.createSubtask(subtask2, epic1.getId());

        System.out.println("До обновления нужный эпики " + taskManager.getEpicById(epic1.getId()));
        System.out.println("До обновления все подзадачи " + taskManager.getAllSubtask().toString());

        /*System.out.println("После обновления все эпики " + taskManager.getAllEpic().toString());
        System.out.println("После обновления все сабтаски " + taskManager.getAllSubtask().toString());*/

    }
}
