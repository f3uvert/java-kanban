package ru.yandex.tracker.model;

import ru.yandex.tracker.service.TaskPriority;
import ru.yandex.tracker.service.TaskType;

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
        if (epicId != this.getUniqueId()) {
            this.epicId = epicId;
        }
    }

    public TaskType getType(){
        return TaskType.SUBTASK;
    }

}
