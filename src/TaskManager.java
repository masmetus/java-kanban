import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int idCounter = 1;


    public HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    public HashMap<Integer, Epic> epicList = new HashMap<>();
    public HashMap<Integer, Task> taskList = new HashMap<>();


    ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicList.values());
    }

    ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskList.values());
    }

    ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskList.values());
    }

    public void deleteAllEpics(Epic epic) {
        epicList.clear();
    }

    public void deleteAllTasks() {
        taskList.clear();
    }

    public void deleteAllSubtasks(Subtask subtask) {
        subtaskList.clear();
    }

    public Epic getEpicById(int id) {
        return epicList.get(id);
    }

    public void getTaskById(int id) {

    }

    public void getSubtaskById(int id) {

    }

    public void createEpic(Epic epic) {
        epic.setId(idCounter);
        epicList.put(epic.setId(idCounter), epic);
        idCounter++;
    }

    public void createTask(Task task) {
        task.setId(idCounter);
        taskList.put(task.setId(idCounter), task);
        idCounter++;
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(idCounter);
        subtaskList.put(subtask.setId(idCounter), subtask);
        idCounter++;
    }

    public void updateEpicById(int id, Epic epic) {
        if (epicList.containsKey(id)) {
            epicList.put(epic.getId(), epic);
        } else {
            System.out.println("Эпик не сущесвует или у Вас нет к нему доступа.");
        }
    }

    public void updateTaskById(int id, Task task) {
        if (taskList.containsKey(id)) {
            taskList.put(task.getId(), task);
        } else {
            System.out.println("Задача не сущесвует или у Вас нет к ней доступа.");
        }
    }

    public void updateSubtaskById(int id, Subtask subtask) {
        if (subtaskList.containsKey(id)) {
            subtaskList.put(subtask.getId(), subtask);
        } else {
            System.out.println("Задача не сущесвует или у Вас нет к ней доступа.");
        }
    }

    public void deleteEpicById (int id) {
        if (epicList.containsKey(id)){
            epicList.remove(id);
        } else {
            System.out.println("Такого эпика нет.");
        }
    }


    public void getAllTaskOfEpic() {

    }

}
