package manager;

import tasks.Epic;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    LinkedListHistory<Task> linkedListHistory;

    public InMemoryHistoryManager() {
        linkedListHistory = new LinkedListHistory<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        Node<Task> newNode;
        final int taskId = task.getIdTask();

        if (linkedListHistory.map.containsKey(taskId)) {
            remove(taskId);
        }

        //Добавляем таск в связанный список.
        if (linkedListHistory.tail == null) {
            newNode = new Node<>(null, task, null);
            linkedListHistory.tail = newNode;
            linkedListHistory.head = newNode;

        } else {
            newNode = new Node<>(linkedListHistory.head, task, null);
            linkedListHistory.head.next = newNode;
            linkedListHistory.head = newNode;
        }

        //Добавляем таск в таблицу.
        linkedListHistory.map.put(taskId, linkedListHistory.head);

    } // Добаввляем полученную задачу в историю.

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();

        if (linkedListHistory.tail == null) {
            return history;
        }

        Node<Task> node = linkedListHistory.tail;

        while (true) {
            if (node == null) {
                break;
            } else {
                history.add(node.data);
                node = node.next;
            }
        }

        return history;
    }


    @Override
    public void remove(int id) {

        if (linkedListHistory.map.get(id).data instanceof Epic) {
            ArrayList<Integer> subtasks = ((Epic) linkedListHistory.map.get(id).data).getSubtasks();
            for (Integer subtask : subtasks) {
                remove(subtask);
            }
        }
        if (linkedListHistory.map.containsKey(id)) {
            Node nodeToRemove = linkedListHistory.map.get(id);
            if (nodeToRemove != null) {
                Node nodePrev = nodeToRemove.prev;
                Node nodeNext = nodeToRemove.next;

                if (nodePrev == null) {
                    linkedListHistory.tail = nodeNext;
                    if (nodeNext != null) {
                        nodeNext.prev = null;
                    }
                } else {
                    nodePrev.next = nodeNext;
                    if (nodeNext != null) {
                        nodeNext.prev = nodePrev;
                    } else {
                        linkedListHistory.head = nodePrev;
                    }
                }

                linkedListHistory.map.remove(id);
            }
        }
    }
} //Удаляет задачу по ее id из linkedListHistoryTask


