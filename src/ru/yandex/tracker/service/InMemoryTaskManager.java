package ru.yandex.tracker.service;

import ru.yandex.tracker.model.Epic;
import ru.yandex.tracker.model.SubTask;
import ru.yandex.tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    private final HashMap<Integer, Task> commonTask = new HashMap<>();
    private final HashMap<Integer, Epic> epicTask = new HashMap<>();
    private final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    private final ArrayList<Task> historyView = new ArrayList<>();

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
        setHistory(commonTask.get(id));
        return commonTask.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        setHistory((Task) subTaskMap.get(id));
        return subTaskMap.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        setHistory((Task) epicTask.get(id));
        return epicTask.get(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyView;
    }

    private void setHistory(Task task) {
        if (historyView.size() == 10) {
            historyView.removeFirst();
            historyView.add(task);
        } else {
            historyView.add(task);
        }
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
    }

    @Override
    public void deleteEpic(int epicId) {
        Epic epic = epicTask.remove(epicId);
        for (Integer subtaskId : epic.getAllSubTasks()) {
            subTaskMap.remove(subtaskId);
        }
    }

    @Override
    public void deleteSubTask(int id) {
        Epic epic = epicTask.get(subTaskMap.get(id).getEpicId());
        epic.getAllSubTasks().remove(id);
        subTaskMap.remove(id);
        updateEpicStatus(epic);
    }

    @Override
    public void deleteTasks() {
        commonTask.clear();
    }

    @Override
    public void deleteSubTasks() {
        subTaskMap.clear();

        for (Epic epic : epicTask.values()) {
            epic.deleteSubTasks();
        }


    }

    @Override
    public void deleteEpics() {
        epicTask.clear();
        subTaskMap.clear();
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
