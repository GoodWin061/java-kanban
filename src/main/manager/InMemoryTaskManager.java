package main.manager;

import main.history.HistoryManager;
import main.task.*;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    private int idCounter;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, SubTask> subTasks;
    private final HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private Epic epic;
    private final NavigableSet<Task> prioritizedTasks = new TreeSet<>(Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getId));

    public InMemoryTaskManager() {
        this.idCounter = 0;
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
    }

    protected NavigableSet<Task> getPrioritizedTasksSet() {
        return prioritizedTasks;
    }

    @Override
    public void addTask(String title, String description, Duration duration, LocalDateTime startTime) {
        Task newTask = new Task(idCounter++, title, description, duration, startTime);

        if (isIntersectingWithAny(newTask)) {
            throw new IllegalArgumentException("Новая задача пересекается по времени с другой задачей");
        }

        tasks.put(newTask.getId(), newTask);
        prioritizedTasks.add(newTask);
    }

    @Override
    public void addSubTask(String title, String description, Duration duration, LocalDateTime startTime, int epicId) {
        if (epicId == idCounter) {
            System.out.println("Подзадача не может быть назначена эпиком.");
            return;
        }
        Epic epic = epics.get(epicId);
        SubTask newSubTask = new SubTask(idCounter++, title, description, duration, startTime, epicId);

        if (isIntersectingWithAny(newSubTask)) {
            throw new IllegalArgumentException("Новая подзадача пересекается по времени с другой задачей");
        }

        subTasks.put(newSubTask.getId(), newSubTask);
        prioritizedTasks.add(newSubTask);
        if (epic != null) {
            epic.addSubTask(newSubTask);
            updateStatus(epic);
        }
    }

    @Override
    public void addEpic(String title, String description) {
        Epic newEpic = new Epic(idCounter++, title, description);
        epics.put(newEpic.getId(), newEpic);
    }

    @Override
    public Task getIdTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public SubTask getIdSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public Epic getIdEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<SubTask> getAllSubTask() {
        return new ArrayList<>(subTasks.values());
    }

    public List<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllTask() {
        tasks.clear();
        subTasks.clear();
        prioritizedTasks.clear();
    }

    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            Task oldTask = tasks.get(id);
            prioritizedTasks.remove(oldTask);

            if (task.getStartTime() != null && task.getDuration() != null) {
                if (isIntersectingWithAny(task)) {
                    prioritizedTasks.add(oldTask);
                    throw new IllegalArgumentException("Обновляемая задача пересекается по времени с другой задачей");
                }
            }

            tasks.put(id, task);
            prioritizedTasks.add(task);
        }
    }

    public void updateSubTask(int id, SubTask subTask) {
        if (subTasks.containsKey(id)) {
            SubTask oldSubTask = subTasks.get(id);
            prioritizedTasks.remove(oldSubTask);

            if (subTask.getStartTime() != null && subTask.getDuration() != null) {
                if (isIntersectingWithAny(subTask)) {
                    prioritizedTasks.add(oldSubTask);
                    throw new IllegalArgumentException("Обновляемая подзадача пересекается по времени с другой задачей");
                }
            }

            subTasks.put(id, subTask);
            prioritizedTasks.add(subTask);

            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                updateStatus(epic);
            }
        }
    }

    public void updateEpic(int id, Epic epic) {
        if (epics.containsKey(id)) {
            epics.put(id, epic);
        }
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
            prioritizedTasks.remove(subTask);
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                updateStatus(epic);
            }
        }
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic =  epics.remove(id);
        if (epic != null) {
            List<SubTask> subTasks = epic.getSubTasks();
            for (SubTask subTask : subTasks) {
                deleteSubTask(subTask.getId());
                prioritizedTasks.remove(subTask);
            }
        }
    }

    public List<SubTask> getSubTaskEpic(int id) {
        return subTasks.values().stream()
                .filter(subTask -> subTask.getEpicId() == id)
                .collect(Collectors.toList());
    }

    public void updateStatus(Epic epic) {
        boolean allDone = true;
        boolean allNew = true;

        List<SubTask> subTasksEpic = epic.getSubTasks();

        for (SubTask subTask : subTasksEpic) {
            if (subTask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subTask.getStatus() != Status.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public int getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public boolean isIntersecting(Task task1, Task task2) {
        if (task1.getStartTime() == null || task1.getDuration() == null ||
                task2.getStartTime() == null || task2.getDuration() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = start1.plus(task1.getDuration());

        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = start2.plus(task2.getDuration());

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    public boolean isIntersectingWithAny(Task newTask) {
        return getPrioritizedTasksSet().stream()
                .filter(task -> task.getId() != newTask.getId())
                .anyMatch(existingTask -> isIntersecting(existingTask, newTask));
    }
}
