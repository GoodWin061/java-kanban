package main.manager;

import main.history.HistoryManager;
import main.task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.*;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    private void save() {
        try (OutputStream os = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) { //запись кодировки UTF_8 с BOM

            os.write(0xEF);
            os.write(0xBB);
            os.write(0xBF);

            writer.write("id,type,title,status,description,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Epic epic : getAllEpic()) {
                writer.write(toString(epic) + "\n");
            }

            for (SubTask subTask : getAllSubTask()) {
                writer.write(toString(subTask) + "\n");
            }

        } catch (IOException e) {
            System.out.println("Ошибка при сохранении: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        try {
            if (!file.exists()) {
                System.out.println("Файл задач не найден");
                return;
            }

            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

            getTasks().clear();
            getSubTasks().clear();
            getEpics().clear();

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(",", -1);

                int id = Integer.parseInt(parts[0]);
                String type = parts[1];
                String title = parts[2];
                Status status = Status.valueOf(parts[3]);
                String description = parts[4];
                String epicIdStr = parts.length > 5 ? parts[5] : "";

                switch (type) {
                    case "TASK":
                        Task task = new Task(id, title, description);
                        task.setStatus(status);
                        getTasks().put(id, task);
                        break;

                    case "EPIC":
                        Epic epic = new Epic(id, title, description);
                        epic.setStatus(status);
                        getEpics().put(id, epic);
                        break;

                    case "SUBTASK":
                        int epicId = Integer.parseInt(epicIdStr);
                        SubTask subTask = new SubTask(id, title, description, epicId);
                        subTask.setStatus(status);
                        getSubTasks().put(id, subTask);
                        Epic epicForSub = getEpics().get(epicId);
                        if (epicForSub != null) {
                            epicForSub.addSubTask(subTask);
                        }
                        break;

                    default:
                        System.out.println("Неизвестный тип задачи: " + type);
                }

                if (id >= getIdCounter()) {
                    setIdCounter(id + 1);
                }
            }

            for (Epic epic : getEpics().values()) {
                updateStatus(epic);
            }

            System.out.println("Загрузка из файла завершена.");

        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
    }

    private String toString(Task task) {
        String type = task.getType().toString();
        String epicId = "";

        if (task instanceof SubTask) {
            epicId = String.valueOf(((SubTask) task).getEpicId());
        }

        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                type,
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                epicId);
    }


    @Override
    public void addTask(String title, String description) {
        super.addTask(title, description);
        save();
    }

    @Override
    public void addSubTask(String title, String description, int epicId) {
        super.addSubTask(title, description, epicId);
        save();
    }

    @Override
    public void addEpic(String title, String description) {
        super.addEpic(title, description);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void updateTask(int id, Task task) {
        super.updateTask(id, task);
        save();
    }

    @Override
    public void updateSubTask(int id, SubTask subTask) {
        super.updateSubTask(id, subTask);
        save();
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        super.updateEpic(id, epic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }
}
