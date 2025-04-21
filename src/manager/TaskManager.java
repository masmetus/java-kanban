package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    private int idCounter = 1;


    private final Map<Integer, Subtask> subtaskMap = new HashMap<>();
    private final Map<Integer, Epic> epicMap = new HashMap<>();
    private final Map<Integer, Task> taskMap = new HashMap<>();


    //Методы задачи
    public void createTask(Task task) {
        task.setId(idCounter++);
        taskMap.put(task.getId(), task);
    }

    public List<Task> getAllTask() {
        return new ArrayList<>(taskMap.values());
    }

    public Task getTaskById(int id) {
        if (!taskMap.containsKey(id)) {
            System.out.println("Задачи с идентификатором " + id + " нет!");
        }
        return taskMap.get(id);
    }

    public void removeAllTask() {
        taskMap.clear();
    }

    public void removeTaskById(int id) {
        if (!taskMap.containsKey(id)) {
            System.out.println("Задачи с идентификатором " + id + " нет!");
            return;
        }
        taskMap.remove(id);
    }

    public void updateTask(Task updatedTask) {
        if (!taskMap.containsKey(updatedTask.getId())) {
            System.out.println("Задача не существует или у Вас нет к ней доступа.");
            return;
        }

        updatedTask.setId(updatedTask.getId());
        taskMap.put(updatedTask.getId(), updatedTask);
    }

    //Методы эпика
    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epicMap.put(epic.getId(), epic);
    }

    public List<Epic> getAllEpic() {
        return new ArrayList<>(epicMap.values());
    }

    public Epic getEpicById(int id) {
        if (!epicMap.containsKey(id)) {
            System.out.println("Эпика с идентификатором " + id + " нет!");
        }
        return epicMap.get(id);
    }

    public void removeAllEpic() {
        for (Epic epic : epicMap.values()) {
            List<Integer> subtaskIds = new ArrayList<>(epic.getSubtaskIds());
            for (Integer subtaskId : subtaskIds) {
                subtaskMap.remove(subtaskId);
            }
        }
        epicMap.clear();
    }

    public void removeEpicById(int id) {
        if (!epicMap.containsKey(id)) {
            System.out.println("Эпик с идентификатором: " + id + " не существует!");
            return;
        }

        Epic epic = epicMap.get(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtaskMap.remove(subtaskId);
            }
        }
        epicMap.remove(id);
    }

    public void updateEpic(Epic updatedEpic) {
        if (!epicMap.containsKey(updatedEpic.getId())) {
            System.out.println("Эпик не существует или у Вас нет к нему доступа.");
            return;

        }

        Epic epic = epicMap.get(updatedEpic.getId());
        List<Integer> subtaskIds = new ArrayList<>(epic.getSubtaskIds());
        epic.setId(updatedEpic.getId());
        epicMap.put(updatedEpic.getId(), updatedEpic);

        epic.getSubtaskIds().clear();
        epic.getSubtaskIds().addAll(subtaskIds);

        updatedEpic.setId(updatedEpic.getId());

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
            Subtask subtask = subtaskMap.get(subtaskId);
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
        if (!epicMap.containsKey(epicId)) {
            System.out.println("Ошибка: эпик с ID " + epicId + " не найден!");
            return;
        }

        if (subtask.getEpicId() != epicId) {
            System.out.println("Ошибка при связывании подзадачи с эпиком!");
            return;
        }

        subtask.setId(idCounter++);
        subtaskMap.put(subtask.getId(), subtask);
        Epic epic = epicMap.get(epicId);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic);
    }

    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskMap.values());
    }


    public void removeAllSubtask() {
        for (Epic epic : epicMap.values()) {
            epic.removeAllSubtaskIds();
            epic.setStatus(Status.NEW);
        }
        subtaskMap.clear();

    }

    public void removeSubtaskById(int id) {
        Subtask subtask = subtaskMap.get(id);
        if (subtask == null) {
            System.out.println("Подзадачи с идентификатором " + id + " нет!");
            return;
        }

        //Удаляем связь этой подзадачи и эпика
        //Дальше по эпику. Если у него больше не осталось подзадач, то мы его в NEW
        //Если остались, то ничего

        int epicId = subtask.getEpicId();
        Epic epic = epicMap.get(epicId);
        if (epic == null) {
            System.out.println("Нет эпика для этой подзадачи. Ахтунг!");
            return;
        }

        epic.getSubtaskIds().remove(Integer.valueOf(id));
        subtaskMap.remove(id);

        updateEpicStatus(epic);
    }


    public Subtask getSubtaskById(int id) {
        if (!subtaskMap.containsKey(id)) {
            System.out.println("Подзадачи с идентификатором " + id + " нет!");
        }
        return subtaskMap.get(id);
    }

    public void updateSubtask(Subtask updatedSubtask) {
        if (!subtaskMap.containsKey(updatedSubtask.getId())) {
            System.out.println("Задача не существует или у Вас нет к ней доступа.");
            return;
        }
        updatedSubtask.setId(updatedSubtask.getId());
        subtaskMap.put(updatedSubtask.getId(), updatedSubtask);

        Epic epic = epicMap.get(updatedSubtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }


    public List<Subtask> getAllSubtaskOfEpic(int epicId) {
        Epic epic = epicMap.get(epicId);
        if (!epicMap.containsKey(epicId)) {
            System.out.println("Эпика с идентификатором " + epicId + " не существует!");
            return null;
        }
        if (epic == null) {
            System.out.println("В эпике нет подзадач!");
            return null;
        }
        List<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtaskMap.get(subtaskId);
            if (subtask != null) {
                subtasksOfEpic.add(subtask);
            }
        }

        return subtasksOfEpic;
    }

}
