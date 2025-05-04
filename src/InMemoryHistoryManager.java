import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        Task copy = new Task(task.getId(), task.getTitle(), task.getDescription());
        history.add(copy);
        if (history.size() > 10) {
            removeFirstElement();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    private void removeFirstElement() {
        if (!history.isEmpty()) {
            history.remove(0);
        }
    }
}
