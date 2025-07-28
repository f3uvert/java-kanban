package ru.yandex.tracker.model;
import ru.yandex.tracker.service.TaskPriority;
import ru.yandex.tracker.service.TaskType;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int uniqueId;
    private TaskPriority taskPriority;
    private Duration duration;
    private LocalDateTime startTime;


    public Task(String name, String description, TaskPriority taskPriority,LocalDateTime startTime,Duration duration) {
        this.name = name;
        this.description = description;
        this.taskPriority = taskPriority;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setUniqueId(int uniqueId) {

        this.uniqueId = uniqueId;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public TaskPriority getTaskPriority() {
        return taskPriority;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return uniqueId == task.uniqueId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }


    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", uniqueId=" + uniqueId +
                ", taskPriority=" + taskPriority +
                '}';
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

}
