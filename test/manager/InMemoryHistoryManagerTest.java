package manager;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", "Description 1", Status.NEW);
        task1.setId(1);
        task2 = new Task("Task 2", "Description 2", Status.IN_PROGRESS);
        task2.setId(2);
        task3 = new Task("Task 3", "Description 3", Status.DONE);
        task3.setId(3);
    }

    @Test
    void getHistoryTest() {
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void testInitialization() {
        assertNotNull(historyManager);
    }

    //убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void historyManagerShouldKeepOnlyLastTaskView() {
        historyManager.add(task1);
        Task updatedTask = new Task("Updated", "Desc", Status.DONE);
        updatedTask.setId(task1.getId());
        historyManager.add(updatedTask);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(updatedTask.getTitle(), history.get(0).getTitle());
    }

    @Test
    void emptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void noDuplicatesInHistory() {
        historyManager.add(task1);
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void removeFromBeginningOfHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void removeFromMiddleOfHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertFalse(history.contains(task2));
    }

    @Test
    void removeFromEndOfHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(1));
    }

    @Test
    void removeNonExistentTaskFromHistory() {
        historyManager.add(task1);
        historyManager.remove(999); // Несуществующий ID
        assertEquals(1, historyManager.getHistory().size());
    }
}