package main.task;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private final List<SubTask> subTasks;

    public Epic(int id, String title, String description) {
        super(id, title, description);
        this.subTasks = new ArrayList<>();
    }

    public void addSubTask(SubTask subTask) {
        if (subTask.getId() != this.getId()) {
            subTasks.add(subTask);
        } else {
            System.out.println("Задача не может быть добавлена в качестве Эпика.");
        }
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public String toString() {
        return "task.Epic{" +
                ", id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
