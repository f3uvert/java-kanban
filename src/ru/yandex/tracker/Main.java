package ru.yandex.tracker;

import ru.yandex.tracker.model.Epic;
import ru.yandex.tracker.model.SubTask;
import ru.yandex.tracker.model.Task;
import ru.yandex.tracker.service.Manager;
import ru.yandex.tracker.service.TaskPriority;

import static ru.yandex.tracker.service.TaskPriority.*;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        int taskId1 = manager.addNewTask(new Task("task1", "Task1 description", TaskPriority.NEW));
        int taskId2 = manager.addNewTask(new Task("task1", "Task2 description", TaskPriority.NEW));
        int epicId1 = manager.addNewEpic(new Epic("1","1", NEW));
        int subTaskId1 = manager.addNewSubTask(new SubTask("1","1",epicId1,NEW));
        int subTaskId2 = manager.addNewSubTask(new SubTask("2","2",epicId1,NEW));
        int subTaskId3 = manager.addNewSubTask(new SubTask("3","3",epicId1,NEW));
        System.out.println(manager.getSubTask(subTaskId1));
        System.out.println(manager.getEpic(epicId1));
        manager.updateSubTask(manager.getSubTask(subTaskId1));
        manager.updateEpic(manager.getEpic(epicId1));
        System.out.println(manager.getEpicSubtasks(epicId1));

        printTask(manager);

        final Task task = manager.getTask(taskId1);
        task.setTaskPriority(DONE);
        manager.updateTask(task);
        System.out.println("CHANGE STATUS: Task1 NEW->DONE");
        System.out.println("Задачи:");
        printTask(manager);
    }
    private static void printTask(Manager manager) {
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }

    }
}
