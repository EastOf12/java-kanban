package tasks;

import java.util.ArrayList;

public class Epic extends Task{
    private final ArrayList<Integer> subtasks;
    public Epic(String title, String description) {
        super(title, description);
        subtasks = new ArrayList<>();
    }

    public final ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public final void setSubtasks(Integer idSubtask) {
        subtasks.add(idSubtask);
    }

    public final void removeIdSubtasks(Integer idSubtask) {
        subtasks.remove(idSubtask);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;

        Epic epic = (Epic) object;

        return subtasks.equals(epic.subtasks);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + subtasks.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", idTask= '" + getIdTask() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", subtasks='" + subtasks +
                "'}";
    }
}