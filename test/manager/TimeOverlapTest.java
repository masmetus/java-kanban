package manager;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeOverlapTest extends HttpTaskManagerTestBase {

    @Test
    void createOverlappingTask_shouldReturn406() throws Exception {
        LocalDateTime time = LocalDateTime.now();
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                time, Duration.ofMinutes(30));
        manager.createTask(task1);

        Task task2 = new Task("Task2", "Desc", Status.NEW,
                time.plusMinutes(15), Duration.ofMinutes(30));

        HttpResponse<String> response = sendPostRequest("/tasks", task2);
        assertEquals(406, response.statusCode());
    }
}
