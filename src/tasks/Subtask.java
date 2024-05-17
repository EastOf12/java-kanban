package tasks;
public class Subtask extends Task {
    private int idEpic;

    public Subtask(String title, String description, int idEpic) {
        super(title, description);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;

        Subtask subtask = (Subtask) object;

        return idEpic == subtask.idEpic;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + idEpic;
        return result;
    }

    @Override
    public String toString() {
        return "task.Subtask{" + "title='" + getTitle() + '\'' + ", description='" + getDescription() + '\'' + ", idTask= '" + getIdTask() + '\'' + ", status='" + getStatus() + '\'' + ", idEpic='" + idEpic + "'}";
    }
}
