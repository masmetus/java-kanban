package model;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String title, String description, Status status, int epicIds) {
        super(title, description, status);
        this.epicId = epicIds;
    }

    public int getEpicId() {
        return epicId;
    }



    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epicId=" + getEpicId() +
                '}';
    }
}
