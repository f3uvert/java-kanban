package ru.yandex.tracker.service;

import org.junit.jupiter.api.Test;
import ru.yandex.tracker.model.Epic;
import ru.yandex.tracker.model.SubTask;
import ru.yandex.tracker.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.tracker.service.TaskPriority.IN_PROGRESS;
import static ru.yandex.tracker.service.TaskPriority.NEW;

class TaskTest {

    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();


    @Test
    public void equalsTaskId() {
        Task task = new Task("1", "1", TaskPriority.NEW);
        Task task1 = new Task("2", "2", TaskPriority.NEW);
        task.setUniqueId(1);
        task1.setUniqueId(1);
        //task.setUniqueId(task1.getUniqueId());

        assertEquals(task, task1);


    }//True

    @Test
    public void equalsParentId() {
        Epic epic = new Epic("1", "1", TaskPriority.NEW);
        epic.setUniqueId(1);
        SubTask subTask = new SubTask("1", "1", epic.getUniqueId(), TaskPriority.NEW);
        subTask.setUniqueId(1);
        assertNotEquals(epic, subTask);
    } //True

    @Test
    public void checkEpicAdd() {
        Epic epic = new Epic("1", "1", TaskPriority.NEW);
        epic.setUniqueId(1);
        epic.setSubTaskId(epic.getUniqueId());
        assertEquals(-1, epic.getSubTaskId(epic.getUniqueId()));


    }

    @Test
    public void checkSubTask() {
        SubTask subTask = new SubTask("1", "1", 5, TaskPriority.NEW);
        subTask.setUniqueId(1);
        subTask.setEpicId(subTask.getUniqueId());

        assertEquals(5, subTask.getEpicId());

    }//True

    @Test
    public void utilClass() {
        assertNotNull(Managers.getDefault());
    }


    @Test
    public void idInMemoryTaskManager() {
        Task task = new Task("1", "1", NEW);
        taskManager.addNewTask(task);
        Epic epic = new Epic("1", "1", NEW);
        taskManager.addNewEpic(epic);
        SubTask subTask = new SubTask("1", "1", epic.getUniqueId(), NEW);
        taskManager.addNewSubTask(subTask);
        assertEquals(task, taskManager.getTask(task.getUniqueId()));
        assertEquals(subTask, taskManager.getSubTask(subTask.getUniqueId()));
        assertEquals(epic, taskManager.getEpic(epic.getUniqueId()));
    }

    @Test
    public void generateId() {
        Task task = new Task("1", "1", NEW);
        Epic epic = new Epic("2","2",NEW);
        task.setUniqueId(1);
        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic);
        assertNotEquals(task.getUniqueId(),epic.getUniqueId());
    }

    @Test
    public void editParameter() {
        Task task = new Task("1", "1", NEW);

        taskManager.addNewTask(task);
        task.setName("2");
        task.setDescription("2");
        task.setTaskPriority(IN_PROGRESS);
        task.setUniqueId(1);

        assertEquals(task.getName(),taskManager.getTask(0).getName());
        assertEquals(task.getDescription(),taskManager.getTask(0).getDescription());
        assertEquals(task.getUniqueId(),taskManager.getTask(0).getUniqueId());
        assertEquals(task.getTaskPriority(),taskManager.getTask(0).getTaskPriority());
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = taskManager.addNewTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }
    @Test
    public void add(){
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        inMemoryHistoryManager.add(task);
        final List<Task> history = inMemoryHistoryManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }


}