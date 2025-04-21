import java.util.ArrayList;
import java.util.HashMap;

public class Subtask extends Task {

    private int epicId;

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
                "epicIds=" + getEpicId() +
                "} " + super.toString();
    }
}
