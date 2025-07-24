package ru.yandex.tracker.service;

import ru.yandex.tracker.model.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        int id = super.addNewSubTask(subTask);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }


    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            writer.write("id,type,name,priority,description,epic\n");


            for (Task task : getTasks()) {
                writer.write(taskToString(task) + "\n");
            }


            for (Epic epic : getEpics()) {
                writer.write(taskToString(epic) + "\n");
            }


            for (SubTask subTask : getSubtasks()) {
                writer.write(taskToString(subTask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }


    private String taskToString(Task task) {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(String.valueOf(task.getUniqueId()));
        joiner.add(task.getType().name());
        joiner.add(task.getName());
        joiner.add(task.getTaskPriority().name());
        joiner.add(task.getDescription());


        joiner.add(task.getStartTime() != null ? task.getStartTime().toString() : "null");
        joiner.add(task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "null");

        if (task instanceof SubTask) {
            joiner.add(String.valueOf(((SubTask) task).getEpicId()));
        }
        return joiner.toString();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");


            for (int i = 1; i < lines.length; i++) {
                Task task = taskFromString(lines[i]);
                if (task != null) {
                    if (task instanceof Epic) {
                        manager.addNewEpic((Epic) task);
                    } else if (task instanceof SubTask) {
                        manager.addNewSubTask((SubTask) task);
                    } else {
                        manager.addNewTask(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }

        return manager;
    }


    private static Task taskFromString(String value) {
        String[] fields = value.split(",", -1);

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskPriority priority = TaskPriority.valueOf(fields[3]);
        String description = fields[4];
        LocalDateTime startTime = !"null".equals(fields[5]) ? LocalDateTime.parse(fields[5]) : null;
        Duration duration = !"null".equals(fields[6]) ? Duration.ofMinutes(Long.parseLong(fields[6])) : null;


        switch (type) {
            case TASK:
                Task task = new Task(name, description, priority,startTime,duration);
                task.setUniqueId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description, priority);
                epic.setUniqueId(id);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                SubTask subTask = new SubTask(name, description, epicId, priority,startTime,duration);
                subTask.setUniqueId(id);
                return subTask;
            default:
                return null;
        }
    }
}

