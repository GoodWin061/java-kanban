package main.task;

import java.util.List;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task {
    private final List<SubTask> subTasks;

    public Epic(int id, String title, String description) {
        super(id, title, description, Duration.ZERO, null);
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
    public Duration getDuration() {
        Duration totalDuration = Duration.ZERO;
        for (SubTask subTask : subTasks) {
            Duration subDuration = subTask.getDuration();
            if (subDuration != null) {
                totalDuration = totalDuration.plus(subDuration);
            }
        }
        return totalDuration;
    }

    @Override
    public LocalDateTime getStartTime() {
        LocalDateTime earliest = null;
        for (SubTask subTask : subTasks) {
            LocalDateTime subStart = subTask.getStartTime();
            if (subStart != null) {
                if (earliest == null || subStart.isBefore(earliest)) {
                    earliest = subStart;
                }
            }
        }
        return earliest;
    }

    @Override
    public LocalDateTime getEndTime() {
        LocalDateTime latest = null;
        for (SubTask subTask : subTasks) {
            LocalDateTime subEnd = subTask.getEndTime();
            if (subEnd != null) {
                if (latest == null || subEnd.isAfter(latest)) {
                    latest = subEnd;
                }
            }
        }
        return latest;
    }

    @Override
    public String toString() {
        return "task.Epic{" +
                ", id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                ", subTasksCount=" + subTasks.size() +
                '}';
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }
}
