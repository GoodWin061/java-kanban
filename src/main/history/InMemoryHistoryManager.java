package main.history;

import java.util.ArrayList;
import java.util.List;
import main.task.Task;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        Task copy = new Task(task.getId(), task.getTitle(), task.getDescription());
        history.add(copy);
        if (history.size() > 10) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
