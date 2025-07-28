package ru.yandex.tracker.model;

import ru.yandex.tracker.service.TaskPriority;
import ru.yandex.tracker.service.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, int epicId,
                   TaskPriority taskPriority, LocalDateTime startTime, Duration duration) {
        super(name, description, taskPriority, startTime, duration);
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

    public TaskType getType() {
        return TaskType.SUBTASK;
    }

}
