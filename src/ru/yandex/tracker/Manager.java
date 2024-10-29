package ru.yandex.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Manager {
    private static int counter = 0;
    private static HashMap<Integer, Task> commonTask = new HashMap<>();
    private static HashMap<Integer, Task> epicTask = new HashMap<>();
    private static HashMap<Integer, Task> subTaskMap = new HashMap<>();


    List<Task> getTasks() {
        ArrayList<Task> listTasks = new ArrayList<>();
        for (Task hashMap : commonTask.values()) {
            listTasks.add(hashMap);
        }
        return listTasks;
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
        Epic epic = (Epic) epicTask.get(epicId);
        ArrayList<Integer> allSubTaskId = epic.getAllSubTasks();
        ArrayList<SubTask> newSubTaskList = new ArrayList<>();
        for (int i = 0; i < allSubTaskId.size(); i++) {
            if (subTaskMap.get(allSubTaskId.get(i)) != null) {
                newSubTaskList.add((SubTask) subTaskMap.get(allSubTaskId.get(i)));
            }
        }
        return newSubTaskList;
    }


    Task getTask(int id) {
        return commonTask.get(id);
    }

    SubTask getSubTask(int id) {
        return (SubTask) subTaskMap.get(id);
    }

    Epic getEpic(int id) {
        return (Epic) epicTask.get(id);
    }


    public int addNewTask(Task task) {
        counter++;
        commonTask.put(counter, task);
        return counter;
    }

    public int addNewEpic(Epic epic) {
        counter++;
        epicTask.put(counter, epic);
        return counter;
    }

    public int addNewSubTask(SubTask subTask) {
        counter++;
        subTaskMap.put(counter, subTask);
        return counter;
    }

    public void updateTask(Task task) {
        commonTask.put(task.getUniqueId(), task);
    }

    public void updateEpic(Epic epic) {
        epicTask.put(epic.getUniqueId(), epic);
    }

    public void updateSubTask(SubTask subTask) {
        subTaskMap.put(subTask.getUniqueId(), subTask);
    }

    public void deleteTask(int id) {
        commonTask.remove(id);
    }

    public void deleteEpic(int id) {
        epicTask.remove(id);
    }

    public void deleteSubTask(int id) {
        subTaskMap.remove(id);
    }

    public void deleteTasks() {
        commonTask.clear();
    }

    public void deleteSubTasks() {
        subTaskMap.clear();
        if (!epicTask.isEmpty()) {
            for (int i = 0; i < epicTask.size(); i++) {
                ((Epic) epicTask.get(i)).deleteSubTasks();
            }
        }


    }

    public void deleteEpics() {
        deleteSubTasks();
        epicTask.clear();
    }


    public void statusTasks(Task task, TaskPriority taskPriority) {
        task.setTaskPriority(taskPriority);
    }

    public void statusTasks(SubTask subTask, TaskPriority taskPriority) {
        subTask.setTaskPriority(taskPriority);
    }

    private void statusTaskByEpic(Epic epic, SubTask subTask) {
        int counterNew = 0;
        int counterDone = 0;

        if (subTask.getTaskPriority() == TaskPriority.DONE) {
            for (Integer i : epic.getAllSubTasks()) { //SubTask subtask1

                if (subTaskMap.get(i).getTaskPriority() != TaskPriority.DONE) {
                    break;
                } else {
                    counterDone++;
                }
            }
        } else if (subTask.getTaskPriority() == TaskPriority.NEW) {
            for (Integer i : epic.getAllSubTasks()) {


                if (subTaskMap.get(i).getTaskPriority() != TaskPriority.NEW) {
                    break;
                } else {
                    counterNew++;
                }
            }
        } else {
            epic.setTaskPriority(TaskPriority.IN_PROGRESS);
        }
        if (counterNew == epic.getAllSubTasks().size()) {
            epic.setTaskPriority(TaskPriority.NEW);
        }
        if (counterDone == epic.getAllSubTasks().size()) {
            epic.setTaskPriority(TaskPriority.DONE);
        }
    }
}
