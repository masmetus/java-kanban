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
    public void historyManagerShouldKeepOnlyLastTaskView(){
        Task task = new Task("Тестовая задачка", "И такое же описание", Status.IN_PROGRESS);
        taskManager.createTask(task);

        taskManager.getTaskById(task.getId());

        assertEquals(1, taskManager.getHistory().size());
        Task firstView = taskManager.getHistory().get(0);
        assertEquals(task.getId(), firstView.getId());
        assertEquals(task.getTitle(), firstView.getTitle());
        assertEquals(task.getDescription(), firstView.getDescription());
        assertEquals(task.getStatus(), firstView.getStatus());

        // Обновляем задачу
        Task updatedTask = new Task("Обновленная задача", "Обновленное описание", Status.DONE);
        updatedTask.setId(task.getId());
        taskManager.updateTask(updatedTask);

        //Смотрим её и убеждаемся, что там только одна задача
        taskManager.getTaskById(updatedTask.getId());

        assertEquals(1, taskManager.getHistory().size());
        Task lastView = taskManager.getHistory().get(0);
        assertEquals(updatedTask.getId(), lastView.getId());
        assertEquals(updatedTask.getTitle(), lastView.getTitle());
        assertEquals(updatedTask.getDescription(), lastView.getDescription());
        assertEquals(updatedTask.getStatus(), lastView.getStatus());

        // Убеждаемся, что это действительно обновленная версия
        assertNotEquals(task.getTitle(), lastView.getTitle());
        assertNotEquals(task.getDescription(), lastView.getDescription());
        assertNotEquals(task.getStatus(), lastView.getStatus());
    }
}