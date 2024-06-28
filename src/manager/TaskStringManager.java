package manager;

import tasks.*;

import java.io.IOException;

public class TaskStringManager {
    public static String taskToString(Task task, TaskType taskType) {

        String taskString;
        if (taskType.equals(TaskType.SUBTASK)) {
            Subtask subtask = (Subtask) task;
            taskString = String.format("%d,%s,%s,%s,%s,%d", subtask.getIdTask(), taskType, subtask.getTitle(),
                    subtask.getStatus(), subtask.getDescription(), subtask.getIdEpic());
        } else {
            taskString = String.format("%d,%s,%s,%s,%s", task.getIdTask(), taskType, task.getTitle(),
                    task.getStatus(), task.getDescription());
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

        switch (taskType) {
            case TASK:
                Task task = new Task(title, description);
                task.setIdTask(id);
                task.setStatus(status);
                return task;
            case EPIC:
                Epic epic = new Epic(title, description);
                epic.setIdTask(id);
                epic.setStatus(status);
                return epic;
            default:
                int idEpic = Integer.parseInt(values[5]);
                Subtask subtask = new Subtask(title, description, idEpic);
                subtask.setIdTask(id);
                subtask.setStatus(status);
                return subtask;
        }
    }
}