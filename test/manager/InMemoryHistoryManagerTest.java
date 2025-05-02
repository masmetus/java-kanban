package manager;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void getHistoryTest() {
        Task task = new Task("Тестовая задачка", "И такое же описание", Status.DONE);
        taskManager.createTask(task);

        // Получаем задачу, чтобы добавить её в историю
        taskManager.getTaskById(task.getId());

        // Проверяем, что история содержит 1 задачу
        assertEquals(1, taskManager.getHistory().size());
        assertEquals(task, taskManager.getHistory().get(0));
    }

    @Test
    public void testInitialization() {
        assertNotNull(taskManager);
    }

    //убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    public void historyManagerShouldKeepDifferentVersionsOfTask(){
        Task task = new Task("Тестовая задачка", "И такое же описание", Status.IN_PROGRESS);
        taskManager.createTask(task);

        taskManager.getTaskById(task.getId());

        assertEquals(task.getId(), taskManager.getHistory().get(0).getId());
        assertEquals(task.getTitle(), taskManager.getHistory().get(0).getTitle());
        assertEquals(task.getDescription(), taskManager.getHistory().get(0).getDescription());
        assertEquals(task.getStatus(), taskManager.getHistory().get(0).getStatus());

        Task updateTask = new Task("Обновленная задача", "Обновленное описание", Status.DONE);
        updateTask.setId(task.getId());
        taskManager.updateTask(updateTask);

        taskManager.getTaskById(updateTask.getId());

        assertEquals(updateTask.getId(), taskManager.getHistory().get(1).getId());

        assertEquals(updateTask.getTitle(), taskManager.getHistory().get(1).getTitle());
        assertNotEquals(updateTask.getTitle(), taskManager.getHistory().get(0).getTitle());

        assertEquals(updateTask.getDescription(), taskManager.getHistory().get(1).getDescription());
        assertNotEquals(updateTask.getDescription(), taskManager.getHistory().get(0).getDescription());

        assertEquals(updateTask.getStatus(), taskManager.getHistory().get(1).getStatus());
        assertNotEquals(updateTask.getStatus(), taskManager.getHistory().get(0).getStatus());
    }
}