public class Subtask extends Task{
    int idEpic;

    public Subtask(String title, String description, int idTask, TaskStatus status, int idEpic) {
        super(title, description, idTask, status);
        this.idEpic = idEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", idTask= '" + idTask + '\'' +
                ", status='" + status + '\'' +
                ", idEpic='" + idEpic +
                "'}";
    }
}
