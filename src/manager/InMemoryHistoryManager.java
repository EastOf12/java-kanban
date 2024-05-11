package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedListHistory linkedListHistory;

    public InMemoryHistoryManager() {
        linkedListHistory = new LinkedListHistory();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            linkedListHistory.addNode(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return linkedListHistory.getNodes();
    }

    @Override
    public void remove(int id) {
        if (linkedListHistory.map.containsKey(id)) {
            linkedListHistory.removeNode(id);
        }
    }

    private static class Node {
        Task data;
        Node next;
        Node prev;

        public Node(Task data) {
            this.data = data;
        }
    }

    private static class LinkedListHistory {
        Node head;
        Node tail;
        Map<Integer, Node> map;

        public LinkedListHistory() {
            head = null;
            tail = null;
            map = new HashMap<>();
        }

        private boolean isEmpty() {
            return head == null;
        }

        private void addNode(Task data) {
            Node newData = new Node(data);

            final int taskId = data.getIdTask();

            if (map.containsKey(taskId)) {
                removeNode(taskId);
                System.out.println(taskId + " должен быть удален");
            }

            if (isEmpty()) {
                tail = newData;
            } else {
                head.prev = newData;
            }

            newData.next = head;
            head = newData;

            // Добавляем таск в таблицу.
            map.put(taskId, newData);
        } //Добавить узел в начало списка.

        private void removeNode(int id) {
            Node nodeToRemove = map.get(id);
            Node prevNode = nodeToRemove.prev;
            Node nextNode = nodeToRemove.next;

            if (prevNode != null) {
                prevNode.next = nextNode;
            } else {
                if (nextNode != null) {
                    nextNode.prev = null;
                }
                head = nextNode;
            }

            if (nextNode != null) {
                nextNode.prev = prevNode;
            } else {
                if (prevNode != null) {
                    prevNode.next = null;
                }
                tail = prevNode;
            }

            map.remove(id);
        }

        private List<Task> getNodes() {
            List<Task> history = new ArrayList<>();

            if (tail == null) {
                return history;
            }

            Node node = head;

            while (node != null) {
                history.add(node.data);
                node = node.next;
            }

            return history;
        }
    }
}