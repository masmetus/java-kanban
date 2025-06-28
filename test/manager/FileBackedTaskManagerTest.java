package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {
    private File tmpFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tmpFile = File.createTempFile("tasks",".csv");
        manager = new FileBackedTaskManager(tmpFile);
    }

    @AfterEach
    void tearDown() {
        tmpFile.delete();
    }

    @Test
    void testFileCreatedOnFirstOperation() throws IOException {
        assertTrue(tmpFile.exists());
        assertEquals(0,tmpFile.length());

        manager.createTask(new Task("Test", "Desc", Status.NEW));

        List<String> lines = Files.readAllLines(tmpFile.toPath());
        assertEquals(2, lines.size()); // Только заголовок
        assertEquals("id,type,name,status,description,epic", lines.get(0));
    }

    @Test
    void testLoadEmptyFile() throws IOException {
        // Создаем действительно пустой файл
        try (var writer = new java.io.FileWriter(tmpFile)) {
            writer.write("id,type,name,status,description,epic\n");
        }

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tmpFile);
        assertTrue(loaded.getAllTask().isEmpty());
        assertTrue(loaded.getAllEpic().isEmpty());
        assertTrue(loaded.getAllSubtask().isEmpty());
    }

    @Test
    void testTaskCreationSavesToFile() throws IOException {
        Task task = new Task("Task 1", "Description", Status.NEW);
        manager.createTask(task); // Вызовет save() внутри

        List<String> lines = Files.readAllLines(tmpFile.toPath());
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).startsWith("1,TASK,Task 1,NEW,Description,"));
    }

    @Test
    void testTaskSaving() throws IOException {
        Task task = new Task("Task 1", "Description", Status.NEW);
        manager.createTask(task);

        // Проверяем через загрузку нового менеджера
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tmpFile);
        List<Task> tasks = loadedManager.getAllTask();

        assertEquals(1, tasks.size());
        assertEquals(task.getTitle(), tasks.get(0).getTitle());
        assertEquals(task.getDescription(), tasks.get(0).getDescription());
        assertEquals(task.getStatus(), tasks.get(0).getStatus());
    }

    @Test
    void testEpicAndSubtaskSaving() {
        Epic epic = new Epic("Epic 1", "Description", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description", Status.NEW, epic.getId());
        manager.createSubtask(subtask, epic.getId());

        // Загружаем и проверяем
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tmpFile);

        List<Epic> epics = loadedManager.getAllEpic();
        List<Subtask> subtasks = loadedManager.getAllSubtask();

        assertEquals(1, epics.size());
        assertEquals(1, subtasks.size());
        assertEquals(epic.getId(), subtasks.get(0).getEpicId());
    }
}
