package main.history;

import java.util.ArrayList;
import java.util.List;

import main.task.Task;

import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> mapNode = new HashMap<>();
    private Node head;
    private Node tail;

    private final static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task) {
            this.task = task;
        }
    }

    @Override
    public void add(Task task) {
        removeNodeIfExists(task.getId());
        Node newNode = new Node(task);
        linkLast(newNode);
        mapNode.put(task.getId(), newNode);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node node = tail;
        while (node != null) {
            result.add(node.task);
            node = node.prev;
        }
        return result;
    }

    @Override
    public void remove(int id) {
        removeNodeIfExists(id);
    }

    private void removeNodeIfExists(int id) {
        Node node = mapNode.get(id);
        if (node != null) {
            removeNode(node);
            mapNode.remove(id);
        }
    }

    private void removeNode(Node node) {
        if (node == null) return;
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        node.prev = null;
        node.next = null;
    }

    private void linkLast(Node node) {
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

}
