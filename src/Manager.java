import java.util.HashMap;


public class Manager {
    private static int counter = 0;
    public static HashMap<Integer, Task> commonTask = new HashMap<>();
    public static HashMap<Integer, Task> epicTask = new HashMap<>();
    public static HashMap<Integer, Task> subTaskMap = new HashMap<>();


    public void getAllTasks() {
        for (Task value : commonTask.values()) {
            System.out.println(value);
        }
    }


    public void deleteAllTasks() {
        commonTask.clear();
        epicTask.clear();
        subTaskMap.clear();
    }

    public void getTasksById(int id) {
        System.out.println(commonTask.get(id));
    }

    public void createTask(Task task) {
        counter++;
        commonTask.put(counter, task);
    }

    public void createTask(Epic epic) {

        counter++;
        commonTask.put(counter, epic);
        epicTask.put(counter, epic);

    }

    public void createTask(Epic epic, SubTask subTask) {
        counter++;
        commonTask.put(counter, subTask);
        subTaskMap.put(counter, subTask);
        epic.addSubTask(subTask);
    }

    public void updateTask(Task task) {
        commonTask.put(task.uniqueId, task);
    }

    public void updateTask(Epic epic) {
        epicTask.put(epic.uniqueId, epic);
    }

    public void updateTask(SubTask subTask) {
        subTaskMap.put(subTask.uniqueId, subTask);
    }

    public void deleteTask(int id) {
        deleteTaskByHashMap(commonTask, id);
        deleteTaskByHashMap(epicTask, id);
        deleteTaskByHashMap(subTaskMap, id);
    }

    public void getTaskByEpic(Epic epic) {
        epic.printEpic();
    }


    public void statusTasks(Task task, TaskPriority taskPriority) {
        task.taskPriority = taskPriority;
    }

    public void statusTasks(SubTask subTask, TaskPriority taskPriority) {
        subTask.taskPriority = taskPriority;
    }

    public void statusTaskByEpic(Epic epic, SubTask subTask) {
        int counterNew = 0;
        int counterDone = 0;

        if (subTask.taskPriority == TaskPriority.DONE) {
            for (Integer i : epic.tasks.keySet()) {

                if (epic.tasks.get(i).taskPriority != TaskPriority.DONE) {
                    break;
                } else {
                    counterDone++;
                }
            }
        } else if (subTask.taskPriority == TaskPriority.NEW) {
            for (Integer i : epic.tasks.keySet()) {


                if (epic.tasks.get(i).taskPriority != TaskPriority.NEW) {
                    break;
                } else {
                    counterNew++;
                }
            }
        } else {
            epic.taskPriority = TaskPriority.IN_PROGRESS;
        }
        if (counterNew == epic.tasks.size()) {
            epic.taskPriority = TaskPriority.NEW;
        }
        if (counterDone == epic.tasks.size()) {
            epic.taskPriority = TaskPriority.DONE;
        }
    }

    public int returnId() {
        return counter;
    }

    private void deleteTaskByHashMap(HashMap<Integer, Task> hashMap, int id) {
        for (int i = 0; i < hashMap.size(); i++) {
            if (hashMap.get(i) == commonTask.get(id)) {
                hashMap.remove(id);
            }
        }
    }
}
