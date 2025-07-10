package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,startTime,duration,epic\n");

            for (Task task : getAllTask()) {
                writer.write(CsvTaskMapper.toCsvString(task) + "\n");
            }

            for (Epic epic : getAllEpic()) {
                writer.write(CsvTaskMapper.toCsvString(epic) + "\n");
            }

            for (Subtask subtask : getAllSubtask()) {
                writer.write(CsvTaskMapper.toCsvString(subtask) + "\n");
            }

            writer.write("\n");
            for (Task task : getHistory()) {
                writer.write(task.getId() + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        List<Integer> historyIds = new ArrayList<>();
        boolean readingHistory = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    readingHistory = true;
                    continue;
                }

                if (readingHistory) {
                    historyIds.add(Integer.parseInt(line));
                } else {
                    Task task = CsvTaskMapper.fromCsvString(line);
                    if (task instanceof Epic epic) {
                        manager.epicMap.put(epic.getId(), epic);
                    } else if (task instanceof Subtask subtask) {
                        manager.subtaskMap.put(subtask.getId(), subtask);
                    } else {
                        manager.taskMap.put(task.getId(), task);
                    }
                }
            }

            var subtaskIterator = manager.subtaskMap.entrySet().iterator();
            while (subtaskIterator.hasNext()) {
                var entry = subtaskIterator.next();
                Subtask subtask = entry.getValue();
                Epic epic = manager.epicMap.get(subtask.getEpicId());

                if (epic != null) {
                    epic.addSubtaskId(subtask.getId());
                } else {
                    subtaskIterator.remove();
                }
            }

            for (Integer id : historyIds) {
                if (manager.taskMap.containsKey(id)) {
                    manager.historyManager.add(manager.taskMap.get(id));
                } else if (manager.epicMap.containsKey(id)) {
                    manager.historyManager.add(manager.epicMap.get(id));
                } else if (manager.subtaskMap.containsKey(id)) {
                    manager.historyManager.add(manager.subtaskMap.get(id));
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки", e);
        }

        return manager;
    }

    //Таски
    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    //Эпики
    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    //Сабтаски
    @Override
    public void createSubtask(Subtask subtask, int epicId) {
        super.createSubtask(subtask, epicId);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        save();
    }
}
