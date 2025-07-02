package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public final class CsvTaskMapper {
    private CsvTaskMapper() {
    }

    public static String toCsvString(Task task) {
        String type;
        String epicId = "";

        if (task instanceof Epic) {
            type = "EPIC";
        } else if (task instanceof Subtask subtask) {
            type = "SUBTASK";
            epicId = String.valueOf(((Subtask) task).getEpicId());
        } else {
            type = "TASK";
        }

        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                type,
                task.getTitle(),
                task.getStatus().name(),
                task.getDescription(),
                epicId);
    }

    public static Task fromCsvString(String csvLine) {
        String[] fields = csvLine.split(",");
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String title = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        if ("TASK".equals(type)) {
            Task task = new Task(title, description, status);
            task.setId(id);
            return task;
        } else if ("EPIC".equals(type)) {
            Epic epic = new Epic(title, description, status);
            epic.setId(id);
            return epic;
        } else if ("SUBTASK".equals(type)) {
            int epicId = Integer.parseInt(fields[5]);
            Subtask subtask = new Subtask(title, description, status, epicId);
            subtask.setId(id);
            return subtask;
        } else {
            throw new ManagerSaveException("Неизвестный тип задачи: " + type, null);
        }
    }
}
