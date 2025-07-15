package manager;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TasksHandlerTest extends HttpTaskManagerTestBase {

    @Test
    void createTask_shouldReturn201() throws Exception {
        Task task = new Task("Test", "Description", Status.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        HttpResponse<String> response = sendPostRequest("/tasks", task);
        assertEquals(201, response.statusCode());
    }

    @Test
    void getTask_shouldReturn200() throws Exception {
        Task task = new Task("Test", "Desc", Status.NEW);
        manager.createTask(task);
        HttpResponse<String> response = sendGetRequest("/tasks/" + task.getId());
        assertEquals(200, response.statusCode());
    }

    @Test
    void getNonExistentTask_shouldReturn404() throws Exception {
        HttpResponse<String> response = sendGetRequest("/tasks/999");
        assertEquals(404, response.statusCode());
    }

    @Test
    void updateTask_shouldReturn201() throws Exception {
        Task task = new Task("Test", "Desc", Status.NEW);
        manager.createTask(task);
        task.setStatus(Status.IN_PROGRESS);
        HttpResponse<String> response = sendPostRequest("/tasks/" + task.getId(), task);
        assertEquals(201, response.statusCode());
    }

    @Test
    void deleteTask_shouldReturn200() throws Exception {
        Task task = new Task("Test", "Desc", Status.NEW);
        manager.createTask(task);
        HttpResponse<String> response = sendDeleteRequest("/tasks/" + task.getId());
        assertEquals(200, response.statusCode());
    }
}
