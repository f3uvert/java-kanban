package ru.yandex.tracker;

import ru.yandex.tracker.model.Epic;
import ru.yandex.tracker.model.SubTask;
import ru.yandex.tracker.model.Task;
import ru.yandex.tracker.service.InMemoryHistoryManager;
import ru.yandex.tracker.service.InMemoryTaskManager;
import ru.yandex.tracker.service.TaskManager;
import ru.yandex.tracker.service.TaskPriority;

import static ru.yandex.tracker.service.TaskPriority.*;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        int taskId1 = manager.addNewTask(new Task("task1", "Task1 description", TaskPriority.NEW));
        int taskId2 = manager.addNewTask(new Task("task1", "Task2 description", TaskPriority.NEW));
        int epicId1 = manager.addNewEpic(new Epic("1", "1", NEW));
        int subTaskId1 = manager.addNewSubTask(new SubTask("1", "1", epicId1, NEW));
        int subTaskId2 = manager.addNewSubTask(new SubTask("2", "2", epicId1, NEW));
        int subTaskId3 = manager.addNewSubTask(new SubTask("3", "3", epicId1, NEW));
        printAllTasks(manager);


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
