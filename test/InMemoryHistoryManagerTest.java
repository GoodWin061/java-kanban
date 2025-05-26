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
        Task modifiedTask = new Task(task.getId(), "Задача 2", task.getDescription());
        Task taskFromHistory = historyManager.getHistory().get(0);
        assertEquals("Задача 1", taskFromHistory.getTitle());
    }

    @Test
    public void addTaskTest() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task(1, "Задача 1", "Описание 1");
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void removeTaskTest() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task(1, "Задача 1", "Описание 1");
        historyManager.add(task);
        historyManager.remove(1);
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    public void getHistoryTest() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Задача 1", "Описание 1");
        Task task2 = new Task(2, "Задача 2", "Описание 2");
        historyManager.add(task1);
        historyManager.add(task2);
        assertEquals(2, historyManager.getHistory().size());
    }
}