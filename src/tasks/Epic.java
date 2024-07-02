package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasks;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description, LocalDateTime.now(), Duration.ofDays(0));
        subtasks = new ArrayList<>();
        endTime = startTime;
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(Integer idSubtask) {
        subtasks.add(idSubtask);
    }

    public void removeIdSubtasks(Integer idSubtask) {
        subtasks.remove(idSubtask);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        endTime = LocalDateTime.now();
        duration = Duration.ofDays(0);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;

        Epic epic = (Epic) object;

        return subtasks.equals(epic.subtasks);
    }

    public void setEndTime(LocalDateTime endTimeNew) {
        endTime = endTimeNew;
    }

    public void setStartTime(LocalDateTime newStartTime) {
        startTime = newStartTime;
    }

    public void setDuration(Duration newDuration) {
        duration = newDuration;
    }


    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + subtasks.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "task.Epic{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", idTask= '" + getIdTask() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", subtasks='" + subtasks +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                "'}";
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }
}
