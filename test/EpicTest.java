import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @org.junit.jupiter.api.Test
    void addSubTask() {
    }

    @org.junit.jupiter.api.Test
    void getSubTasks() {
    }

    @org.junit.jupiter.api.Test
    void testToString() {
    }

    @Test
    void testAddSubTask() {
        Epic epic = new Epic(1, "Задача Epic", "Описание Epic");

        SubTask subTask = new SubTask(1, "Подзадача", "Описание", 1);
        epic.addSubTask(subTask);
        assertTrue(epic.getSubTasks().isEmpty(), "Задача не может быть добавлена в качестве Эпика.");
    }
}