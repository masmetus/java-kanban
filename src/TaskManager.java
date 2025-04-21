import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    int idCounter = 1;


    private final Map<Integer, Subtask> subtaskList = new HashMap<>();
    private final Map<Integer, Epic> epicList = new HashMap<>();
    private final Map<Integer, Task> taskList = new HashMap<>();


    //Методы задачи
    public void createTask(Task task) {
        task.setId(idCounter++);
        taskList.put(task.getId(), task);
    }

    List<Task> getAllTask() {
        return new ArrayList<>(taskList.values());
    }

    public Task getTaskById(int id) {
        if (!taskList.containsKey(id)) {
            System.out.println("Задачи с идентификатором " + id + " нет!");
        }
        return taskList.get(id);
    }

    public void removeAllTask() {
        taskList.clear();
    }

    public void removeTaskById(int id) {
        if (!taskList.containsKey(id)) {
            System.out.println("Задачи с идентификатором " + id + " нет!");
            return;
        }
        taskList.remove(id);
    }

    public void updateTask(int taskId, Task updatedTask) {
        if (!taskList.containsKey(taskId)) {
            System.out.println("Задача не существует или у Вас нет к ней доступа.");
            return;
        }

        updatedTask.setId(taskId);
        taskList.put(taskId, updatedTask);
    }

    //Методы эпика
    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epicList.put(epic.getId(), epic);
    }

    List<Epic> getAllEpic() {
        return new ArrayList<>(epicList.values());
    }

    public Epic getEpicById(int id) {
        if (!epicList.containsKey(id)) {
            System.out.println("Эпика с идентификатором " + id + " нет!");
        }
        return epicList.get(id);
    }

    public void removeAllEpic() {
        epicList.clear();
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

    public void updateEpic(int epicId, Epic updatedEpic) {
        if (!epicList.containsKey(epicId)) {
            System.out.println("Эпик не существует или у Вас нет к нему доступа.");
            return;

        }

        Epic epic = epicList.get(epicId);
        List<Integer> subtaskIds = new ArrayList<>(epic.getSubtaskIds());
        epic.setId(epicId);
        epicList.put(epicId, updatedEpic);

        epic.getSubtaskIds().clear();
        epic.getSubtaskIds().addAll(subtaskIds);

        updatedEpic.setId(epicId);

    }

    private void updateEpicStatus(Epic epic) {
        if (epic == null) {
            System.out.println("Нечего обновлять");
            return;
        }

        if (epic.getSubtaskIds().isEmpty()) {
            //Если у эпика нет подзадач... то статус должен быть NEW
            //Была подзадача в статусе Done, как и эпик. Я её удалил (пункт f ТЗ). Статус NEW, подзадач то нет
            epic.setStatus(Status.NEW);
        }

        boolean allDone = true;
        boolean allNew = true;

        //Если у эпика есть подзадачи, то смотрим на их статус
        List<Integer> subtaskIds = epic.getSubtaskIds();

        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtaskList.get(subtaskId);
            if (subtask != null) {
                Status status = subtask.getStatus();
                if (status != status.NEW) {
                    allNew = false;
                }
                if (status != status.DONE) {
                    allDone = false;
                }
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

    }

    //Методы сабтаски
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

    List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskList.values());
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
        if (epic == null) {
            System.out.println("Нет эпика для этой подзадачи. Ахтунг!");
            return;
        }

        epic.getSubtaskIds().remove(Integer.valueOf(id));
        subtaskList.remove(id);

        updateEpicStatus(epic);
    }


    public Subtask getSubtaskById(int id) {
        if (!subtaskList.containsKey(id)) {
            System.out.println("Подзадачи с идентификатором " + id + " нет!");
        }
        return subtaskList.get(id);
    }

    public void updateSubtask(int subtaskId, Subtask updatedSubtask) {
        if (!subtaskList.containsKey(subtaskId)) {
            System.out.println("Задача не существует или у Вас нет к ней доступа.");
            return;
        }
        updatedSubtask.setId(subtaskId);
        subtaskList.put(subtaskId, updatedSubtask);

        Epic epic = epicList.get(updatedSubtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
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

}
