import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int idCounter;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, SubTask> subTasks;
    private final HashMap<Integer, Epic> epics;
    private Epic epic;

    public TaskManager() {
        this.idCounter = 0;
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        epics = new HashMap<>();
    }

    public void addTask(String title, String description) {
        Task newTask = new Task(idCounter++, title, description);
        tasks.put(newTask.getId(), newTask);
    }

    public void addSubTask(String title, String description, int epicId) {
        Epic epic = epics.get(epicId);
        SubTask newSubTask = new SubTask(idCounter++, title, description, epicId);
        subTasks.put(newSubTask.getId(), newSubTask);
        if (epic != null) {
            epic.addSubTask(newSubTask);
            epic.updateStatus();
        }
    }

    public void addEpic(String title, String description) {
        Epic newEpic = new Epic(idCounter++, title, description);
        epics.put(newEpic.getId(), newEpic);
    }

    public Task getIdTask(int id) {
        return tasks.get(id);
    }

    public SubTask getIdSubTask(int id) {
        return subTasks.get(id);
    }

    public Epic getIdEpic(int id) {
        return epics.get(id);
    }

    public void viewTasks() {
        for (Task task : tasks.values()) {
            System.out.println(task);
        }
    }

    public void viewSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            System.out.println(subTask);
        }
    }

    public void viewEpics() {
        for (Epic epic : epics.values()) {
            System.out.println(epic);
        }
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
    }

    public void updateTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        }
    }

    public void updateSubTask(int id, SubTask subTask) {
        if (subTasks.containsKey(id)) {
            subTasks.put(id, subTask);
        }
    }

    public void updateEpic(int id, Epic epic) {
        if (epics.containsKey(id)) {
            epics.put(id, epic);
        }
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.updateStatus();
            }
        }
    }

    public void deleteEpic(int id) {
        Epic epic =  epics.remove(id);
    }

    public List<SubTask> getSubTaskEpic(int id) {
        List<SubTask> subTasksEpic = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getEpicId() == id) {
                subTasksEpic.add(subTask);
            }
        }
        return subTasksEpic;
    }
}
