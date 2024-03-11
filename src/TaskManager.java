import java.util.ArrayList;
import java.util.Objects;

public class TaskManager {

    static int taskId;

    //1.Хранить все 3 типа задач. Формируем 3 списка с задачами.
    private ArrayList<Epic> allEpic;
    private ArrayList<Subtask> allSubtask;
    private ArrayList<Task> allTask;

    public TaskManager() {
        allEpic = new ArrayList<>();
        allSubtask = new ArrayList<>();
        allTask = new ArrayList<>();
        taskId = 0;
    }

    //Методы Task
    public ArrayList<Task> getAllTask() {
        System.out.println(allTask);
        return allTask;
    } // Получить все таски.

    public ArrayList<Task> clearAllTask() {
        allTask.clear();
        return allTask;
    } // Очистить все таски.

    public void createTask(Task task) {
        allTask.add(task);
    } // Создать новый таск.

    public Task getTask(int idTask) {
        for(Task task: allTask) {
            if(task.idTask == idTask) {
                return task;
            }
        }
        System.out.println("Таск с id " + idTask + " не найден в списке allTask.");
        return null;
    } // Получить таск по id.

    public boolean removalTask(int idTask) {
        for(Task task: allTask) {
            if(task.idTask == idTask) {
                allTask.remove(task);
                return true;
            }
        }


        return false;
    } // Удалить таск по id.

    public boolean updateTask(Task task) {
        for(Task tk: allTask) {
            if(tk.idTask == task.idTask) {
                allTask.remove(tk);
                allTask.add(task);
                return true;
            }
            }
        return false;
    } // Обновляем таск.

    //Методы Epic
    public ArrayList<Epic> getAllEpic() {
        System.out.println(allEpic);
        return allEpic;
    } // Получить все эпики.

    public ArrayList<Epic> clearAllEpic() {
        allEpic.clear();
        return allEpic;
    } // Очистить все эпики.

    public void createEpic(Epic epic) {
        allEpic.add(epic);
    } // Создать новый эпик.

    public Epic getEpic(int idTask) {
        for(Epic epic: allEpic) {
            if(epic.idTask == idTask) {
                return epic;
            }
        }
        System.out.println("Эпик с id " + idTask + " не найден в списке allEpic.");
        return null;
    } // Получить эпик по id

    public boolean removalEpic(int idTask) {
        for(Epic epic: allEpic) {
            if(epic.idTask == idTask) {
                allEpic.remove(epic);
                return true;
            }
        }
        return false;
    } // Удалить эпик по id.

    public boolean updateEpic(Epic epic) {
        for(Epic ep: allEpic) {
            if(ep.idTask == epic.idTask) {
                allEpic.remove(ep);
                if(epic.status != ep.status) {
                    epic.status = ep.status;
                }

                epic.subtasks = ep.subtasks;
                allEpic.add(epic);
                return true;
            }
        }
        return false;
    } // Обновляем эпик.

    public void updateStatusEpic(Epic epic) {
        if(epic.subtasks.isEmpty()) {
            epic.status = Task.TaskStatus.NEW;
        } else {
            int countNew = 0;
            int countDone = 0;
            for(Subtask subtask: epic.subtasks) {
                if(subtask.status == Task.TaskStatus.NEW) {
                    countNew++;
                    if(countNew == epic.subtasks.size()) {
                        epic.status = Task.TaskStatus.NEW;
                        return;
                    }
                } else if(subtask.status == Task.TaskStatus.DONE) {
                    countDone++;
                    if(countDone == epic.subtasks.size()) {
                        epic.status = Task.TaskStatus.DONE;
                        return;
                    }
                }
            }
            epic.status = Task.TaskStatus.IN_PROGRESS;
        }
    }

    //Методы Subtask
    public ArrayList<Subtask> getAllSubtask() {
        System.out.println(allSubtask);
        return allSubtask;
    } // Получить все подзадачи.

    public ArrayList<Subtask> clearAllSubtask() {
        allSubtask.clear();
        for(Epic epic: allEpic) {
            epic.subtasks.clear();
            epic.status = Task.TaskStatus.NEW;
        }
        return allSubtask;
    } // Очистить все подзадачи.

    public void createSubtask(Subtask subtask) {
        int idEpic = subtask.idEpic;

        for(Epic epic: allEpic) {
            if(epic.idTask == idEpic) {
                epic.subtasks.add(subtask);
                allSubtask.add(subtask);

                updateStatusEpic(epic);
                return;
            }
        }
        System.out.println("Не получилось создать задачу, тк эпик с id " + idEpic + " не существует.");
    } // Создать новую подзадачу.

    public Subtask getSubtask(int idTask) {
        for(Subtask subtask: allSubtask) {
            if(subtask.idTask == idTask) {
                return subtask;
            }
        }
        System.out.println("Подзадача с id " + idTask + " не найдена в списке allSubtask.");
        return null;
    } // Получить подзадачу по id.

    public boolean removalSubtask(int idTask) {
        for(Subtask subtask: allSubtask) {
            if(subtask.idTask == idTask) {
                allSubtask.remove(subtask);
                for(Epic epic: allEpic) {
                    if(epic.idTask == subtask.idEpic) {
                        epic.subtasks.remove(subtask);
                        updateStatusEpic(epic);
                        return true;
                    }
                }
            }
        }
        return false;
    } //Удалить подзадачу по id.

    public boolean updateSubtask(Subtask subtask) {
        for(Subtask sbk: allSubtask) {
            if(sbk.idTask == subtask.idTask) {
                for(Epic epic: allEpic) {
                    if(subtask.idEpic == epic.idTask) {
                        allSubtask.remove(sbk);
                        allSubtask.add(subtask);
                        epic.subtasks.remove(sbk);
                        epic.subtasks.add(subtask);
                        updateStatusEpic(epic);
                        return true;
                    }
                }
            }
        }
        return false;
    } // Обновляем подзадачу.

    public ArrayList<Subtask> getSubtaskEpic(int idEpic) {
        for(Epic epic: allEpic) {
            if(epic.idTask == idEpic) {
                return epic.subtasks;
            }
        }
        return null;
    } //Получаем все задачи в эпике.



    //2.Реализовать методы для каждого типа задач:

    // управление статусами.
    // При обновлении эпика, не может быть такого, что статус был прописан вручную.
    // При обновлении подзадачи просчитваем статус эпика в котором она находится.

    //Метод определения id для нового таска.
    public static int addTaskID() {
        taskId += 1;
        return taskId;
    }
}
