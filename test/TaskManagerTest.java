import main.manager.TaskManager;
import main.task.Task;
import main.task.Epic;
import main.task.SubTask;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @Test
    void testAddAndGetTask() {
        taskManager = createTaskManager();
        taskManager.addTask("Задача 1", "Описание 1", Duration.ofHours(1), LocalDateTime.now());
        Task task = taskManager.getIdTask(0);
        assertNotNull(task);
        assertEquals("Задача 1", task.getTitle());
    }

    @Test
    void testAddAndGetEpic() {
        taskManager = createTaskManager();
        taskManager.addEpic("Эпик 1", "Описание эпика");
        Epic epic = taskManager.getIdEpic(0);
        assertNotNull(epic);
        assertEquals("Эпик 1", epic.getTitle());
    }

    @Test
    void testAddAndGetSubTask() {
        taskManager = createTaskManager();
        taskManager.addEpic("Эпик 1", "Описание эпика");
        taskManager.addSubTask("Подзадача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.now(), 0);
        SubTask subTask = taskManager.getIdSubTask(1);
        assertNotNull(subTask);
        assertEquals("Подзадача 1", subTask.getTitle());
        assertEquals(0, subTask.getEpicId());
    }

    @Test
    void shouldNotAllowOverlappingTasks() {
        taskManager = createTaskManager();
        taskManager.addTask("Задача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 1, 10, 0));

        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addTask("Задача 2", "Описание 2", Duration.ofHours(2), LocalDateTime.of(2025, 6, 1, 11, 0));
        });

        taskManager.addTask("Задача 3", "Описание 3", Duration.ofHours(2), LocalDateTime.of(2025, 6, 1, 12, 0));

        assertEquals(2, taskManager.getAllTasks().size());
    }
}
