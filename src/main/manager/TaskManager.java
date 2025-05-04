package main.manager;

import main.task.*;

public interface TaskManager {
    void addTask(String title, String description);
    Task getIdTask(int id);
    void deleteTask(int id);

    void addSubTask(String title, String description, int epicId);
    SubTask getIdSubTask(int id);
    void deleteSubTask(int id);

    void addEpic(String title, String description);
    Epic getIdEpic(int id);
    void deleteEpic(int id);
}
