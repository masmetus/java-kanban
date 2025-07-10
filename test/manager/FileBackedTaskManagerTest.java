package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tmpFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tmpFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tmpFile);
    }

    @AfterEach
    void tearDown() {
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
    }

    @Test
    void shouldCreateFileOnManagerCreation() {
        assertTrue(tmpFile.exists());
    }

    @Test
    void shouldSaveTaskToFile() throws IOException {
        Task task = new Task("Test Task", "Description", Status.NEW);
        manager.createTask(task);

        //У нас есть перенос строк
        String content = Files.readString(tmpFile.toPath());
        String[] lines = content.split("\n", -1); // -1 сохраняет пустые строки

        assertTrue(lines.length >= 3, "Файл должен содержать минимум 3 строки (заголовок, задача, разделитель)");

        // Проверяем заголовок
        assertEquals("id,type,name,status,description,startTime,duration,epic", lines[0]);

        String[] taskData = lines[1].split(",");
        assertEquals("1", taskData[0]); // ID
        assertEquals("TASK", taskData[1]);
        assertEquals("Test Task", taskData[2]);
        assertEquals("NEW", taskData[3]);
        assertEquals("Description", taskData[4]);

        assertEquals("", lines[2], "После задач должна быть пустая строка-разделитель");
    }

    @Test
    void shouldSaveEpicToFile() throws IOException {
        Epic epic = new Epic("Test Epic", "Description", Status.NEW);
        manager.createEpic(epic); // Вызывает save() внутри

        List<String> lines = Files.readAllLines(tmpFile.toPath());
        assertTrue(lines.get(1).contains("EPIC"), "Файл должен содержать тип EPIC");
    }

    @Test
    void shouldSaveSubtaskToFile() throws IOException {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Desc", Status.NEW, epic.getId());
        manager.createSubtask(subtask, epic.getId());

        String content = Files.readString(tmpFile.toPath());
        String[] lines = content.split("\n", -1);

        assertTrue(lines.length >= 4, "Файл должен содержать минимум 4 строки");

        String[] subtaskData = lines[2].split(",");
        assertEquals("SUBTASK", subtaskData[1]);
        assertEquals(String.valueOf(epic.getId()), subtaskData[7]);

        assertEquals("", lines[3], "После задач должна быть пустая строка");
    }

    @Test
    void shouldSaveTimeDataToFile() throws IOException {
        LocalDateTime time = LocalDateTime.of(2023, 1, 1, 12, 0);
        Duration duration = Duration.ofMinutes(30);
        Task task = new Task("Task", "Desc", Status.NEW, time, duration);
        manager.createTask(task);

        String content = Files.readString(tmpFile.toPath());
        String[] lines = content.split("\n", -1);

        // Проверяем строку с задачей
        String[] taskData = lines[1].split(",");
        assertEquals("01.01.2023 12:00", taskData[5], "Формат времени должен быть dd.MM.yyyy HH:mm");
        assertEquals("30", taskData[6], "Длительность в минутах");
    }

    @Test
    void shouldLoadEmptyFile() throws IOException {
        try (var writer = new java.io.FileWriter(tmpFile)) {
            writer.write("id,type,name,status,description,startTime,duration,epic\n");
        }

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tmpFile);
        assertTrue(loaded.getAllTask().isEmpty(), "Загруженный менеджер должен быть пустым");
    }

    @Test
    void shouldLoadTasksFromFile() {
        Task task = new Task("Task", "Desc", Status.NEW);
        manager.createTask(task);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tmpFile);
        assertEquals(1, loaded.getAllTask().size(), "Должна загрузиться одна задача");
        assertEquals(task.getTitle(), loaded.getAllTask().get(0).getTitle(), "Названия задач должны совпадать");
    }

    @Test
    void shouldLoadEpicsAndSubtasksFromFile() {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Desc", Status.NEW, epic.getId());
        manager.createSubtask(subtask, epic.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tmpFile);
        assertEquals(1, loaded.getAllEpic().size(), "Должен загрузиться один эпик");
        assertEquals(1, loaded.getAllSubtask().size(), "Должна загрузиться одна подзадача");
        assertEquals(epic.getId(), loaded.getAllSubtask().get(0).getEpicId(),
                "Подзадача должна быть связана с эпиком");
    }

    @Test
    void shouldThrowExceptionWhenLoadingFromInvalidFile() {
        assertThrows(ManagerSaveException.class, () ->
                        FileBackedTaskManager.loadFromFile(new File("nonexistent_file.csv")),
                "Должно выбрасываться исключение при загрузке из несуществующего файла");
    }

    @Test
    void shouldPreserveTaskOrderWhenSavingAndLoading() {
        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        Task task2 = new Task("Task 2", "Desc", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tmpFile);
        assertEquals(task1.getId(), loaded.getAllTask().get(0).getId(),
                "Порядок задач должен сохраняться");
    }

    @Test
    void shouldCleanupOrphanedSubtasks() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile))) {
            writer.write("id,type,name,status,description,startTime,duration,epic\n");
            writer.write("1,EPIC,Epic,NEW,Desc,,,\n");
            writer.write("2,SUBTASK,Orphaned,NEW,Orphaned desc,,,999\n");
            writer.write("3,SUBTASK,Valid,NEW,Valid desc,,,1\n");
            writer.write("\n\n");
        }
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tmpFile);

        List<Subtask> subtasks = manager.getAllSubtask();
        assertEquals(1, subtasks.size(), "Должна остаться только одна подзадача");
        assertEquals("Valid", subtasks.get(0).getTitle(), "Должна остаться корректная подзадача");

        Epic epic = manager.getEpicById(1);
        assertEquals(1, epic.getSubtaskIds().size(), "Эпик должен содержать одну подзадачу");
        assertEquals(3, epic.getSubtaskIds().get(0), "Это должна быть подзадача с id=3");
    }
}
