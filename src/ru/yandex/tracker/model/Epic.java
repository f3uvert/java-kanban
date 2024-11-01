package ru.yandex.tracker.model;


import ru.yandex.tracker.service.TaskPriority;

import java.util.ArrayList;


public class Epic extends Task {

    private ArrayList<Integer> subTaskId = new ArrayList<>();

    public Epic(String name, String description, TaskPriority taskPriority) {
        super(name, description, taskPriority);
    }

    public int getSubTaskId(int id) {
        return subTaskId.get(id);
    }

    public void setSubTaskId(int id) {
        subTaskId.add(id);
    }

    public ArrayList<Integer> getAllSubTasks() {
        return subTaskId;
    }

    public void deleteSubTasks() {
        subTaskId.clear();
    }


}
