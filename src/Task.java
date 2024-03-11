public class Task {
    String title;
    String description;
    int idTask;
    TaskStatus status;

    public Task(String title, String description, int idTask, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.idTask = idTask;
        this.status = status;
    }

    public enum TaskStatus {
        NEW,
        IN_PROGRESS,
        DONE;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", idTask= '" + idTask + '\'' +
                ", status='" + status +
                "'}";
    }

}
