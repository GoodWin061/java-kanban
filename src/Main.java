import main.manager.InMemoryTaskManager;
import main.task.Epic;
import main.task.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        int idCounter = 0;
        System.out.println("Поехали!");

        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager.addTask("Задача1", "Описание 1");
        taskManager.addTask("Задача2", "Описание 1");
        List<Task> tasks = taskManager.getAllTasks();
        for (Task task : tasks) {
            System.out.println(task);
        }

        taskManager.addEpic("эпик1", "описание эпика1");
        List<Epic> epics = taskManager.getAllEpic();
        for (Epic epic : epics) {
            System.out.println(epic);
        }

        taskManager.addSubTask("подзадача1", "описание подзадачи1", 2);
        taskManager.addSubTask("подзадача2", "описание подзадачи2", 2);
        taskManager.addSubTask("подзадача3", "описание подзадачи3", 2);
    }
}
