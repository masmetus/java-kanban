package manager;

import model.Epic;
import model.Status;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicsHandlerTest extends HttpTaskManagerTestBase {

    @Test
    void createEpic_shouldReturn201() throws Exception {
        Epic epic = new Epic("Epic", "Description", Status.NEW);
        HttpResponse<String> response = sendPostRequest("/epics", epic);
        assertEquals(201, response.statusCode());
    }

    @Test
    void getEpicSubtasks_shouldReturn200() throws Exception {
        Epic epic = new Epic("Epic", "Desc", Status.NEW);
        manager.createEpic(epic);
        HttpResponse<String> response = sendGetRequest("/epics/" + epic.getId() + "/subtasks");
        assertEquals(200, response.statusCode());
    }
}
