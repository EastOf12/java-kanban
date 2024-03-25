package manager;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    ArrayList<Task> historyTask;
    private final int maxSizeHistoryTask;

    public InMemoryHistoryManager() {
        maxSizeHistoryTask = 10;
        historyTask = new ArrayList<>();
    }

    @Override
    public void addHistory(Task task) {
        if(historyTask.size() == maxSizeHistoryTask) {
            historyTask.removeFirst();
        }
        historyTask.add(task);
    } // Добаввляем полученную задачу в историю.

    @Override
    public ArrayList<Task> getHistory() {
        if(historyTask.isEmpty()) {
            return null;
        }
        return historyTask;
    }
}

