package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private int idCounter = 1;


    final Map<Integer, Subtask> subtaskMap = new HashMap<>();
    final Map<Integer, Epic> epicMap = new HashMap<>();
    final Map<Integer, Task> taskMap = new HashMap<>();
    final HistoryManager historyManager = new InMemoryHistoryManager();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparingInt(Task::getId));


    //Методы задачи
    @Override
    public void createTask(Task task) {
        if (hasTimeOverlap(task)) {
            throw new ManagerSaveException("Задача пересекается по времени с существующими", null);
        }
        task.setId(idCounter++);
        taskMap.put(task.getId(), task);
        addToPrioritized(task);
    }

    @Override
    public List<Task> getAllTask() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskMap.get(id);
        if (task == null) {
            throw new ManagerSaveException("Задача с идентификатором " + id + " не найдена", null);
        } else {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public void removeAllTask() {
        taskMap.values().forEach(this::removeFromPrioritized);
        taskMap.clear();
    }

    @Override
    public void removeTaskById(int id) {
        if (!taskMap.containsKey(id)) {
            throw new ManagerSaveException("Задача с идентификатором " + id + " не найдена", null);
        }
        taskMap.remove(id);
        removeFromPrioritized(taskMap.get(id));
    }

    @Override
    public void updateTask(Task updatedTask) {
        Task existing = taskMap.get(updatedTask.getId());
        if (existing == null) {
            throw new ManagerSaveException("Задача не существует или у Вас нет к ней доступа", null);
        }

        removeFromPrioritized(existing);

        if (hasTimeOverlap(updatedTask)) {
            addToPrioritized(existing);
            throw new ManagerSaveException("Обновленная задача пересекается по времени", null);
        }

        // Сохраняем существующий ID (без вызова setId)
        taskMap.put(existing.getId(), updatedTask);
        addToPrioritized(updatedTask);
    }

    //Методы эпика
    @Override
    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epic.setStatus(Status.NEW);
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public List<Epic> getAllEpic() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicMap.get(id);
        if (epic == null) {
            throw new ManagerSaveException("Эпик с идентификатором " + id + " не найден", null);
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void removeAllEpic() {
        for (Epic epic : epicMap.values()) {
            epic.removeAllSubtaskIds();
        }

        subtaskMap.values().forEach(this::removeFromPrioritized);
        subtaskMap.clear();

        epicMap.clear();
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epicMap.remove(id);
        if (epic == null) {
            throw new ManagerSaveException("Эпик с идентификатором " + id + " не найден", null);
        }

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtaskMap.remove(subtaskId);
            if (subtask != null) {
                removeFromPrioritized(subtask);
            }
        }
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        Epic existingEpic = epicMap.get(updatedEpic.getId());
        if (existingEpic == null) {
            System.out.println("Эпик не существует или у Вас нет к нему доступа.");
            return;

        }
        existingEpic.setTitle(updatedEpic.getTitle());
        existingEpic.setDescription(updatedEpic.getDescription());

    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasks = getAllSubtaskOfEpic(epic.getId());

        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
        }

        boolean allNew = subtasks.stream()
                .allMatch(s -> s.getStatus() == Status.NEW);
        boolean allDone = subtasks.stream()
                .allMatch(s -> s.getStatus() == Status.DONE);
        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

    }

    //Методы сабтаски
    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        if (!epicMap.containsKey(epicId)) {
            throw new ManagerSaveException("Ошибка: эпик не найден!", null);
        }

        if (hasTimeOverlap(subtask)) {
            throw new ManagerSaveException("Подзадача пересекается по времени", null);
        }

        subtask.setId(idCounter++);
        subtaskMap.put(subtask.getId(), subtask);

        Epic epic = epicMap.get(epicId);
        epic.addSubtaskId(subtask.getId());

        addToPrioritized(subtask);
        updateEpicStatus(epic);
        updateEpicTime(epicId);
    }

    @Override
    public List<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public void removeAllSubtask() {
        subtaskMap.values().forEach(this::removeFromPrioritized);
        subtaskMap.clear();

        for (Epic epic : epicMap.values()) {
            epic.removeAllSubtaskIds();
            epic.setStatus(Status.NEW);
            updateEpicTime(epic.getId());
        }

    }

    @Override
    public void removeSubtaskById(Integer id) {
        Subtask subtask = subtaskMap.remove(id);
        if (subtask == null) {
            throw new ManagerSaveException("Подзадача с идентификатором " + id + " не найдена", null);
        }

        removeFromPrioritized(subtask);
        int epicId = subtask.getEpicId();
        Epic epic = epicMap.get(epicId);

        if (epic != null) {
            epic.getSubtaskIds().remove(id);
            updateEpicStatus(epic);
            updateEpicTime(epicId);
        }
    }


    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtaskMap.get(id);
        if (subtask == null) {
            throw new ManagerSaveException("Подзадача с идентификатором " + id + " не найдена", null);
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        Subtask existing = subtaskMap.get(updatedSubtask.getId());
        if (existing == null) {
            throw new ManagerSaveException("Подзадача не существует или у Вас нет к ней доступа", null);
        }

        removeFromPrioritized(existing);

        if (hasTimeOverlap(updatedSubtask)) {
            addToPrioritized(existing);
            throw new ManagerSaveException("Обновленная подзадача пересекается по времени", null);
        }

        subtaskMap.put(existing.getId(), updatedSubtask);
        addToPrioritized(updatedSubtask);

        updateEpicStatus(epicMap.get(existing.getEpicId()));
        updateEpicTime(existing.getEpicId());
    }


    @Override
    public List<Subtask> getAllSubtaskOfEpic(int epicId) {
        Epic epic = epicMap.get(epicId);
        if (epic == null) {
            throw new ManagerSaveException("Эпик с идентификатором " + epicId + " не существует", null);
        }

        return epic.getSubtaskIds().stream()
                .map(subtaskMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    //Добавление задачи
    private void addToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    //Удаление задачи
    private void removeFromPrioritized(Task task) {
        prioritizedTasks.remove(task);
    }

    //Проверка пересечений
    private boolean hasTimeOverlap(Task newTask) {
        if (newTask.getStartTime() == null) {
            return false;
        }

        return prioritizedTasks.stream()
                .filter(task -> task.getStartTime() != null)
                .anyMatch(existing -> isOverlapping(existing, newTask));
    }

    private boolean isOverlapping(Task task1, Task task2) {
        if (task1.getId() == task2.getId()) return false;

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !(end1 == null || end2 == null ||
                end1.isBefore(start2) || end2.isBefore(start1));
    }

    //Обновление времени эпика
    private void updateEpicTime(int epicId) {
        Epic epic = epicMap.get(epicId);
        if (epic != null) {
            List<Subtask> subtasks = getAllSubtaskOfEpic(epicId);
            epic.updateTime(subtasks);
        }
    }

}
