package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;


    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }


    //проверьте, что экземпляры класса Task равны друг другу, если равен их id
    @Test
    void checkingTheComplianceOfTaskIfTheirIdsAreEqual() {
        Task task = new Task("Я тестовая задача", "А я тестовое описание", Status.NEW);
        taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");
    }

    //проверьте, что наследники класса Task равны друг другу, если равен их id
    @Test
    void checkingTheComplianceOfEpicIfTheirIdsAreEqual() {
        Epic epic = new Epic("Я тестовый эпик", "А я тестовое описание", Status.NEW);

        taskManager.createEpic(epic);

        Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertEquals(epic, savedEpic, "Эпики должны быть равны!");
        assertEquals(epic.getId(), savedEpic.getId(), "Эпики должны быть равны!");
    }

    //Теперь сабтаски
    @Test
    void checkingTheComplianceOfSubtaskIfTheirIdsAreEqual() {
        Epic epic = new Epic("Я тестовый эпик", "Я описание", Status.NEW);

        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Я подзадача", " Я описание её", Status.NEW, epic.getId());
        taskManager.createSubtask(subtask, epic.getId());

        Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());

        assertEquals(subtask, savedSubtask, "Подзадачи должны быть равны!");
        assertEquals(subtask.getId(), savedSubtask.getId(), "Подзадачи должны быть равны!");
    }


    //"проверьте, что объект Subtask нельзя сделать своим же эпиком;" - текущая архитектура не позволяет
    //"проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;" - текущая архитектура не позволяет
    //"проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;" - текущая архитектура не позволяет


    //создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    public void taskShouldRemainUnchangedAfterAddingToManager() {

        String title = "Я заголовок!";
        String description = "А я описание!";
        Status status = Status.NEW;

        Task task = new Task(title, description, status);

        taskManager.createTask(task);

        assertEquals(task.getTitle(), title, "Заголовки должны совпадать!");
        assertEquals(task.getDescription(), description, "Описание должно совпадать!");
        assertEquals(task.getStatus(), status, "Статус должен совпадать!");
    }

    @Test
    public void EpicShouldRemainUnchangedAfterAddingToManager() {

        String title = "Я заголовок!";
        String description = "А я описание!";
        Status status = Status.NEW;

        Epic epic = new Epic(title, description, status);

        taskManager.createEpic(epic);

        assertEquals(epic.getTitle(), title, "Заголовки должны совпадать!");
        assertEquals(epic.getDescription(), description, "Описание должно совпадать!");
        assertEquals(epic.getStatus(), status, "Статус должен совпадать!");
    }

    @Test
    public void subtaskShouldRemainUnchangedAfterAddingToManager() {

        String title = "Я заголовок!";
        String description = "А я описание!";
        Status status = Status.NEW;
        Epic epic = new Epic(title, description, status);
        taskManager.createEpic(epic);

        int epicId = epic.getId();

        Subtask subtask = new Subtask(title, description, status, epicId);

        taskManager.createSubtask(subtask, epicId);

        assertEquals(subtask.getTitle(), title, "Заголовки должны совпадать!");
        assertEquals(subtask.getDescription(), description, "Описание должно совпадать!");
        assertEquals(subtask.getStatus(), status, "Статус должен совпадать!");
        assertEquals(subtask.getEpicId(), epicId, "Идентификатор эпика должен совпадать!");
    }

    @Test
    public void checkingWhatEpicUpdatedCorrect() {

        Epic epic = new Epic("Основной", "Основной", Status.NEW);
        taskManager.createEpic(epic);

        Epic epic1 = new Epic("Для теста обновления", "Обновление", Status.NEW);
        epic1.setId(epic.getId());

        taskManager.updateEpic(epic1);

        Subtask subtask = new Subtask("Я подзадача", " Я описание её", Status.DONE, epic.getId());
        taskManager.createSubtask(subtask, epic.getId());

        assertEquals(epic, epic1, "Эпик не обновился!");

    }


    @Test
    public void checkingWhatAfterRemoveAllEpicRemovedAllSubtasksEverywhere() {

        Epic epic = new Epic("Основной", "Основной", Status.NEW);
        taskManager.createEpic(epic);

        Epic epic1 = new Epic("Для теста обновления", "Обновление", Status.NEW);
        taskManager.createEpic(epic1);

        Subtask subtask = new Subtask("Я подзадача", " Я описание её", Status.DONE, epic.getId());
        taskManager.createSubtask(subtask, epic.getId());

        assertFalse(taskManager.getAllEpic().isEmpty());
        assertFalse(taskManager.getAllSubtask().isEmpty());
        assertFalse(epic.getSubtaskIds().isEmpty());

        taskManager.removeAllEpic();

        assertTrue(taskManager.getAllEpic().isEmpty());
        assertTrue(taskManager.getAllSubtask().isEmpty());
        assertTrue(epic.getSubtaskIds().isEmpty());


    }

}