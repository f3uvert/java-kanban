package ru.yandex.tracker.service;

import ru.yandex.tracker.model.Epic;
import ru.yandex.tracker.model.SubTask;
import ru.yandex.tracker.model.Task;

import java.util.List;

public interface TaskManager {


    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<SubTask> getAllSubtasks();

    public List<Task> getTasks();

    public List<SubTask> getSubtasks();

    public List<Epic> getEpics();

    public List<SubTask> getEpicSubtasks(int epicId);

    public Task getTask(int id);

    public SubTask getSubTask(int id);

    public Epic getEpic(int id);

    public int addNewTask(Task task);

    public int addNewEpic(Epic epic);

    public int addNewSubTask(SubTask subTask);

    public void updateTask(Task task);

    public void updateEpic(Epic epic);

    public void updateSubTask(SubTask subTask);

    public void deleteTask(int id);

    public void deleteEpic(int epicId);

    public void deleteSubTask(int id);

    public void deleteTasks();

    public void deleteSubTasks();

    public void deleteEpics();

    public List<Task> getHistory();
    //Добавьте в программу новую функциональность — нужно, чтобы трекер отображал последние просмотренные пользователем задачи.
    // Для этого добавьте метод getHistory в TaskManager и реализуйте его — он должен возвращать последние 10 просмотренных задач.
    // Просмотром будем считать вызов тех методов, которые получают задачу по идентификатору
    // — getTask(int id), getSubtask(int id) и getEpic(int id). От повторных просмотров избавляться не нужно.


}