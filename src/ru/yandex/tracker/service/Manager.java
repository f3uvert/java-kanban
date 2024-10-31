package ru.yandex.tracker.service;

import ru.yandex.tracker.model.Epic;
import ru.yandex.tracker.model.SubTask;
import ru.yandex.tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Manager {
    private int counter = 0; //Если я сделаю счетчик костантой то  не смогу присваивать значение id
    private final HashMap<Integer, Task> commonTask = new HashMap<>();
    private final HashMap<Integer, Epic> epicTask = new HashMap<>();
    private final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();


    List<Task> getTasks() {

        return new ArrayList<>(this.commonTask.values());
    }

    List<SubTask> getSubtasks() {
        ArrayList<SubTask> listSubTasks = new ArrayList<>();
        for (Task hashMap : subTaskMap.values()) {
            listSubTasks.add((SubTask) hashMap);
        }
        return listSubTasks;
    }

    List<Epic> getEpics() {
        ArrayList<Epic> listEpics = new ArrayList<>();
        for (Task hashMap : epicTask.values()) {
            listEpics.add((Epic) hashMap);
        }
        return listEpics;
    }

    List<SubTask> getEpicSubtasks(int epicId) {
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


    Task getTask(int id) {
        return commonTask.get(id);
    }

    SubTask getSubTask(int id) {
        return subTaskMap.get(id);
    }

    Epic getEpic(int id) {
        return epicTask.get(id);
    }


    public int addNewTask(Task task) {
        final int id = counter++; //При такой записи у меня каждый id будет = 0
        task.setUniqueId(id);
        commonTask.put(id, task);
        return id;

    }

    public int addNewEpic(Epic epic) {
        final int id = counter++;
        epic.setUniqueId(id);
        epicTask.put(id, epic);
        return id;
    }

    public int addNewSubTask(SubTask subTask) {
        final int id = counter++;
        subTask.setUniqueId(id);
        subTaskMap.put(id, subTask);
        epicTask.get(subTask.getEpicId()).setSubTaskId(id);
        return id;
    }

    public void updateTask(Task task) {
        commonTask.put(task.getUniqueId(), task);
    }

    public void updateEpic(Epic epic) {
        epicTask.put(epic.getUniqueId(), epic);
    }

    public void updateSubTask(SubTask subTask) {
        subTaskMap.put(subTask.getUniqueId(), subTask);

        updateEpicStatus(epicTask.get(subTask.getEpicId()));
    }

    public void deleteTask(int id) {
        commonTask.remove(id);
    }

    public void deleteEpic(int epicId) {
        epicTask.get(epicId).getAllSubTasks().clear();
        epicTask.remove(epicId);
    }

    public void deleteSubTask(int id) {
        Epic epic = epicTask.get(subTaskMap.get(id).getEpicId());
        epic.getAllSubTasks().remove(id);
        subTaskMap.remove(id);
        updateEpicStatus(epic);
    }

    public void deleteTasks() {
        commonTask.clear();
    }

    public void deleteSubTasks() {
        subTaskMap.clear();

        for (Epic epic : epicTask.values()) {
            epic.deleteSubTasks();
        }


    }

    public void deleteEpics() {
        epicTask.clear();
    }


    private void updateEpicStatus(Epic epic) {


        for (Integer i : epic.getAllSubTasks()) {
            if(epic.equals((subTaskMap.get(i)))) {
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
                    }
                    else if(subTaskMap.get(i).getTaskPriority() == TaskPriority.NEW){
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
