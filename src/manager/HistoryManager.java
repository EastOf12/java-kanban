package manager;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory(); //Возвращает исторю задач в виде связанного списка
}
