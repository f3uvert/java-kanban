

public class Task {
    String name;
    String description;
    int uniqueId;
    TaskPriority taskPriority;

    Task(String name, String description, int uniqueId, TaskPriority taskPriority) {
        this.name = name;
        this.description = description;
        this.taskPriority = taskPriority;
        this.uniqueId = uniqueId;
    }

    @Override
    public String toString() {
        return name + " - " + description + " ; " + taskPriority;
    }


}
