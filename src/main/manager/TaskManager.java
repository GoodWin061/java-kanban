package main.manager;

import main.task.*;

import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    void addSubTask(SubTask subTask);

    void addEpic(Epic epic);

    Task getIdTask(int id);

    void deleteTask(int id);

    SubTask getIdSubTask(Integer id);

    void deleteSubTask(int id);

    Epic getIdEpic(int id);

    void deleteEpic(int id);

    void updateTask(int id, Task task);

    void updateSubTask(int id, SubTask subTask);

    List<Task> getPrioritizedTasks();

    List<Task> getAllTasks();

    List<SubTask> getAllSubTask();

    List<Epic> getAllEpic();

    List<SubTask> getSubTaskEpic(int id);
}
