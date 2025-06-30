import org.junit.jupiter.api.Test;
import main.task.Epic;
import main.task.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void testAddSubTask() {
        Epic epic = new Epic(1, "Задача task.Epic", "Описание task.Epic");

        SubTask subTask = new SubTask(1, "Задача1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0), 1);
        epic.addSubTask(subTask);
        assertTrue(epic.getSubTasks().isEmpty(), "Задача не может быть добавлена в качестве Эпика.");
    }
}