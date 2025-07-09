package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private LocalDateTime endTime;

    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String title, String description, Status status) {
        super(title, description, status);

    }

    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeAllSubtaskIds() {
        subtaskIds.clear();
    }

    public void updateTime(List<Subtask> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) {
            super.setStartTime(null);
            super.setDuration(Duration.ZERO);
            this.endTime = null;
            return;
        }

        LocalDateTime newStartTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime newEndTime = subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Duration newDuration = subtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        super.setStartTime(newStartTime);
        super.setDuration(newDuration);
        this.endTime = newEndTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtaskIds=" + getSubtaskIds() +
                '}';
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
