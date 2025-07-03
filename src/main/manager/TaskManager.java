package main.manager;

import main.task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskManager {
    void addTask(String title, String description, Duration duration, LocalDateTime startTime);

    Task getIdTask(int id);

    void deleteTask(int id);

    void addSubTask(String title, String description, Duration duration, LocalDateTime startTime, int epicId);

    SubTask getIdSubTask(int id);

    void deleteSubTask(int id);

    void addEpic(String title, String description);

    Epic getIdEpic(int id);

    void deleteEpic(int id);

    List<Task> getPrioritizedTasks();

    List<Task> getAllTasks();
}
