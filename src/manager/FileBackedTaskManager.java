package manager;

import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private File file = new File("src/save.csv");

    public FileBackedTaskManager() {
        super();
    }

    private static Task taskFromString(String taskString) throws IOException {
        String[] values = taskString.split(",");

        int id = Integer.parseInt(values[0]);
        String type = values[1];
        String title = values[2];
        TaskStatus status = TaskStatus.valueOf(values[3]);
        String description = values[4];

        if (type.equals("TASK")) {
            Task task = new Task(title, description);
            task.setIdTask(id);
            task.setStatus(status);
            return task;
        } else if (type.equals("EPIC")) {
            Epic epic = new Epic(title, description);
            epic.setIdTask(id);
            epic.setStatus(status);
            return epic;
        } else {
            int idEpic = Integer.parseInt(values[5]);
            Subtask subtask = new Subtask(title, description, idEpic);
            subtask.setIdTask(id);
            subtask.setStatus(status);
            return subtask;
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager();
        fileBackedTaskManager.setFile(file);

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        int countLine = 0;

        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            countLine++;

            if (countLine > 1) {
                Task task = taskFromString(line);

                if (task instanceof Epic) {
                    fileBackedTaskManager.createEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    fileBackedTaskManager.createSubtask((Subtask) task);
                } else {
                    fileBackedTaskManager.createTask(task);
                }
            }
        }

        bufferedReader.close();
        return fileBackedTaskManager;
    }

    @Override
    public ArrayList<Task> getAllTask() {
        return super.getAllTask();
    }

    @Override
    public void clearAllTask() {
        super.clearAllTask();
        save(file);
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save(file);
    }

    @Override
    public Task getTask(int idTask) {
        Task task = super.getTask(idTask);
        return task;
    }

    @Override
    public boolean removalTask(int idTask) {
        boolean result = super.removalTask(idTask);
        save(file);
        return result;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
        save(file);
        return result;
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        return super.getAllEpic();
    }

    @Override
    public void clearAllEpic() {
        super.clearAllEpic();
        save(file);
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save(file);
    }

    @Override
    public Epic getEpic(int idTask) {
        Epic epic = super.getEpic(idTask);
        return epic;
    }

    @Override
    public boolean removalEpic(int idTask) {
        boolean result = super.removalEpic(idTask);
        save(file);
        return result;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        boolean result = super.updateEpic(epic);
        save(file);
        return result;
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return super.getAllSubtask();
    }

    @Override
    public void clearAllSubtask() {
        super.clearAllSubtask();
        save(file);
    }

    @Override
    public boolean createSubtask(Subtask subtask) {
        boolean result = super.createSubtask(subtask);
        save(file);
        return result;
    }

    @Override
    public Subtask getSubtask(int idTask) {
        Subtask subtask = super.getSubtask(idTask);
        return subtask;
    }

    @Override
    public boolean removalSubtask(int idTask) {
        boolean result = super.removalSubtask(idTask);
        save(file);
        return result;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        boolean result = super.updateSubtask(subtask);
        save(file);
        return result;
    }

    @Override
    public ArrayList<Subtask> getSubtaskEpic(int idEpic) {
        ArrayList<Subtask> subtaskEpic = super.getSubtaskEpic(idEpic);
        return subtaskEpic;
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> history = super.getHistory();
        return history;
    }

    public void save(File file) {
        final String FIRST_LINE_TEXT = "id,type,name,status,description,epic";
        ArrayList<String> allTaskSave = new ArrayList<>();
        Path path = file.toPath();

        if (!Files.exists(path)) {
            System.out.println("Введённый путь не существует.");
        } else {
            allTaskSave.add(FIRST_LINE_TEXT);

            //Сохрнаяем в файл все задачи типа Task.
            for (Task task : getAllTask()) {
                allTaskSave.add(taskToString(task, TaskType.TASK));
            }

            //Сохрнаяем в файл все задачи типа Epic
            for (Epic epic : getAllEpic()) {
                allTaskSave.add(taskToString(epic, TaskType.EPIC));
            }

            //Сохрнаяем в файл все задачи типа Subtask
            for (Subtask subtask : getAllSubtask()) {
                allTaskSave.add(taskToString(subtask, TaskType.SUBTASK));
            }

            //Записываем в файл.
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                if (allTaskSave.size() == 1) {
                    writer.write("");
                    return;
                }
                for (String task : allTaskSave) {
                    writer.write(task);
                    writer.newLine(); // Добавление новой строки после каждой записи
                }
            } catch (IOException e) {
                System.out.println("Ошибка при записи текста в файл: " + e.getMessage());
            }
        }
    }

    public String taskToString(Task task, TaskType taskType) {

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

    public void setFile(File file) {
        this.file = file;
    }
}
