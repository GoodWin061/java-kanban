import org.junit.jupiter.api.Test;
import main.task.Epic;
import main.task.SubTask;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

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

        assertNotEquals(task, epic, "Подзадача и эпик не равны");
    }
}