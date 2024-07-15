package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        try {
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int countLine = 0;

            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                countLine++;

                if (countLine > 1) {
                    Task task = TaskStringManager.taskFromString(line);

                    if (task.getTaskType().equals(TaskType.EPIC)) {
                        fileBackedTaskManager.allEpic.put(task.getIdTask(), (Epic) task);
                    } else if (task.getTaskType().equals(TaskType.SUBTASK)) {
                        Subtask subtask = (Subtask) task;
                        fileBackedTaskManager.prioritizedTasks.add(subtask);
                        fileBackedTaskManager.allSubtask.put(task.getIdTask(), subtask);
                        Epic epic = fileBackedTaskManager.allEpic.get(subtask.getIdEpic());
                        epic.setSubtasks(subtask.getIdTask());
                    } else {
                        fileBackedTaskManager.prioritizedTasks.add(task);
                        fileBackedTaskManager.allTask.put(task.getIdTask(), task);
                    }
                }
            }

            bufferedReader.close();
            return fileBackedTaskManager;
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при загрузке файла: " + exception.getMessage());
        }

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


    public void save(File file) {
        final String FIRST_LINE_TEXT = "id,type,name,status,description,startDate,startTime,duration,epic";
        ArrayList<String> allTaskSave = new ArrayList<>();
        Path path = file.toPath();

        //Записываем в файл.
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            allTaskSave.add(FIRST_LINE_TEXT);

            //Сохрнаяем в файл все задачи типа Task.
            for (Task task : getAllTask()) {
                allTaskSave.add(TaskStringManager.taskToString(task, TaskType.TASK));
            }

            //Сохрнаяем в файл все задачи типа Epic
            for (Epic epic : getAllEpic()) {
                allTaskSave.add(TaskStringManager.taskToString(epic, TaskType.EPIC));
            }

            //Сохрнаяем в файл все задачи типа Subtask
            for (Subtask subtask : getAllSubtask()) {
                allTaskSave.add(TaskStringManager.taskToString(subtask, TaskType.SUBTASK));
            }

            if (allTaskSave.size() == 1) {
                writer.write("");
                return;
            }
            for (String task : allTaskSave) {
                writer.write(task);
                writer.newLine(); // Добавление новой строки после каждой записи
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении файла: " + exception.getMessage());
        }
    }
}


