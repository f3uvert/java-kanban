package ru.yandex.tracker.service;

import java.io.File;

public class Managers {

    private Managers() {

    }

    public static TaskManager getDefault() {


        return new FileBackedTaskManager(new File("task_manager.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
