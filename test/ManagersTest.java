import main.manager.InMemoryTaskManager;
import main.manager.Managers;
import main.manager.TaskManager;
import main.history.HistoryManager;
import main.history.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    public void testGetDefaultTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Manager.TaskManager должен быть инициализирован");
        assertInstanceOf(InMemoryTaskManager.class, taskManager, "Manager.TaskManager должен быть экземпляром Manager.InMemoryTaskManager.");
    }

    @Test
    public void testGetDefaultHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "history.HistoryManager должен быть инициализирован");
        assertInstanceOf(InMemoryHistoryManager.class, historyManager, "history.HistoryManager должен быть экземпляром history.InMemoryHistoryManager.");
    }
}