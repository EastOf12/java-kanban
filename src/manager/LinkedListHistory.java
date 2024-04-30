package manager;

import java.util.HashMap;
import java.util.Map;

class LinkedListHistory<T> {
    Node<T> head;
    Node<T> tail;
    Map<Integer, Node> map = new HashMap<>();
}
