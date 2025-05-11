package ru.yandex.tracker;

import ru.yandex.tracker.model.Epic;
import ru.yandex.tracker.model.SubTask;
import ru.yandex.tracker.model.Task;
import ru.yandex.tracker.service.*;



public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();


        Task task1 = new Task("Task 1", "Description 1", TaskPriority.NEW);
        int taskId1 = manager.addNewTask(task1);

        Epic epic1 = new Epic("Epic 1", "Description epic", TaskPriority.NEW);
        int epicId1 = manager.addNewEpic(epic1);

        SubTask subTask1 = new SubTask("Subtask 1", "Desc", epicId1, TaskPriority.NEW);
        int subTaskId1 = manager.addNewSubTask(subTask1);


        manager.getTask(taskId1);
        manager.getEpic(epicId1);
        manager.getSubTask(subTaskId1);
        manager.getTask(taskId1);


        System.out.println(manager.getHistory());




    }

    private static void printTask(InMemoryTaskManager manager) {
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getUniqueId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
