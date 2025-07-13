package ru.yandex.tracker.service;

import ru.yandex.tracker.model.Epic;
import ru.yandex.tracker.model.SubTask;
import ru.yandex.tracker.model.Task;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    private final Map<Integer, Task> commonTask = new HashMap<>();
    private final Map<Integer, Epic> epicTask = new HashMap<>();
    private final Map<Integer, SubTask> subTaskMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

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
        ArrayList<Integer> allSubTaskId = epic.getAllSubTasks();
        ArrayList<SubTask> newSubTaskList = new ArrayList<>();
        for (Integer id : allSubTaskId) {
            SubTask subTask = subTaskMap.get(id);
            if (subTask != null) {
                newSubTaskList.add(subTask);
            }
        }
        return newSubTaskList;
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
        final int id = counter++;
        task.setUniqueId(id);
        commonTask.put(id, task);
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
        final int id = counter++;
        subTask.setUniqueId(id);

        subTaskMap.put(id, subTask);
        epicTask.get(subTask.getEpicId()).setSubTaskId(id);
        return id;
    }

    @Override
    public void updateTask(Task task) {
        commonTask.put(task.getUniqueId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicTask.put(epic.getUniqueId(), epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTaskMap.put(subTask.getUniqueId(), subTask);

        updateEpicStatus(epicTask.get(subTask.getEpicId()));
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


    private void updateEpicStatus(Epic epic) {


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
