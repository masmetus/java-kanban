import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

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

    public void removeAllSubtaskIds () {
        subtaskIds.clear();
    }

    public void removeSubtaskIds (int id) {
        subtaskIds.remove(id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + getSubtaskIds() +
                "} " + super.toString();
    }
}
