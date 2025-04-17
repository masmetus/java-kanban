import java.util.HashMap;
import java.util.Objects;

public class Task {


    private String title;
    private String description;
    private int id;
    private Status status;


    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public int setId(int id) {
        this.id = id;
        return 0;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        //this.id = id;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }
}
