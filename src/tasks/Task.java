package tasks;

public class Task {
    private String title;
    private String description;
    private int idTask;
    private TaskStatus status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIdTask() {
        return idTask;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Task task = (Task) object;

        if (idTask != task.idTask) return false;
        if (!title.equals(task.title)) return false;
        if (!description.equals(task.description)) return false;
        return status == task.status;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + idTask;
        result = 31 * result + status.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "tasks.Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", idTask= '" + idTask + '\'' +
                ", status='" + status +
                "'}";
    }

}