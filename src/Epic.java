import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {

    public ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String title, String description, Status status) {
        super(title, description, status);
    }


    public void addSubtaskToEpic(int id){
        subtaskIds.add(id);
    }
}
