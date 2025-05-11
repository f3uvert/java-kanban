package ru.yandex.tracker.service;

import ru.yandex.tracker.model.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> inMemoryHistoryView = new ArrayList<>();
    private static final int MAX_LIST_SIZE = 10;

    @Override
    public void add(Task task) {
        if (inMemoryHistoryView.size() == MAX_LIST_SIZE) {
            inMemoryHistoryView.removeFirst();
            inMemoryHistoryView.add(task);
        } else {
            inMemoryHistoryView.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(inMemoryHistoryView);
    }
}
