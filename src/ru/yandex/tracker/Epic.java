package ru.yandex.tracker;


import java.lang.reflect.Array;
import java.util.ArrayList;


public class Epic extends Task {

    private ArrayList<Integer> subTaskId = new ArrayList<>();

    Epic(String name, String description, int uniqueId, TaskPriority taskPriority) {
        super(name, description, uniqueId, taskPriority);
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
