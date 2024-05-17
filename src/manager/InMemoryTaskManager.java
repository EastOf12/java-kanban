package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    //1.Хранить все 3 типа задач. Формируем 3 списка с задачами.
    private final HashMap<Integer, Epic> allEpic;
    private final HashMap<Integer, Subtask> allSubtask;
    private final HashMap<Integer, Task> allTask;
    private final HistoryManager historyManager;
    private int taskId;

    public InMemoryTaskManager() {
        allEpic = new HashMap<>();
        allSubtask = new HashMap<>();
        allTask = new HashMap<>();
        taskId = 0;
        historyManager = Managers.getDefaultHistory();
    }

    //Методы tasks.Task
    @Override
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(allTask.values());
    } // Получить все таски.

    @Override
    public void clearAllTask() {
        allTask.clear();
    } // Очистить все таски.

    @Override
    public void createTask(Task task) {
        taskId = addTaskID();
        task.setIdTask(taskId);
        allTask.put(taskId, task);
    } // Создать новый таск.

    @Override
    public Task getTask(int idTask) {
        Task task = allTask.get(idTask);
        historyManager.addHistory(task);
        return task;
    } // Получить таск по id.

    @Override
    public boolean removalTask(int idTask) {
        if (allTask.containsKey(idTask)) {
            allTask.remove(idTask);
            return true;
        }
        return false;
    } // Удаляем таск по id.

    @Override
    public boolean updateTask(Task task) {
        if (allTask.containsKey(task.getIdTask())) {
            allTask.put(task.getIdTask(), task);
            return true;
        }
        return false;
    } // Обновляем таск.

    //Методы tasks.Epic
    @Override
    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(allEpic.values());
    } // Получить все эпики.

    @Override
    public void clearAllEpic() {
        allSubtask.clear();
        allEpic.clear();
    } // Очистить все эпики.

    @Override
    public void createEpic(Epic epic) {
        taskId = addTaskID();
        epic.setIdTask(taskId);
        allEpic.put(taskId, epic);
    } // Создать новый эпик.

    @Override
    public Epic getEpic(int idTask) {
        Epic epic = allEpic.get(idTask);
        historyManager.addHistory(epic);
        return epic;
    } // Получить эпик по id

    private Epic getEpicRemove(int idTask) {
        return allEpic.get(idTask);
    } // Получить удаляемый эпик по id.

    @Override
    public boolean removalEpic(int idTask) {
        if (allEpic.containsKey(idTask)) {
            for (Integer idSubtask : getEpicRemove(idTask).getSubtasks()) {
                allSubtask.remove(idSubtask);
            }
            allEpic.remove(idTask);
            return true;
        }
        return false;
    } // Удалить эпик по id.

    @Override
    public boolean updateEpic(Epic epic) {
        if (allEpic.containsKey(epic.getIdTask())) {
            updateStatusEpic(epic);
            allEpic.put(epic.getIdTask(), epic);
            return true;
        }
        return false;
    } // Обновляем эпик.

    private void updateStatusEpic(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            int countNew = 0;
            int countDone = 0;
            int sizeSubtasks = epic.getSubtasks().size();

            for (Integer idSubtask : epic.getSubtasks()) {
                if (allSubtask.get(idSubtask).getStatus() == TaskStatus.NEW) {
                    countNew++;
                    if (sizeSubtasks == countNew) {
                        epic.setStatus(TaskStatus.NEW);
                        return;
                    }
                } else if (allSubtask.get(idSubtask).getStatus() == TaskStatus.DONE) {
                    countDone++;
                    if (sizeSubtasks == countDone) {
                        epic.setStatus(TaskStatus.DONE);
                        return;
                    }
                }
            }
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    } // Обновить статус эпика.

    //Методы tasks.Subtask

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(allSubtask.values());
    } // Получить все подзадачи.

    @Override
    public void clearAllSubtask() {
        allSubtask.clear();
        for (Epic epic : allEpic.values()) {
            epic.removeAllSubtasks();
        }
    } // Очистить все подзадачи.

    @Override
    public boolean createSubtask(Subtask subtask) {
        int idEpicSubtask = subtask.getIdEpic();

        if (allEpic.containsKey(idEpicSubtask)) {
            taskId = addTaskID();
            subtask.setIdTask(taskId);
            allSubtask.put(taskId, subtask);
            Epic epic = allEpic.get(idEpicSubtask);
            epic.setSubtasks(subtask.getIdTask());
            updateStatusEpic(epic);
            return true;
        }

        return false;
    } // Создать новую подзадачу.

    @Override
    public Subtask getSubtask(int idTask) {
        Subtask subtask = allSubtask.get(idTask);
        historyManager.addHistory(subtask);
        return subtask;
    } // Получить подзадачу по id.

    @Override
    public boolean removalSubtask(int idTask) {
        if (allSubtask.containsKey(idTask)) {
            Subtask subtask = allSubtask.get(idTask);
            int idEpic = subtask.getIdEpic();
            Epic epic = allEpic.get(idEpic);
            allSubtask.remove(idTask);
            epic.removeIdSubtasks(idTask);
            updateStatusEpic(epic);
            return true;
        }
        return false;
    } // Удалить подзадачу по id.

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (allSubtask.containsKey(subtask.getIdTask())) {
            allSubtask.put(subtask.getIdTask(), subtask);
            int idEpic = subtask.getIdEpic();
            Epic epic = allEpic.get(idEpic);
            updateStatusEpic(epic);
            return true;
        }
        return false;
    } // Обновляем подзадачу.

    @Override
    public ArrayList<Subtask> getSubtaskEpic(int idEpic) {
        if (!allEpic.containsKey(idEpic)) {
            return null;
        }
        Epic epic = allEpic.get(idEpic);
        Subtask subtask;
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Integer idSubtask : epic.getSubtasks()) {
            subtask = allSubtask.get(idSubtask);
            subtasks.add(subtask);
        }
        return subtasks;
    } // Получаем все задачи в эпике.

    private int addTaskID() {
        taskId += 1;
        return taskId;
    } //Метод определения id для нового таска.

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }
}
