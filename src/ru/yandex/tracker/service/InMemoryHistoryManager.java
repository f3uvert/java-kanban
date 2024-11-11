package ru.yandex.tracker.service;

import ru.yandex.tracker.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> inMemoryHistoryView = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (inMemoryHistoryView.size() == 10) {
            inMemoryHistoryView.removeFirst();
            inMemoryHistoryView.add(task);
        } else {
            inMemoryHistoryView.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryView;
    }
}
