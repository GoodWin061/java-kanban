import org.junit.jupiter.api.Test;
import main.task.Epic;
import main.task.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    public void testSubTaskEquality() {
        SubTask subTask1 = new SubTask(1, "Подзадача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0), 1);
        SubTask subTask2 = new SubTask(1, "Подзадача 2", "Описание 2", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0), 1);

        assertEquals(subTask1, subTask2, "Подзадачи равны");
    }

    @Test
    public void testCannotSetEpic() {
        SubTask task = new SubTask(1, "Подзадача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0), 1);
        Epic epic = new Epic(1, "Задача 2", "Описание 2");

        assertNotEquals(task, epic, "Подзадача и эпик не равны");
    }
}