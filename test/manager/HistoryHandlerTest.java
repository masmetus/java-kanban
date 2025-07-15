package manager;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryHandlerTest extends HttpTaskManagerTestBase {

    @Test
    void getHistory_shouldReturn200() throws Exception {
        Task task = new Task("Test", "Desc", Status.NEW);
        manager.createTask(task);
        manager.getTaskById(task.getId()); // Добавляем в историю

        HttpResponse<String> response = sendGetRequest("/history");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(task.getTitle()));
    }

    @Test
    void emptyHistory_shouldReturnEmptyList() throws Exception {
        HttpResponse<String> response = sendGetRequest("/history");
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
}
