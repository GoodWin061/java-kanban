import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    public void testGetDefaultTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager должен быть инициализирован");
        assertInstanceOf(InMemoryTaskManager.class, taskManager, "TaskManager должен быть экземпляром InMemoryTaskManager.");
    }

    @Test
    public void testGetDefaultHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager должен быть инициализирован");
        assertInstanceOf(InMemoryHistoryManager.class, historyManager, "HistoryManager должен быть экземпляром InMemoryHistoryManager.");
    }
}