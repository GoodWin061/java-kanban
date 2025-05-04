import main.manager.InMemoryTaskManager;
import main.manager.TaskManager;
import main.task.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import main.task.Epic;
import main.task.SubTask;
import main.task.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
        taskManager.addEpic("Эпик 1", "Описание эпика 1");
    }

    @Test
    void testCannotAddEpic() {
        taskManager.addSubTask("Подзадача 1", "Описание подзадачи 1", 0);
        assertNull(taskManager.getIdSubTask(0));
    }

    @Test
    public void testAddAndFindTaskById() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        manager.addTask("Задача 1", "Описание 1");
        Task task = manager.getIdTask(0);
        assertNotNull(task);
        assertEquals("Задача 1", task.getTitle());
        assertEquals("Описание 1", task.getDescription());
    }

    @Test
    public void testAddAndFindSubTaskById() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        manager.addEpic("Эпик 1", "Описание эпика 1");
        manager.addSubTask("Подзадача 1", "Описание подзадачи 1", 0);
        SubTask subTask = manager.getIdSubTask(1);
        assertNotNull(subTask);
        assertEquals("Подзадача 1", subTask.getTitle());
        assertEquals(0, subTask.getEpicId());
    }

    @Test
    public void testAddAndFindEpicById() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        manager.addEpic("Эпик 1", "Описание эпика 1");
        Epic epic = manager.getIdEpic(0);
        assertNotNull(epic);
        assertEquals("Эпик 1", epic.getTitle());
    }

    @Test
    void testTaskImmutabilityAdd() {
        Task task = new Task("Задача 1", "Описание 1");
        taskManager.addTask(task.getTitle(), task.getDescription());
        Task addedTask = taskManager.getIdTask(0);
        assertEquals(task.getTitle(), addedTask.getTitle());
        assertEquals(task.getDescription(), addedTask.getDescription());
        assertEquals(Status.NEW, addedTask.getStatus());
    }
}