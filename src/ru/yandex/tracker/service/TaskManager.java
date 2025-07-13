package ru.yandex.tracker.service;

import ru.yandex.tracker.model.Epic;
import ru.yandex.tracker.model.SubTask;
import ru.yandex.tracker.model.Task;

import java.util.List;

public interface TaskManager {


    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<SubTask> getAllSubtasks();

    List<Task> getTasks();

    List<SubTask> getSubtasks();

    List<Epic> getEpics();

    List<SubTask> getEpicSubtasks(int epicId);

    Task getTask(int id);

    SubTask getSubTask(int id);

    Epic getEpic(int id);

    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    int addNewSubTask(SubTask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void deleteTask(int id);

    void deleteEpic(int epicId);

    void deleteSubTask(int id);

    void deleteTasks();

    void deleteSubTasks();

    void deleteEpics();

    List<Task> getHistory();


}