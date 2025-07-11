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
        Task task1 = new Task("Задача 1", "Описание 1", Duration.ofHours(1), LocalDateTime.now());
        taskManager.addTask(task1);
        Task task = taskManager.getIdTask(0);
        assertNotNull(task);
        assertEquals("Задача 1", task.getTitle());
    }

    @Test
    void testAddAndGetEpic() {
        taskManager = createTaskManager();
        Epic epic1 = new Epic(1,"Эпик 1", "Описание эпика");
        taskManager.addEpic(epic1);
        Epic epic = taskManager.getIdEpic(0);
        assertNotNull(epic);
        assertEquals("Эпик 1", epic.getTitle());
    }

    @Test
    void testAddAndGetSubTask() {
        taskManager = createTaskManager();
        Epic epic1 = new Epic(0,"Эпик 1", "Описание эпика");
        SubTask subTask1 = new SubTask(1,"Подзадача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.now(), 0);
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1);
        SubTask subTask = taskManager.getIdSubTask(1);
        assertNotNull(subTask);
        assertEquals("Подзадача 1", subTask.getTitle());
        assertEquals(0, subTask.getEpicId());
    }

    @Test
    void shouldNotAllowOverlappingTasks() {
        taskManager = createTaskManager();
        Task task1 = new Task("Задача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 1, 10, 0));
        taskManager.addTask(task1);

        assertThrows(IllegalArgumentException.class, () -> {
            Task task2 = new Task("Задача 2", "Описание 2", Duration.ofHours(2), LocalDateTime.of(2025, 6, 1, 11, 0));
            taskManager.addTask(task2);
        });

        Task task3 = new Task("Задача 3", "Описание 3", Duration.ofHours(2), LocalDateTime.of(2025, 6, 1, 12, 0));
        taskManager.addTask(task3);

        assertEquals(2, taskManager.getAllTasks().size());
    }
}
