import main.manager.InMemoryTaskManager;
import main.manager.FileBackedTaskManager;
import main.task.Epic;
import main.task.Task;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;


public class Main {

    public static void main(String[] args) {
        int idCounter = 0;
        System.out.println("Поехали!");
        File file = new File("tasks.csv");
        FileBackedTaskManager taskManager1 = new FileBackedTaskManager(file);

        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        taskManager1.addTask("Задача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0));
        taskManager1.addTask("Задача 2", "Описание 1", Duration.ofHours(1), LocalDateTime.of(2025, 6, 30, 18, 0));
        List<Task> tasks = taskManager1.getAllTasks();
        for (Task task : tasks) {
            System.out.println(task);
        }

        taskManager1.addEpic("эпик1", "описание эпика1");
        List<Epic> epics = taskManager1.getAllEpic();
        for (Epic epic : epics) {
            System.out.println(epic);
        }

        taskManager1.addSubTask("подзадача1", "описание подзадачи1", Duration.ofHours(1), LocalDateTime.of(2025, 6, 28, 14, 0), 2);
        taskManager1.addSubTask("подзадача2", "описание подзадачи2", Duration.ofHours(2), LocalDateTime.of(2025, 6, 29, 16, 0), 2);
        taskManager1.addSubTask("подзадача3", "описание подзадачи3", Duration.ofHours(3), LocalDateTime.of(2025, 6, 30, 20, 0), 2);

        System.out.println("Абсолютный путь до файла: " + file.getAbsolutePath());

        if (file.exists()) {
            System.out.println("Файл '" + file.getName() + "' успешно создан. Содержимое:");
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                for (String line : lines) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("Ошибка при чтении файла: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Файл не был создан.");
        }
    }
}
