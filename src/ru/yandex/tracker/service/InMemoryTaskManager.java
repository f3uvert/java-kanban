package ru.yandex.tracker.service;

import ru.yandex.tracker.model.Epic;
import ru.yandex.tracker.model.SubTask;
import ru.yandex.tracker.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    private final Map<Integer, Task> commonTask = new HashMap<>();
    private final Map<Integer, Epic> epicTask = new HashMap<>();
    private final Map<Integer, SubTask> subTaskMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())));


    public boolean isTasksIntersect(Task task1, Task task2) { /// Пересекаются ли 2 задачи во время выполнения
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !(end1.isBefore(start2) || end2.isBefore(start1));
    }

    public boolean hasTaskIntersections(Task newTask) { /// Пересекается ли новая задача с любыми существующими задачами
        if (newTask.getStartTime() == null) {
            return false;
        }

        return getPrioritizedTasks().stream()
                .anyMatch(existingTask -> isTasksIntersect(newTask, existingTask));
    }

    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks);
    }

    @Override
    public List<Task> getAllTasks() {
        return List.of();
    }

    @Override
    public List<Epic> getAllEpics() {
        return List.of();
    }

    @Override
    public List<SubTask> getAllSubtasks() {
        return List.of();
    }

    @Override
    public List<Task> getTasks() {

        return new ArrayList<>(this.commonTask.values());
    }

    @Override
    public List<SubTask> getSubtasks() {
        return new ArrayList<>(this.subTaskMap.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(this.epicTask.values());
    }

    @Override
    public List<SubTask> getEpicSubtasks(int epicId) {
        Epic epic = epicTask.get(epicId);
        if (epic == null) {
            return List.of();
        }
        return epic.getAllSubTasks().stream()
                .map(subTaskMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Task getTask(int id) {
        historyManager.add((Task) commonTask.get(id));
        return commonTask.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        historyManager.add((Task) subTaskMap.get(id));
        return subTaskMap.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epicTask.get(id));
        return epicTask.get(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public int addNewTask(Task task) {
        if (hasTaskIntersections(task)) {
            throw new IllegalStateException("Задача пересекается по времени с существующей");
        }
        final int id = counter++;
        task.setUniqueId(id);
        commonTask.put(id, task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        final int id = counter++;
        epic.setUniqueId(id);
        epicTask.put(id, epic);
        return id;
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        if (hasTaskIntersections(subTask)) {
            throw new IllegalStateException("Подзадача пересекается по времени");
        }
        final int id = counter++;
        subTask.setUniqueId(id);

        subTaskMap.put(id, subTask);
        epicTask.get(subTask.getEpicId()).setSubTaskId(id);
        if (subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
        }
        Epic epic = epicTask.get(subTask.getEpicId());
        epic.setSubTaskId(id);
        updateEpicStatus(epic);
        updateEpicTime(epic);
        return id;
    }

    @Override
    public void updateTask(Task task) {
        if (hasTaskIntersections(task)) {
            throw new IllegalStateException("Задача пересекается по времени с существующей");
        }
        commonTask.put(task.getUniqueId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicTask.put(epic.getUniqueId(), epic);
    }

    public void updateEpicTime(Epic epic) {
        List<SubTask> subTasks = getEpicSubtasks(epic.getUniqueId());

        if (subTasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }

        LocalDateTime start = subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        Duration duration = subTasks.stream()
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setStartTime(start);
        epic.setDuration(duration);

        prioritizedTasks.removeIf(t -> t.equals(epic.getUniqueId()));
        if (start != null) {
            prioritizedTasks.add(epic);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (hasTaskIntersections(subTask)) {
            throw new IllegalStateException("Подзадача пересекается по времени");
        }

        subTaskMap.put(subTask.getUniqueId(), subTask);

        updateEpicStatus(epicTask.get(subTask.getEpicId()));
        updateEpicTime(epicTask.get(subTask.getEpicId()));
    }

    @Override
    public void deleteTask(int id) {
        commonTask.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic epic = epicTask.remove(epicId);
        if (epic != null) {
            for (Integer subTaskId : epic.getAllSubTasks()) {
                subTaskMap.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
            historyManager.remove(epicId);
        }
    }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTask = subTaskMap.remove(id);
        if (subTask != null) {
            Epic epic = epicTask.get(subTask.getEpicId());
            if (epic != null) {
                epic.getAllSubTasks().remove((Integer) id);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteTasks() {

        for (Integer taskId : commonTask.keySet()) {
            historyManager.remove(taskId);
        }
        commonTask.clear();
    }

    @Override
    public void deleteSubTasks() {

        for (Integer subTaskId : subTaskMap.keySet()) {
            historyManager.remove(subTaskId);
        }
        subTaskMap.clear();

        for (Epic epic : epicTask.values()) {
            epic.deleteSubTasks();
        }
    }

    @Override
    public void deleteEpics() {

        for (Epic epic : epicTask.values()) {
            for (Integer subTaskId : epic.getAllSubTasks()) {
                historyManager.remove(subTaskId);
            }
        }

        // Затем удаляем сами эпики
        for (Integer epicId : epicTask.keySet()) {
            historyManager.remove(epicId);
        }

        subTaskMap.clear();
        epicTask.clear();
    }


    public void updateEpicStatus(Epic epic) {


        for (Integer i : epic.getAllSubTasks()) {
            if (epic.equals((subTaskMap.get(i)))) {
                i = epic.hashCode();
                if (subTaskMap.get(i).getTaskPriority() == TaskPriority.IN_PROGRESS) {
                    epic.setTaskPriority(TaskPriority.IN_PROGRESS);
                    break;
                } else if (subTaskMap.get(i).getTaskPriority() == TaskPriority.DONE || subTaskMap.get(i).getTaskPriority() == TaskPriority.NEW) {
                    if (subTaskMap.get(i).getTaskPriority() == TaskPriority.DONE) {
                        for (Integer j : epic.getAllSubTasks()) {
                            if (subTaskMap.get(j).getTaskPriority() == TaskPriority.NEW) {
                                epic.setTaskPriority(TaskPriority.IN_PROGRESS);
                                break;
                            } else {
                                epic.setTaskPriority(TaskPriority.DONE);
                            }

                        }
                    } else if (subTaskMap.get(i).getTaskPriority() == TaskPriority.NEW) {
                        if (subTaskMap.get(i).getTaskPriority() == TaskPriority.NEW) {
                            for (Integer j : epic.getAllSubTasks()) {
                                if (subTaskMap.get(j).getTaskPriority() == TaskPriority.DONE) {
                                    epic.setTaskPriority(TaskPriority.IN_PROGRESS);
                                    break;
                                } else {
                                    epic.setTaskPriority(TaskPriority.NEW);
                                }

                            }
                        }
                    }
                }
            }
        }


    }
}
