package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int taskId;

    //1.Хранить все 3 типа задач. Формируем 3 списка с задачами.
    private HashMap<Integer, Epic> allEpic;
    private HashMap<Integer, Subtask> allSubtask;
    private HashMap<Integer, Task> allTask;

    public TaskManager() {
        allEpic = new HashMap<>();
        allSubtask = new HashMap<>();
        allTask = new HashMap<>();
        taskId = 0;
    }

    //Методы tasks.Task
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(allTask.values());
    } // Получить все таски.

    public void clearAllTask() {
        allTask.clear();
    } // Очистить все таски.

    public void createTask(Task task) {
        taskId = addTaskID();
        task.setIdTask(taskId);
        allTask.put(taskId, task);
    } // Создать новый таск.

    public Task getTask(int idTask) {
        return allTask.get(idTask);
    } // Получить таск по id.

    public boolean removalTask(int idTask) {
        if(allTask.containsKey(idTask)){
            allTask.remove(idTask);
            return true;
        }
        return false;
        } // Удаляем таск по id.

    public boolean updateTask(Task task) {
        if(allTask.containsKey(task.getIdTask())) {
            allTask.put(task.getIdTask(), task);
            return true;
        }
        return false;
    } // Обновляем таск.

    //Методы tasks.Epic
    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(allEpic.values());
    } // Получить все эпики.

    public void clearAllEpic() {
        allSubtask.clear();
        allEpic.clear();
    } // Очистить все эпики.

    public void createEpic(Epic epic) {
        taskId = addTaskID();
        epic.setIdTask(taskId);
        allEpic.put(taskId, epic);
    } // Создать новый эпик.

    public Epic getEpic(int idTask) {
        return allEpic.get(idTask);
    } // Получить эпик по id

    public boolean removalEpic(int idTask) {
        if(allEpic.containsKey(idTask)){
            for (Integer idSubtask: getEpic(idTask).getSubtasks()) {
                allSubtask.remove(idSubtask);
            }
            allEpic.remove(idTask);
            return true;
        }
        return false;
    } // Удалить эпик по id.

    public boolean updateEpic(Epic epic) {
        if(allEpic.containsKey(epic.getIdTask())) {
            updateStatusEpic(epic);
            allEpic.put(epic.getIdTask(), epic);
            return true;
        }
        return false;
    } // Обновляем эпик.

    private void updateStatusEpic(Epic epic) {
        if(epic.getSubtasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            int countNew = 0;
            int countDone = 0;
            int sizeSubtasks = epic.getSubtasks().size();

            for(Integer idSubtask: epic.getSubtasks()) {
                if(allSubtask.get(idSubtask).getStatus() == TaskStatus.NEW) {
                    countNew++;
                    if(sizeSubtasks == countNew) {
                        epic.setStatus(TaskStatus.NEW);
                        return;
                    }
                } else if (allSubtask.get(idSubtask).getStatus() == TaskStatus.DONE) {
                    countDone++;
                    if(sizeSubtasks == countDone) {
                        epic.setStatus(TaskStatus.DONE);
                        return;
                    }
                }
            }
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    } // Обновить статус эпика.

    //Методы tasks.Subtask

    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(allSubtask.values());
    } // Получить все подзадачи.

    public void clearAllSubtask() {
        allSubtask.clear();
        for(Epic epic: allEpic.values()) {
            epic.removeAllSubtasks();
        }
    } // Очистить все подзадачи.

    public boolean createSubtask(Subtask subtask) {
        int idEpicSubtask = subtask.getIdEpic();

        if(allEpic.containsKey(idEpicSubtask)) {
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

    public Subtask getSubtask(int idTask) {
        return allSubtask.get(idTask);
    } // Получить подзадачу по id.

    public boolean removalSubtask(int idTask) {
        if(allSubtask.containsKey(idTask)){
            Subtask subtask = allSubtask.get(idTask);
            int idEpic = subtask.getIdEpic();
            Epic epic = allEpic.get(idEpic);
            allSubtask.remove(idTask);
            epic.removeIdSubtasks(idTask);
            updateStatusEpic(epic);
            return true;
        }
        return false;
    } //Удалить подзадачу по id.

    public boolean updateSubtask(Subtask subtask) {
        if(allSubtask.containsKey(subtask.getIdTask())) {
            allSubtask.put(subtask.getIdTask(), subtask);
            int idEpic = subtask.getIdEpic();
            Epic epic = allEpic.get(idEpic);
            updateStatusEpic(epic);
            return true;
        }
        return false;
    } // Обновляем подзадачу.

    public ArrayList<Subtask> getSubtaskEpic(int idEpic) {
        if(!allEpic.containsKey(idEpic)) {
            return null;
        }
        Epic epic = allEpic.get(idEpic);
        Subtask subtask;
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for(Integer idSubtask: epic.getSubtasks()) {
            subtask = allSubtask.get(idSubtask);
            subtasks.add(subtask);
        }
        return subtasks;
    } //Получаем все задачи в эпике.

    //Метод определения id для нового таска.
    private int addTaskID() {
        taskId += 1;
        return taskId;
    }
}
