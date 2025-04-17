import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    int idCounter = 1;


    public HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    public HashMap<Integer, Epic> epicList = new HashMap<>();
    public HashMap<Integer, Task> taskList = new HashMap<>();



    ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epicList.values());
    }

    public void getAllTask(Task task){
        System.out.println(taskList.toString());
    }

    public void getAllSubtask(Subtask subtask){
        System.out.println(subtaskList.toString());
    }

    public void deleteAllEpic(Epic epic){
        epicList.clear();
    }

    public void deleteAllTask(){
        taskList.clear();
    }

    public void deleteAllSubtask(Subtask subtask){
       subtaskList.clear();
    }

    public void getEpicById(Long id) {

    }

    public void getTaskById(Long id) {

    }

    public void getSubtaskById(Long id) {

    }

    public void createEpic(Epic epic) {
        epicList.put(epic.setId(idCounter), epic);
        idCounter++;
    }

    public void createTask(Task task) {
        taskList.put(task.setId(idCounter), task);
        idCounter++;
    }

    public void createSubtask(Subtask subtask) {
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


    public void getAllTaskOfEpic() {

    }

}
