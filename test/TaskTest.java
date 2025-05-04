import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void getId() {
    }

    @Test
    void setId() {
    }

    @Test
    void getTitle() {
    }

    @Test
    void setTitle() {
    }

    @Test
    void getDescription() {
    }

    @Test
    void setDescription() {
    }

    @Test
    void getStatus() {
    }

    @Test
    void setStatus() {
    }

    @Test
    void testEquals() {
    }

    @Test
    void testHashCode() {
    }

    @Test
    void testToString() {
    }

    @Test
    public void testTaskEquality() {
        Task task1 = new Task(1, "Задача 1", "Описание 1");
        Task task2 = new Task(1, "Задача 2", "Описание 2");

        assertEquals(task1, task2, "Задачи равны");
    }
}