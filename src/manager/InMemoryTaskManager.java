package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    //1.Хранить все 3 типа задач. Формируем 3 списка с задачами.
    protected final HashMap<Integer, Epic> allEpic;
    protected final HashMap<Integer, Subtask> allSubtask;
    protected final HashMap<Integer, Task> allTask;
    protected final TreeSet<Task> prioritizedTasks;
    private final HistoryManager historyManager;
    private int taskId;

    public InMemoryTaskManager() {
        allEpic = new HashMap<>();
        allSubtask = new HashMap<>();
        allTask = new HashMap<>();
        taskId = 0;
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    //Методы tasks.Task
    @Override
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(allTask.values());
    } // Получить все таски.

    @Override
    public void clearAllTask() {
        for (Task task : getAllTask()) {
            prioritizedTasks.remove(task);
            historyManager.remove(task.getIdTask());
        }
        allTask.clear();
    } // Очистить все таски.

    @Override
    public void createTask(Task task) {
        taskId = addTaskID();
        task.setIdTask(taskId);
        allTask.put(taskId, task);

        if (checkTaskIntersections(task) && task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }

    } // Создать новый таск.

    @Override
    public Task getTask(int idTask) {
        Task task = allTask.get(idTask);
        historyManager.add(task);
        return task;
    } // Получить таск по id.

    @Override
    public boolean removalTask(int idTask) {
        if (allTask.containsKey(idTask)) {
            prioritizedTasks.remove(allTask.get(idTask));
            allTask.remove(idTask);
            historyManager.remove(idTask);
            return true;
        }
        return false;
    } // Удаляем таск по id.

    @Override
    public boolean updateTask(Task task) {
        if (allTask.containsKey(task.getIdTask())) {
            Task oldTask = allTask.get(task.getIdTask());
            allTask.put(task.getIdTask(), task);
            prioritizedTasks.remove(oldTask);

            if (checkTaskIntersections(task) && task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }

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
        for (Epic epic : allEpic.values()) {
            removeAllSubtasksEpic(epic); //Удаляем все подзадачи по эпику из всех списков.
            historyManager.remove(epic.getIdTask()); //Удаляем эпик из истории.
        }

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
        historyManager.add(epic);
        return epic;
    } // Получить эпик по id

    private Epic getEpicRemove(int idTask) {
        return allEpic.get(idTask);
    } // Получить удаляемый эпик по id.

    @Override
    public boolean removalEpic(int idTask) {
        if (allEpic.containsKey(idTask)) {
            removeAllSubtasksEpic(getEpicRemove(idTask));
            historyManager.remove(idTask);
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

    protected void updateStatusEpic(Epic epic) {
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
        for (Subtask subtask : getAllSubtask()) {
            prioritizedTasks.remove(subtask);
        }

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
            setEndTime(epic, subtask, "add", allSubtask);

            if (checkTaskIntersections(subtask) && subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }

            return true;
        }
        return false;
    } // Создать новую подзадачу.

    @Override
    public Subtask getSubtask(int idTask) {
        Subtask subtask = allSubtask.get(idTask);
        historyManager.add(subtask);
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
            setEndTime(epic, subtask, "removal", allSubtask);
            updateStatusEpic(epic);
            historyManager.remove(idTask);
            prioritizedTasks.remove(subtask);
            return true;
        }
        return false;
    } // Удалить подзадачу по id.

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (allSubtask.containsKey(subtask.getIdTask())) {
            Subtask subtaskOld = allSubtask.get(subtask.getIdTask());
            allSubtask.put(subtask.getIdTask(), subtask);
            int idEpic = subtask.getIdEpic();
            Epic epic = allEpic.get(idEpic);
            updateStatusEpic(epic);
            prioritizedTasks.remove(subtaskOld);

            if (checkTaskIntersections(subtask) && subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }

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
        return (ArrayList<Task>) historyManager.getHistory();
    } //Возвращает исторю просмотров.

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean checkTaskIntersections(Task newTask) {
        return prioritizedTasks.stream()
                .allMatch(task ->
                        newTask.getEndTime().isBefore(task.getStartTime())
                                || newTask.getStartTime().isAfter(task.getEndTime())
                );
    } //Проверяет, пересекается ли срок выполнения новой задачи с теми, что уже есть. Если пересечений нет, то будет true

    private void setEndTime(Epic epic, Subtask subtask, String action, HashMap<Integer, Subtask> allSubtask) {
        if (action.equals("add")) {
            //Корректируем дату начала и завершения эпика.
            if (subtask.getEndTime().isAfter(epic.getEndTime())) {
                epic.setEndTime(subtask.getEndTime());
            }

            if (subtask.getStartTime().isBefore(epic.getStartTime())) {
                epic.setStartTime(subtask.getStartTime());
            }

        } else {
            //Корректируем endTime и startTime при необходимости.
            if (subtask.getEndTime().equals(epic.getEndTime())) {
                epic.setEndTime(null);
                for (int taskID : epic.getSubtasks()) {
                    LocalDateTime subtaskEndTime = allSubtask.get(taskID).getEndTime();
                    if (epic.getEndTime() == null || epic.getEndTime().isBefore(subtaskEndTime)) {
                        epic.setEndTime(subtaskEndTime);
                    }

                    if (subtask.getStartTime().equals(epic.getStartTime())) {
                        epic.setStartTime(null);
                        LocalDateTime subtaskStartTime = allSubtask.get(taskID).getStartTime();
                        if (epic.getStartTime() == null || epic.getStartTime().isAfter(subtaskStartTime)) {
                            epic.setStartTime(subtaskStartTime);
                        }
                    }
                }
            }
        }

        epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime())); //Корректируем время выполнения эпика.
    } //Корректирует время начала и завершения эпика при добавлении или удалении подзадачь.

    private void removeAllSubtasksEpic(Epic epic) {
        for (Integer idSubtask : epic.getSubtasks()) {
            Subtask subtask = allSubtask.get(idSubtask);
            allSubtask.remove(idSubtask);
            historyManager.remove(idSubtask);
            prioritizedTasks.remove(subtask);
        }
    } //Удаляет все подзадачи у эпика из всех списков.
}
