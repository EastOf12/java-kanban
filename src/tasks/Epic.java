package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

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

    public void setEndTime(Subtask subtask, String action, HashMap<Integer, Subtask> allSubtask) {
        if (action.equals("add")) {
            //Увеличиваем общее время выполнения эпика
            duration = duration.plus(subtask.duration);

            //Корректируем дату начала и завершения эпика.
            if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }

            if (subtask.startTime.isBefore(startTime)) {
                startTime = subtask.startTime;
            }

        } else {
            //Уменьшаем общее время выполнения эпика.

            duration = duration.minus(subtask.duration);

            //Корректируем endTime при необходимости.
            if (subtask.getEndTime().equals(endTime)) {
                endTime = null;
                for (int taskID : subtasks) {
                    LocalDateTime subtaskEndTime = allSubtask.get(taskID).getEndTime();
                    if (endTime == null || endTime.isBefore(subtaskEndTime)) {
                        endTime = subtaskEndTime;
                    }
                }
            }

            if (subtask.startTime.equals(startTime)) {
                startTime = null;
                for (int taskID : subtasks) {
                    LocalDateTime subtaskStartTime = allSubtask.get(taskID).startTime;
                    if (startTime == null || startTime.isAfter(subtaskStartTime)) {
                        startTime = subtaskStartTime;
                    }
                }
            }
        }
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
