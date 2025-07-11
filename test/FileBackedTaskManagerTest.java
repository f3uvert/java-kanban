import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.tracker.model.Epic;
import ru.yandex.tracker.model.SubTask;
import ru.yandex.tracker.model.Task;
import ru.yandex.tracker.service.*;
import ru.yandex.tracker.service.TaskPriority;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest{

InMemoryTaskManager taskManager = new InMemoryTaskManager();
InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
private HistoryManager historyManager;
private Task task1;
private Task task2;
private File tempFile;
private FileBackedTaskManager manager;

@Test
void createTaskShouldAddTaskToManager() {
    Task task = new Task("Test Task", "Description", TaskPriority.NEW);
    int taskId = taskManager.addNewTask(task);

    Task savedTask = taskManager.getTask(taskId);
    assertNotNull(savedTask, "Задача должна сохраняться");
    assertEquals(task, savedTask, "Сохраненная задача должна совпадать с оригиналом");
}

@Test
void updateTaskShouldChangeExistingTask() {
    Task task = new Task("Test Task", "Description", TaskPriority.NEW);
    int taskId = taskManager.addNewTask(task);

    Task updatedTask = new Task("Updated Task", "New Description", TaskPriority.IN_PROGRESS);
    updatedTask.setUniqueId(taskId);
    taskManager.updateTask(updatedTask);

    Task savedTask = taskManager.getTask(taskId);
    assertEquals("Updated Task", savedTask.getName(), "Имя задачи должно обновиться");
    assertEquals(TaskPriority.IN_PROGRESS, savedTask.getTaskPriority(), "Статус должен обновиться");
}

@Test
void deleteTaskShouldRemoveTaskFromManager() {
    Task task = new Task("Test Task", "Description", TaskPriority.NEW);
    int taskId = taskManager.addNewTask(task);

    taskManager.deleteTask(taskId);
    assertNull(taskManager.getTask(taskId), "Задача должна удаляться");
}

@Test
void getHistoryShouldReturnViewedTasks() {
    Task task = new Task("Test Task", "Description", TaskPriority.NEW);
    int taskId = taskManager.addNewTask(task);

    taskManager.getTask(taskId);
    List<Task> history = taskManager.getHistory();

    assertEquals(1, history.size(), "История должна содержать просмотренные задачи");
    assertEquals(taskId, history.get(0).getUniqueId(), "ID задачи в истории должен совпадать");
}

@Test
void epicShouldContainSubTasks() {
    Epic epic = new Epic("Test Epic", "Description", TaskPriority.NEW);
    int epicId = taskManager.addNewEpic(epic);

    SubTask subTask = new SubTask("Test SubTask", "Description", epicId, TaskPriority.NEW);
    int subTaskId = taskManager.addNewSubTask(subTask);

    List<SubTask> epicSubTasks = taskManager.getEpicSubtasks(epicId);
    assertEquals(1, epicSubTasks.size(), "Эпик должен содержать подзадачи");
    assertEquals(subTaskId, epicSubTasks.get(0).getUniqueId(), "ID подзадачи должно совпадать");
}

@Test
void deleteEpicShouldRemoveAllSubTasks() {
    Epic epic = new Epic("Test Epic", "Description", TaskPriority.NEW);
    int epicId = taskManager.addNewEpic(epic);

    SubTask subTask = new SubTask("Test SubTask", "Description", epicId, TaskPriority.NEW);
    int subTaskId = taskManager.addNewSubTask(subTask);

    taskManager.deleteEpic(epicId);
    assertNull(taskManager.getSubTask(subTaskId), "Подзадачи должны удаляться вместе с эпиком");
}

@BeforeEach
void setUp() throws IOException {
    historyManager = new InMemoryHistoryManager();
    task1 = new Task("Task 1", "Description 1", TaskPriority.NEW);
    task1.setUniqueId(1);
    task2 = new Task("Task 2", "Description 2", TaskPriority.NEW);
    task2.setUniqueId(2);
    tempFile = File.createTempFile("tasks", ".csv");
    manager = new FileBackedTaskManager(tempFile);
}

@Test
void addShouldAddTaskToHistory() {
    historyManager.add(task1);
    final List<Task> history = historyManager.getHistory();
    assertNotNull(history, "История не должна быть null");
    assertEquals(1, history.size(), "История должна содержать 1 задачу");
    assertEquals(task1, history.get(0), "Задачи должны совпадать");
}

@Test
void addShouldKeepLastTaskVersion() {
    Task updatedTask = new Task("Updated Task 1", "New Description", TaskPriority.DONE);
    updatedTask.setUniqueId(1);

    historyManager.add(task1);
    historyManager.add(updatedTask);

    final List<Task> history = historyManager.getHistory();
    assertEquals(1, history.size(), "История должна содержать только последнюю версию");
    assertEquals(updatedTask, history.get(0), "Должна сохраниться последняя версия");
}

@Test
void getHistoryShouldReturnEmptyListWhenNoTasks() {
    final List<Task> history = historyManager.getHistory();
    assertNotNull(history, "История не должна быть null");
    assertTrue(history.isEmpty(), "История должна быть пустой");
}

@Test
void historyShouldNotExceedMaxSize() {
    for (int i = 0; i < 15; i++) {
        Task task = new Task("Task " + i, "Description " + i, TaskPriority.NEW);
        task.setUniqueId(i);
        historyManager.add(task);
    }

    final List<Task> history = historyManager.getHistory();
    assertEquals(15, history.size(), "История должна содержать максимум 15 задач");
}


@AfterEach
void tearDown() {
    tempFile.delete();
}

@Test
void shouldSaveAndLoadEmptyFile() {
    FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
    assertTrue(loaded.getAllTasks().isEmpty());
    assertTrue(loaded.getAllEpics().isEmpty());
    assertTrue(loaded.getAllSubtasks().isEmpty());
}

@Test
void shouldHandleSaveException() {
    File readOnlyFile = new File("/readonly/tasks.csv");
    FileBackedTaskManager brokenManager = new FileBackedTaskManager(readOnlyFile);

    assertThrows(ManagerSaveException.class, () ->
            brokenManager.addNewTask(new Task("Test", "Desc", TaskPriority.NEW)));
}
}



