import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void getEpicId() {
    }

    @Test
    void testToString() {
    }

    @Test
    public void testSubTaskEquality() {
        SubTask subTask1 = new SubTask(1, "Подзадача 1", "Описание 1", 1);
        SubTask subTask2 = new SubTask(1, "Подзадача 2", "Описание 2", 1);

        assertEquals(subTask1, subTask2, "Подзадачи равны");
    }

    @Test
    public void testCannotSetEpic() {
        SubTask task = new SubTask(1, "Задача 1", "Описание 1", 1);
        Epic epic = new Epic(1, "Задача 2", "Описание 2");

        assertEquals(task, epic, "Задачи равны");
    }
}