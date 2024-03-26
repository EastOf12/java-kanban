package manager;

import tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private final ArrayList<Task> historyTask;
    private static final int MAX_SIZE_HISTORY_TASK = 10;

    public InMemoryHistoryManager() {
        historyTask = new ArrayList<>();
    }

    @Override
    public void addHistory(Task task) {
        if(task == null) {
            return;
        }

        if(historyTask.size() == MAX_SIZE_HISTORY_TASK) {
            historyTask.removeFirst();
        }
        historyTask.add(task);
    } // Добаввляем полученную задачу в историю.

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyTask);
    }
}

