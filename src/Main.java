import main.manager.InMemoryTaskManager;
import main.manager.FileBackedTaskManager;
import main.task.Epic;
import main.task.SubTask;
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
        Task task1 = new Task(1,"Задача 1", "Описание 1", Duration.ofHours(2), LocalDateTime.of(2025, 6, 30, 14, 0));
        Task task2 = new Task(2,"Задача 2", "Описание 1", Duration.ofHours(1), LocalDateTime.of(2025, 6, 30, 18, 0));
        taskManager1.addTask(task1);
        taskManager1.addTask(task2);
        List<Task> tasks = taskManager1.getAllTasks();
        for (Task task : tasks) {
            System.out.println(task);
        }

        Epic epic1 = new Epic(3,"эпик1", "описание эпика1");
        taskManager1.addEpic(epic1);
        List<Epic> epics = taskManager1.getAllEpic();
        for (Epic epic : epics) {
            System.out.println(epic);
        }

        SubTask subTask1 = new SubTask(4, "подзадача1", "описание подзадачи1", Duration.ofHours(1), LocalDateTime.of(2025, 6, 28, 14, 0), 2);
        SubTask subTask2 = new SubTask(4, "подзадача2", "описание подзадачи2", Duration.ofHours(2), LocalDateTime.of(2025, 6, 29, 16, 0), 2);
        SubTask subTask3 = new SubTask(4, "подзадача3", "описание подзадачи3", Duration.ofHours(3), LocalDateTime.of(2025, 6, 30, 20, 0), 2);
        taskManager1.addSubTask(subTask1);
        taskManager1.addSubTask(subTask2);
        taskManager1.addSubTask(subTask3);

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
