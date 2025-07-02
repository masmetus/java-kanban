package manager;

import model.Epic;
import model.Status;
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
            writer.write("id,type,name,status,description,epic\n");

            // Сохраняем задачи
            for (Task task : getAllTask()) {
                writer.write(CsvTaskMapper.toCsvString(task) + "\n");
            }

            // Сохраняем эпики
            for (Epic epic : getAllEpic()) {
                writer.write(CsvTaskMapper.toCsvString(epic) + "\n");
            }

            // Сохраняем подзадачи
            for (Subtask subtask : getAllSubtask()) {
                writer.write(CsvTaskMapper.toCsvString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        List<Subtask> tmpSubtask = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Пропускаем заголовок

            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Task task = CsvTaskMapper.fromCsvString(line);

                if (task instanceof Epic epic) {
                    manager.createEpic(epic);
                } else if (task instanceof Subtask subtask) {
                    tmpSubtask.add(subtask);
                } else {
                    manager.createTask(task);
                }
            }

            for (Subtask subtask : tmpSubtask) {
                manager.createSubtask(subtask, subtask.getEpicId());
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
