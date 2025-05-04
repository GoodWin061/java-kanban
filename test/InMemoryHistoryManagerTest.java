import main.history.HistoryManager;
import main.history.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;
import main.task.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    public void historyTaskIsNoTModifiedAfterAdding() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task(0, "Задача 1", "Описание 1");
        historyManager.add(task);
        task.setTitle("Задача 2");
        Task taskFromHistory = historyManager.getHistory().get(0);;
        assertEquals("Задача 1", taskFromHistory.getTitle());
    }
}