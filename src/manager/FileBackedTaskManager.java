package manager;

import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static File file;

    public FileBackedTaskManager(File file) {
        super();
        FileBackedTaskManager.file = file;
    }


    public static FileBackedTaskManager loadFromFile() {
        try {
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int countLine = 0;

            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                countLine++;

                if (countLine > 1) {
                    Task task = taskString.taskFromString(line);

                    assert task != null;
                    if (task.getTaskType().equals(TaskType.EPIC)) {
                        fileBackedTaskManager.allEpic.put(task.getIdTask(), (Epic) task);
                    } else if (task.getTaskType().equals(TaskType.SUBTASK)) {
                        Subtask subtask = (Subtask) task;
                        fileBackedTaskManager.allSubtask.put(task.getIdTask(), subtask);
                        Epic epic = fileBackedTaskManager.allEpic.get(subtask.getIdEpic());
                        epic.setSubtasks(subtask.getIdTask());
                        fileBackedTaskManager.updateStatusEpic(epic);
                    } else {
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
        final String FIRST_LINE_TEXT = "id,type,name,status,description,epic";
        ArrayList<String> allTaskSave = new ArrayList<>();
        Path path = file.toPath();

        if (!Files.exists(path)) {
            System.out.println("Введённый путь не существует.");
        } else {
            allTaskSave.add(FIRST_LINE_TEXT);

            //Сохрнаяем в файл все задачи типа Task.
            for (Task task : getAllTask()) {
                allTaskSave.add(taskString.taskToString(task, TaskType.TASK));
            }

            //Сохрнаяем в файл все задачи типа Epic
            for (Epic epic : getAllEpic()) {
                allTaskSave.add(taskString.taskToString(epic, TaskType.EPIC));
            }

            //Сохрнаяем в файл все задачи типа Subtask
            for (Subtask subtask : getAllSubtask()) {
                allTaskSave.add(taskString.taskToString(subtask, TaskType.SUBTASK));
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
            } catch (IOException exception) {
                throw new ManagerSaveException("Ошибка при сохранении файла: " + exception.getMessage());
            }
        }
    }

    public static class taskString {
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

        private static Task taskFromString(String taskString) throws IOException {
            String[] values = taskString.split(",");

            int id = Integer.parseInt(values[0]);
            TaskType taskType = TaskType.valueOf(values[1]);
            ;
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
                case SUBTASK:
                    int idEpic = Integer.parseInt(values[5]);
                    Subtask subtask = new Subtask(title, description, idEpic);
                    subtask.setIdTask(id);
                    subtask.setStatus(status);
                    return subtask;
            }
            return null;
        }
    }

    private static class ManagerSaveException extends RuntimeException {

        private ManagerSaveException(final String message) {
            super(message);
        }
    }
}


