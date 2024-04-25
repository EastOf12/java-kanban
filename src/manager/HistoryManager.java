package manager;

import tasks.Task;

import java.util.HashMap;

public interface HistoryManager {
    void addHistory(Task task);

    void remove(int id);

    InMemoryHistoryManager.LinkedListHistory<Task> getHistory(); //Возвращает исторю задач в виде связанного списка

    HashMap<Integer, InMemoryHistoryManager.LinkedListHistory.Node> getHashMapHistory(); //Возвращает исторю задач в виде таблицы.
}
