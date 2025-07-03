import main.manager.InMemoryTaskManager;
import main.manager.TaskManager;
import main.task.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import main.task.Epic;
import main.task.SubTask;
import main.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
        taskManager.addEpic("Эпик 1", "Описание эпика 1");
    }

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void testCannotAddEpic() {
        taskManager.addSubTask("Задача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0), 0);
        assertNull(taskManager.getIdSubTask(0));
    }

    @Test
    public void testAddAndFindTaskById() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        manager.addTask("Задача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0));
        Task task = manager.getIdTask(0);
        assertNotNull(task);
        assertEquals("Задача 1", task.getTitle());
        assertEquals("Описание 1", task.getDescription());
    }

    @Test
    public void testAddAndFindSubTaskById() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        manager.addEpic("Эпик 1", "Описание эпика 1");
        manager.addSubTask("Подзадача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0), 0);
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
        Task task = new Task("Задача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0));
        taskManager.addTask(task.getTitle(), task.getDescription(), task.getDuration(), task.getStartTime());
        List<Task> tasks = ((InMemoryTaskManager) taskManager).getAllTasks();
        assertFalse(tasks.isEmpty(), "Список задач не должен быть пустым");
        Task addedTask = tasks.get(tasks.size() - 1);
        assertEquals(task.getTitle(), addedTask.getTitle());
        assertEquals(task.getDescription(), addedTask.getDescription());
        assertEquals(Status.NEW, addedTask.getStatus());
    }

    @Test
    void updateStatus_AllSubtasksNew_EpicStatusNew() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic(100, "Эпик 1", "Описание 1");
        SubTask sub1 = new SubTask(101, "Подзадача 1", "Описание 1", null, null, 100);
        SubTask sub2 = new SubTask(102, "Подзадача 2", "Описание 2", null, null, 100);

        sub1.setStatus(Status.NEW);
        sub2.setStatus(Status.NEW);

        epic.addSubTask(sub1);
        epic.addSubTask(sub2);

        manager.updateStatus(epic);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void updateStatus_AllSubtasksDone_EpicStatusDone() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic(200, "Эпик 1", "Описание 1");
        SubTask sub1 = new SubTask(201, "Подзадача 1", "Описание 1", null, null, 200);
        SubTask sub2 = new SubTask(202, "Подзадача 2", "Описание 2", null, null, 200);

        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.DONE);

        epic.addSubTask(sub1);
        epic.addSubTask(sub2);

        manager.updateStatus(epic);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void updateStatus_SubtasksNewAndDone_EpicStatusInProgress() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic(300, "Эпик 1", "Описание 1");
        SubTask sub1 = new SubTask(301, "Подзадача 1", "Описание 1", null, null, 300);
        SubTask sub2 = new SubTask(302, "Подзадача 2", "Описание 2", null, null, 300);

        sub1.setStatus(Status.NEW);
        sub2.setStatus(Status.DONE);

        epic.addSubTask(sub1);
        epic.addSubTask(sub2);

        manager.updateStatus(epic);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void updateStatus_SubtasksWithInProgress_EpicStatusInProgress() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic(400, "Эпик 1", "Описание 1");
        SubTask sub1 = new SubTask(401, "Подзадача 1", "Описание 1", null, null, 400);
        SubTask sub2 = new SubTask(402, "Подзадача 2", "Описание 2", null, null, 400);
        SubTask sub3 = new SubTask(403, "Подзадача 3", "Описание 3", null, null, 400);

        sub1.setStatus(Status.NEW);
        sub2.setStatus(Status.IN_PROGRESS);
        sub3.setStatus(Status.DONE);

        epic.addSubTask(sub1);
        epic.addSubTask(sub2);
        epic.addSubTask(sub3);

        manager.updateStatus(epic);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}