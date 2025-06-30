import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.task.Task;
import main.history.HistoryManager;
import main.history.InMemoryHistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();

        task1 = new Task(1, "Задача 1", "Описание 1", Duration.ofMinutes(30), LocalDateTime.of(2025, 10, 10, 10, 0));
        task2 = new Task(2, "Задача 2", "Описание 2", Duration.ofMinutes(45), LocalDateTime.of(2025, 10, 10, 11, 0));
        task3 = new Task(3, "Задача 3", "Описание 3", Duration.ofMinutes(60), LocalDateTime.of(2025, 10, 10, 12, 0));
    }

    @Test
    public void testGetHistoryEmpty() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    public void testAddAndGetHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task1.getId(), history.get(1).getId());
        assertEquals(task2.getId(), history.get(0).getId());
    }

    @Test
    public void testAddDuplicateTasks() {
        historyManager.add(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    public void testRemoveTaskAtStart() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(task1));
        assertTrue(history.contains(task2));
    }

    @Test
    public void testRemoveTaskInMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(task2));
        assertTrue(history.contains(task1));
        assertTrue(history.contains(task3));
    }

    @Test
    public void testRemoveTaskAtEnd() {
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(task2));
        assertTrue(history.contains(task1));
    }
}
