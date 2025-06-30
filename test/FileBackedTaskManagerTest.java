import main.manager.FileBackedTaskManager;
import main.task.Task;
import main.task.Epic;
import main.task.SubTask;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test_tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void deleteTestFile() {
        try {
            List<String> lines = Files.readAllLines(tempFile.toPath());
            for (String line : lines) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }
        tempFile.delete();
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(tempFile);
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        FileBackedTaskManager loaded = new FileBackedTaskManager(tempFile);
        loaded.loadFromFile();

        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpic().isEmpty());
        assertTrue(loaded.getAllSubTask().isEmpty());
    }

    @Test
    void testSaveMultipleTasks() {
        manager.addTask("Задача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0));
        manager.addTask("Задача 2", "Описание 2", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0));

        List<Task> tasks = manager.getAllTasks();
        assertEquals(2, tasks.size());
        assertEquals("Задача 1", tasks.get(0).getTitle());
    }

    @Test
    void testLoadMultipleTasks() {
        manager.addTask("Задача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0));
        manager.addTask("Задача 2", "Описание 2", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 16, 0));

        FileBackedTaskManager loaded = new FileBackedTaskManager(tempFile);
        loaded.loadFromFile();

        List<Task> tasks = loaded.getAllTasks();

        assertEquals(2, tasks.size());
        assertEquals("Задача 1", tasks.get(0).getTitle());
        assertEquals("Задача 2", tasks.get(1).getTitle());
    }

    @Test
    void shouldSaveAndLoadTasksCorrectly() {
        manager.addTask("Задача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 29, 14, 0));
        manager.addEpic("Эпик 1", "Описание эпика");
        manager.addSubTask("Подзадача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0), 1);

        FileBackedTaskManager loaded = new FileBackedTaskManager(tempFile);
        loaded.loadFromFile();

        List<Task> tasks = loaded.getAllTasks();
        List<Epic> epics = loaded.getAllEpic();
        List<SubTask> subTasks = loaded.getAllSubTask();

        assertEquals(1, tasks.size());
        assertEquals(1, epics.size());
        assertEquals(1, subTasks.size());

        assertEquals("Задача 1", tasks.get(0).getTitle());
        assertEquals("Эпик 1", epics.get(0).getTitle());
        assertEquals("Подзадача 1", subTasks.get(0).getTitle());
        assertEquals(1, subTasks.get(0).getEpicId());
    }
}