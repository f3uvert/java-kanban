package ru.yandex.tracker;

import ru.yandex.tracker.model.Epic;
import ru.yandex.tracker.model.SubTask;
import ru.yandex.tracker.model.Task;
import ru.yandex.tracker.service.*;

import java.io.File;
import java.nio.file.Files;


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

        testEmptyFile();

        testSaveMultipleTasks();

        testLoadMultipleTasks();


    }

    private static void testEmptyFile() {
        System.out.println("\n=== Тест 1: Работа с пустым файлом ===");
        try {
            File tempFile = File.createTempFile("empty", ".csv");
            tempFile.deleteOnExit();


            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

            System.out.println("Создан пустой менеджер. Файл: " + tempFile.getAbsolutePath());
            System.out.println("Задачи: " + manager.getTasks().size());
            System.out.println("Эпики: " + manager.getEpics().size());
            System.out.println("Подзадачи: " + manager.getSubtasks().size());


            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
            System.out.println("Загружено из пустого файла:");
            System.out.println("Задачи: " + loadedManager.getTasks().size());

        } catch (Exception e) {
            System.err.println("Ошибка в тесте пустого файла: " + e.getMessage());
        }
    }

    private static void testSaveMultipleTasks() {
        System.out.println("\n=== Тест 2: Сохранение нескольких задач ===");
        try {
            File tempFile = File.createTempFile("tasks", ".csv");
            tempFile.deleteOnExit();

            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);


            Task task1 = new Task("Помыть посуду", "Срочно помыть всю посуду", TaskPriority.NEW);
            Task task2 = new Task("Сделать ДЗ", "Выполнить задание по трекеру", TaskPriority.IN_PROGRESS);

            Epic epic1 = new Epic("Ремонт", "Сделать ремонт в квартире", TaskPriority.NEW);

            manager.addNewTask(task1);
            manager.addNewTask(task2);
            int epicId = manager.addNewEpic(epic1);

            SubTask subTask1 = new SubTask("Купить материалы", "Купить краску и кисти", epicId, TaskPriority.NEW);
            manager.addNewSubTask(subTask1);

            System.out.println("Сохранено в файл: " + tempFile.getAbsolutePath());
            System.out.println("Содержимое файла:");
            System.out.println(Files.readString(tempFile.toPath()));

        } catch (Exception e) {
            System.err.println("Ошибка при сохранении задач: " + e.getMessage());
        }
    }

    private static void testLoadMultipleTasks() {
        System.out.println("\n=== Тест 3: Загрузка нескольких задач ===");
        try {

            File tempFile = File.createTempFile("tasks_load", ".csv");
            tempFile.deleteOnExit();

            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

            Task task = new Task("Задача 1", "Описание 1", TaskPriority.DONE);
            Epic epic = new Epic("Эпик 1", "Описание эпика", TaskPriority.IN_PROGRESS);

            manager.addNewTask(task);
            int epicId = manager.addNewEpic(epic);

            SubTask subTask = new SubTask("Подзадача 1", "Описание подзадачи", epicId, TaskPriority.NEW);
            manager.addNewSubTask(subTask);


            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            System.out.println("Загруженные задачи:");
            loadedManager.getTasks().forEach(t -> System.out.println("- " + t.getName()));

            System.out.println("\nЗагруженные эпики:");
            loadedManager.getEpics().forEach(e -> System.out.println("- " + e.getName()));

            System.out.println("\nЗагруженные подзадачи:");
            loadedManager.getSubtasks().forEach(st -> System.out.println("- " + st.getName()));

            System.out.println("\nИстория просмотров:");
            loadedManager.getHistory().forEach(h -> System.out.println("- " + h.getName()));

        } catch (Exception e) {
            System.err.println("Ошибка при загрузке задач: " + e.getMessage());
        }
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
