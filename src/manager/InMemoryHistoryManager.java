package manager;

import tasks.Epic;
import tasks.Task;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class InMemoryHistoryManager implements HistoryManager{

    private final LinkedListHistory<Task> linkedListHistoryTask;
    private final HashMap<Integer, LinkedListHistory.Node> hashMapHistory;

    public InMemoryHistoryManager() {
        linkedListHistoryTask = new LinkedListHistory<>();
        hashMapHistory = new HashMap<>();
    }

    @Override
    public void addHistory(Task task) {
        if(task == null) {
            return;
        }

        //Получаем id таска.
        final int taskId = task.getIdTask();

        //Проверяем, смотрели ли его ранее.
        if(hashMapHistory.containsKey(taskId)) {
            LinkedListHistory.Node node = hashMapHistory.get(taskId);
            linkedListHistoryTask.removeNode(node);
            linkedListHistoryTask.size --;
        }

        linkedListHistoryTask.addLastTask(task);
        hashMapHistory.put(task.getIdTask(), linkedListHistoryTask.getLast());

    } // Добаввляем полученную задачу в историю.

    @Override
    public LinkedListHistory<Task> getHistory() {
        return linkedListHistoryTask;
    }



    @Override
    public void remove(int id) {
        if(hashMapHistory.containsKey(id)) {
            LinkedListHistory.Node node = hashMapHistory.get(id);

            if (node.getData()  instanceof Epic) {
                Epic epic = (Epic) node.getData();
                for (int subtaskID : epic.getSubtasks()) {
                    remove(subtaskID);
                }
            }
            linkedListHistoryTask.removeNode(node);
            hashMapHistory.remove(id, node);
            linkedListHistoryTask.size --;


        }
    } //Удаляет задачу по ее id из linkedListHistoryTask и hashMapHistory

    public class LinkedListHistory<T> {
        private Node<T> head;
        private Node<T> tail;
        private int size = 0;

        public LinkedListHistory() {
        }

        class Node <E> {

            public E data;
            public Node<E> next;
            public Node<E> prev;

            public Node<E> getNext() {
                return next;
            }

            public E getData() {
                return data;
            }

            public Node<E> getPrev() {
                return prev;
            }

            public Node(Node<E> prev, E data, Node<E> next) {
                this.data = data;
                this.next = next;
                this.prev = prev;
            }

            public void setData(E data) {
                this.data = data;
            }

            public void setNext(Node next) {
                this.next = next;
            }
        }


        public void addLastTask(T element) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(null, element, oldTail);
            tail = newNode;
            if (oldTail == null)
                head = newNode;
            else
                oldTail.prev = newNode;
            size++;
        }

        public Node getLast() {
            final Node<T> curTail = tail;
            if (curTail == null)
                throw new NoSuchElementException();
            return tail;
        }

        public int size() {
            return this.size;
        }

        public Node getFirst() {
            final Node<T> curHead = head;
            if (curHead == null)
                throw new NoSuchElementException();
            return head;
        }

        public void removeNode(Node node) {
            if (node == null || node.getNext() == null) {
                return;
            }

            Node nextNode = node.getNext();
            node.setData(nextNode.getData());
            node.setNext(nextNode.getNext());
        } //Удаляем узел из списка.
    }

    public HashMap<Integer, LinkedListHistory.Node> getHashMapHistory() {
        return hashMapHistory;
    }
}

