package ru.yandex.tracker.model;

import ru.yandex.tracker.service.TaskPriority;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, int epicId, TaskPriority taskPriority) {
        super(name, description, taskPriority);
        this.epicId = epicId;

    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }


}
