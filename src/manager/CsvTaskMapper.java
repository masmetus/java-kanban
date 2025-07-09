package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class CsvTaskMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private CsvTaskMapper() {
    }

    public static String toCsvString(Task task) {
        String type;
        String epicId = "";
        String startTime = "";
        String duration = "";

        if (task instanceof Epic) {
            type = "EPIC";
        } else if (task instanceof Subtask) {
            type = "SUBTASK";
            epicId = String.valueOf(((Subtask) task).getEpicId());
        } else {
            type = "TASK";
        }

        if (task.getStartTime() != null) {
            startTime = task.getStartTime().format(DATE_TIME_FORMATTER);
        }
        if (task.getDuration() != null) {
            duration = String.valueOf(task.getDuration().toMinutes());
        }

        return String.join(",",
                String.valueOf(task.getId()),
                type,
                task.getTitle(),
                task.getStatus().name(),
                task.getDescription(),
                startTime,
                duration,
                epicId
        );
    }

    public static Task fromCsvString(String csvLine) {
        String[] fields = csvLine.split(",", -1); // -1 сохраняет пустые значения

        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String title = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        String startTimeStr = fields[5];
        String durationStr = fields[6];
        String epicIdStr = fields.length > 7 ? fields[7] : "";

        LocalDateTime startTime = null;
        if (!startTimeStr.isEmpty()) {
            startTime = LocalDateTime.parse(startTimeStr, DATE_TIME_FORMATTER);
        }

        Duration duration = null;
        if (!durationStr.isEmpty()) {
            duration = Duration.ofMinutes(Long.parseLong(durationStr));
        }

        switch (type) {
            case "TASK":
                Task task = new Task(title, description, status, startTime, duration);
                task.setId(id);
                return task;
            case "EPIC":
                Epic epic = new Epic(title, description, status);
                epic.setId(id);
                return epic;
            case "SUBTASK":
                int epicId = Integer.parseInt(epicIdStr);
                Subtask subtask = new Subtask(title, description, status, epicId, startTime, duration);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
        }
    }
}
