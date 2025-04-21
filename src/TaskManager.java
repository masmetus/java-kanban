import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    int idCounter = 1;


    private final Map<Integer, Subtask> subtaskList = new HashMap<>();
    private final Map<Integer, Epic> epicList = new HashMap<>();
    private final Map<Integer, Task> taskList = new HashMap<>();


    List<Epic> getAllEpic() {
        return new ArrayList<>(epicList.values());
    }

    List<Task> getAllTask() {
        return new ArrayList<>(taskList.values());
    }

    List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskList.values());
    }

    public void removeAllEpic(Epic epic) {
        epicList.clear();
    }

    public void removeAllTask() {
        taskList.clear();
    }

    public void removeAllSubtask() {
        for (Epic epic : epicList.values()) {
            epic.removeAllSubtaskIds();
        }
        subtaskList.clear();

    }

    public void removeSubtaskById(int id) {
        Subtask subtask = subtaskList.get(id);
        if (subtask == null) {
            System.out.println("Подзадачи с идентификатором " + id + " нет!");
            return;
        }

        //Удаляем связь этой подзадачи и эпика
        //Дальше по эпику. Если у него больше не осталось подзадач, то мы его в NEW
        //Если остались, то ничего

        int epicId = subtask.getEpicId();
        Epic epic = epicList.get(epicId);
        //Так как подзадача не существует без эпика, то останавливаем. И молимся, чтоб такую создать было нельзя
        if (epic == null) {
            System.out.println("Нет эпика для этой подзадачи. Ахтунг!");
            return;
        }
        //Без явного указания эта падла считает, что я удаляю по индексу
        epic.getSubtaskIds().remove(Integer.valueOf(id));
        subtaskList.remove(id);

        //TODO: Самое душное - статус эпика
        updateEpicStatus(epic);
    }

    public void removeTaskById(int id) {
        if (!taskList.containsKey(id)) {
            System.out.println("Задачи с идентификатором " + id + " нет!");
            return;
        }
        taskList.remove(id);
    }

    public void removeEpicById(int id) {
        if (!epicList.containsKey(id)) {
            System.out.println("Эпик с идентификатором: " + id + " не существует!");
            return;
        }

        Epic epic = epicList.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtaskList.remove(subtaskId);
            }
        }
        epicList.remove(id);
    }

    public Epic getEpicById(int id) {
        if (!epicList.containsKey(id)) {
            System.out.println("Эпика с идентификатором " + id + " нет!");
        }
        return epicList.get(id);
    }

    public Task getTaskById(int id) {
        if (!taskList.containsKey(id)) {
            System.out.println("Задачи с идентификатором " + id + " нет!");
        }
        return taskList.get(id);
    }

    public Subtask getSubtaskById(int id) {
        if (!subtaskList.containsKey(id)) {
            System.out.println("Подзадачи с идентификатором " + id + " нет!");
        }
        return subtaskList.get(id);
    }

    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epicList.put(epic.getId(), epic);
    }

    public void createTask(Task task) {
        task.setId(idCounter++);
        taskList.put(task.getId(), task);
    }

    public void createSubtask(Subtask subtask, int epicId) {
        if (!epicList.containsKey(epicId)) {
            System.out.println("Ошибка: эпик с ID " + epicId + " не найден!");
            return;
        }

        if (subtask.getEpicId() != epicId) {
            System.out.println("Ошибка при связывании подзадачи с эпиком!");
            return;
        }

        subtask.setId(idCounter++);
        subtaskList.put(subtask.getId(), subtask);
        Epic epic = epicList.get(epicId);
        epic.addSubtaskId(subtask.getId());
    }

    public void updateEpic(Epic epic) {
        if (epicList.containsKey(epic.getId())) {
            epicList.put(epic.getId(), epic);
        } else {
            System.out.println("Эпик не существует или у Вас нет к нему доступа.");
        }
    }

    public void updateTask(Task task) {
        if (taskList.containsKey(task.getId())) {
            taskList.put(task.getId(), task);
        } else {
            System.out.println("Задача не существует или у Вас нет к ней доступа.");
            return;
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtaskList.containsKey(subtask.getId())) {
            subtaskList.put(subtask.getId(), subtask);
            Epic epic = epicList.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            }
        } else {
            System.out.println("Задача не существует или у Вас нет к ней доступа.");
            return;
        }
    }


    public List<Subtask> getAllSubtaskOfEpic(int epicId) {
        Epic epic = epicList.get(epicId);
        if (!epicList.containsKey(epicId)) {
            System.out.println("Эпика с идентификатором " + epicId + " не существует!");
            return null;
        }
        if (epic == null) {
            System.out.println("В эпике нет подзадач!");
            return null;
        }
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtaskList.get(subtaskId);
            if (subtask != null) {
                subtasksOfEpic.add(subtask);
            }
        }

        return subtasksOfEpic;
    }

    public void updateEpicStatus(Epic epic) {
        if (epic == null) {
            System.out.println("Нечего обновлять");
            return;
        }

        if (epic.getSubtaskIds().isEmpty()) {
            //Если у эпика нет подзадач... то статус должен быть NEW
            //Была подзадача в статусе Done, как и эпик. Я её удалил (пункт f ТЗ). Статус NEW, подзадач то нет
            epic.setStatus(Status.NEW);
        }

        boolean allDone = false;
        boolean allNew = false;

        //Если у эпика есть подзадачи, то смотрим на их статус

    }

}
