import org.junit.jupiter.api.Test;
import main.task.Task;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    public void testTaskEquality() {
        Task task1 = new Task(1, "Задача 1", "Описание 1");
        Task task2 = new Task(1, "Задача 2", "Описание 2");

        assertEquals(task1, task2, "Задачи равны");
    }
}