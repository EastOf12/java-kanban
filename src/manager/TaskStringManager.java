package manager;

import tasks.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskStringManager {
    public static String taskToString(Task task, TaskType taskType) {

        String taskString;
        if (taskType.equals(TaskType.SUBTASK)) {
            Subtask subtask = (Subtask) task;
            taskString = String.format("%d,%s,%s,%s,%s,%tF %tT,%d,%d", subtask.getIdTask(), taskType, subtask.getTitle(),
                    subtask.getStatus(), subtask.getDescription(), task.getStartTime()
                    , task.getStartTime(), task.getDuration().toMinutes(), subtask.getIdEpic());
        } else {
            taskString = String.format("%d,%s,%s,%s,%s,%tF %tT, %d", task.getIdTask(), taskType, task.getTitle(),
                    task.getStatus(), task.getDescription(), task.getStartTime(), task.getStartTime()
                    , task.getDuration().toMinutes());
        }

        return taskString;
    }

    public static Task taskFromString(String taskString) throws IOException {
        String[] values = taskString.split(",");

        int id = Integer.parseInt(values[0]);
        TaskType taskType = TaskType.valueOf(values[1]);
        String title = values[2];
        TaskStatus status = TaskStatus.valueOf(values[3]);
        String description = values[4];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime startTime = LocalDateTime.parse(values[5], formatter);
        long minutes = Long.parseLong(values[6].trim());
        Duration duration = Duration.ofMinutes(minutes);

        switch (taskType) {
            case TASK:
                Task task = new Task(title, description
                        , startTime, duration);
                task.setIdTask(id);
                task.setStatus(status);
                return task;
            case EPIC:
                Epic epic = new Epic(title, description);
                epic.setIdTask(id);
                epic.setStatus(status);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                return epic;
            default:
                int idEpic = Integer.parseInt(values[7]);
                Subtask subtask = new Subtask(title, description, idEpic
                        , startTime, duration);
                subtask.setIdTask(id);
                subtask.setStatus(status);
                return subtask;
        }
    }
}