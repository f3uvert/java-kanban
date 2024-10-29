package ru.yandex.tracker;

public class Task {
    private String name;
    private String description;
    private int uniqueId;
    private TaskPriority taskPriority;


    Task(String name, String description, int uniqueId, TaskPriority taskPriority) {
        this.name = name;
        this.description = description;
        this.taskPriority = taskPriority;
        this.uniqueId = uniqueId;
    }

    /*@Override
    public String toString() {
        return name + " - " + description + " ; " + taskPriority;
    }*/

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);// Что нужно именно переопределить?
    }

    @Override
    public int hashCode() {
        return super.hashCode();// Что нужно именно переопределить?
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }

    public TaskPriority getTaskPriority() {
        return taskPriority;
    }

    public int getUniqueId() {
        return uniqueId;
    }
}
