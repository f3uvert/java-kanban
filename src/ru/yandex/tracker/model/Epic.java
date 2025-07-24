package ru.yandex.tracker.model;

import ru.yandex.tracker.service.TaskPriority;
import ru.yandex.tracker.service.TaskType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subTaskId = new ArrayList<>();
    private static Map<Integer, SubTask> subTaskMap;

    public Epic(String name, String description, TaskPriority taskPriority) {
        super(name, description, taskPriority, null, null);
    }

    public SubTask getSubTask(int id) {
        return subTaskMap != null ? subTaskMap.get(id) : null;
    }

    @Override
    public Duration getDuration() {
        if (subTaskId.isEmpty()) {
            return Duration.ZERO;
        }
        return subTaskId.stream()
                .map(this::getSubTask)
                .filter(Objects::nonNull)
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subTaskId.isEmpty()) {
            return null;
        }
        return subTaskId.stream()
                .map(this::getSubTask)
                .filter(Objects::nonNull)
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        if (subTaskId.isEmpty()) {
            return null;
        }
        return subTaskId.stream()
                .map(this::getSubTask)
                .filter(Objects::nonNull)
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }


    public int getSubTaskId(int id) {
        try {
            return subTaskId.get(id);
        } catch (IndexOutOfBoundsException error) {
            return -1;
        }
    }

    public void setSubTaskId(int id) {
        if (id != this.getUniqueId()) {
            subTaskId.add(id);
        }
    }

    public ArrayList<Integer> getAllSubTasks() {
        return subTaskId;
    }

    public void deleteSubTasks() {
        subTaskId.clear();
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }
}