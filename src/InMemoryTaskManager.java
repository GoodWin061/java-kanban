import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager{
    private int idCounter;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, SubTask> subTasks;
    private final HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager = new InMemoryHistoryManager();
    private Epic epic;

    public InMemoryTaskManager() {
        this.idCounter = 0;
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
    }

    @Override
    public void addTask(String title, String description) {
        Task newTask = new Task(idCounter++, title, description);
        tasks.put(newTask.getId(), newTask);
    }

    @Override
    public void addSubTask(String title, String description, int epicId) {
        if (epicId == idCounter) {
            System.out.println("Подзадача не может быть назначена эпиком.");
            return;
        }
        Epic epic = epics.get(epicId);
        SubTask newSubTask = new SubTask(idCounter++, title, description, epicId);
        subTasks.put(newSubTask.getId(), newSubTask);
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
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            updateStatus(epic);
        }
    }

    public void updateEpic(int id, Epic epic) {
        if (epics.containsKey(id)) {
            epics.put(id, epic);
        }
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
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
            }
        }
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
}
