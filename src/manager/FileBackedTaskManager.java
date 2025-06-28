package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");

            // Сохраняем обычные задачи
            List<Task> tasks = getAllTask();
            for (Task task : tasks) {
                if (!(task instanceof Epic) && !(task instanceof Subtask)) {
                    writer.write(toCsvString(task) + "\n");
                }
            }

            // Сохраняем эпики
            List<Epic> epics = getAllEpic();
            for (Epic epic : epics) {
                writer.write(toCsvString(epic) + "\n");
            }

            // Сохраняем подзадачи
            List<Subtask> subtasks = getAllSubtask();
            for (Subtask subtask : subtasks) {
                writer.write(toCsvString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    private String toCsvString(Task task) {
        String type;
        String epicId = "";

        if (task instanceof Epic) {
            type = "EPIC";
        } else if (task instanceof Subtask) {
            type = "SUBTASK";
            epicId = String.valueOf(((Subtask) task).getEpicId());
        } else {
            type = "TASK";
        }

        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                type,
                task.getTitle(),
                task.getStatus().name(),
                task.getDescription(),
                epicId);
    }


    private Task fromCsvString(String csvLine) {
        String[] fields = csvLine.split(",");
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String title = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        if ("TASK".equals(type)) {
            Task task = new Task(title, description, status);
            task.setId(id);
            return task;
        } else if ("EPIC".equals(type)) {
            Epic epic = new Epic(title, description, status);
            epic.setId(id);
            return epic;
        } else if ("SUBTASK".equals(type)) {
            int epicId = Integer.parseInt(fields[5]);
            Subtask subtask = new Subtask(title, description, status, epicId);
            subtask.setId(id);
            return subtask;
        } else {
            throw new ManagerSaveException("Неизвестный тип задачи: " + type, null);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Map<Integer, List<Subtask>> epicSubtasks = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Пропускаем заголовок

            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Task task = manager.fromCsvString(line);

                if (task instanceof Epic) {
                    manager.createEpic((Epic) task);
                    epicSubtasks.put(task.getId(), new ArrayList<>());
                } else if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    List<Subtask> subtasks = epicSubtasks.get(subtask.getEpicId());
                    if (subtasks == null) {
                        subtasks = new ArrayList<Subtask>();
                        epicSubtasks.put(subtask.getEpicId(), subtasks);
                    }
                    subtasks.add(subtask);
                } else {
                    manager.createTask(task);
                }
            }

            for (Map.Entry<Integer, List<Subtask>> entry : epicSubtasks.entrySet()) {
                int epicId = entry.getKey();
                List<Subtask> subtasks = entry.getValue();
                for (Subtask subtask : subtasks) {
                    manager.createSubtask(subtask, epicId);
                }
            }

        } catch (FileNotFoundException e) {
            return manager;
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


    //Доп задание, да и для себя норм проверить это
    public static void main(String[] args) {

        File file = new File("tasks.csv");


        FileBackedTaskManager manager1 = new FileBackedTaskManager(file);


        Task task1 = new Task("Task 1", "Description 1, not epic", Status.NEW);
        manager1.createTask(task1);

        Task task2 = new Task("Task 2", "not epic", Status.IN_PROGRESS);
        manager1.createTask(task2);


        Epic epic1 = new Epic("Epic 1", "Description epic 1", Status.NEW);
        manager1.createEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.NEW, epic1.getId());
        manager1.createSubtask(subtask1, epic1.getId());

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);

        System.out.println("Tasks equal: " + manager1.getAllTask().equals(manager2.getAllTask()));
        System.out.println("Epics equal: " + manager1.getAllEpic().equals(manager2.getAllEpic()));
        System.out.println("Subtasks equal: " + manager1.getAllSubtask().equals(manager2.getAllSubtask()));
    }


}
