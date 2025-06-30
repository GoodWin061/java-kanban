import org.junit.jupiter.api.Test;
import main.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void testTaskEquality() {
        Task task1 = new Task(1, "Задача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0));
        Task task2 = new Task(1, "Задача 2", "Описание 2", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0));

        assertEquals(task1, task2, "Задачи равны");
    }
}