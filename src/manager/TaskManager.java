package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    //Методы задачи
    void createTask(Task task);

    List<Task> getAllTask();

    Task getTaskById(int id);

    void removeAllTask();

    void removeTaskById(int id);

    void updateTask(Task updatedTask);

    //Методы эпика
    void createEpic(Epic epic);

    List<Epic> getAllEpic();

    Epic getEpicById(int id);

    void removeAllEpic();

    void removeEpicById(int id);

    void updateEpic(Epic updatedEpic);

    //Методы сабтаски
    void createSubtask(Subtask subtask, int epicId);

    List<Subtask> getAllSubtask();

    void removeAllSubtask();

    void removeSubtaskById(Integer id);

    Subtask getSubtaskById(int id);

    void updateSubtask(Subtask updatedSubtask);

    List<Subtask> getAllSubtaskOfEpic(int epicId);

    List<Task> getHistory();

}
