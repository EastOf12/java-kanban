import java.util.ArrayList;
import java.util.Arrays;

public class Epic extends Task{
    ArrayList<Subtask> subtasks;
    public Epic(String title, String description, int idTask, TaskStatus status) {
        super(title, description, idTask, status);
        subtasks = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", idTask= '" + idTask + '\'' +
                ", status='" + status + '\'' +
                ", subtasks.size='" + subtasks.size() +
                "'}";
    }
}
