package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtasksHandlerTest extends HttpTaskManagerTestBase {

    @Test
    void createSubtask_shouldReturn201() throws Exception {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(30));

        HttpResponse<String> response = sendPostRequest("/subtasks", subtask);
        assertEquals(201, response.statusCode());
    }

    @Test
    void createSubtaskWithInvalidEpic_shouldReturn404() throws Exception {
        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, 999);
        HttpResponse<String> response = sendPostRequest("/subtasks", subtask);
        assertEquals(404, response.statusCode());
    }

    @Test
    void updateSubtask_shouldUpdateEpicStatus() throws Exception {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, epic.getId());
        manager.createSubtask(subtask, epic.getId());

        subtask.setStatus(Status.DONE);
        sendPostRequest("/subtasks/" + subtask.getId(), subtask);

        Epic updatedEpic = manager.getEpicById(epic.getId());
        assertEquals(Status.DONE, updatedEpic.getStatus());
    }
}
