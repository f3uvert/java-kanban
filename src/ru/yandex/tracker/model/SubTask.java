package ru.yandex.tracker.model;

import ru.yandex.tracker.service.TaskPriority;

public class SubTask extends Task {
    private int epicId;
    SubTask(String name, String description, int uniqueId, TaskPriority taskPriority) {
        super(name, description, taskPriority);

    }

    public int getEpicId(){
        return epicId;
    }


}
