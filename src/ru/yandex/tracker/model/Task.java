package ru.yandex.tracker.model;

import ru.yandex.tracker.service.TaskPriority;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int uniqueId;
    private TaskPriority taskPriority;


    public Task(String name, String description, TaskPriority taskPriority) {
        this.name = name;
        this.description = description;
        this.taskPriority = taskPriority;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getUniqueId() {
        return uniqueId;
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

    public TaskPriority getTaskPriority() {
        return taskPriority;
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
}
