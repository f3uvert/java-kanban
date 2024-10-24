
import java.util.HashMap;


public class Epic extends Task {

    HashMap<Integer, SubTask> tasks = new HashMap<>();

    Epic(String name, String description, int uniqueId, TaskPriority taskPriority) {
        super(name, description, uniqueId, taskPriority);
    }

    public void addSubTask(SubTask subTask) {
        tasks.put(subTask.uniqueId, subTask);
    }

    public void printEpic() {
        for (Integer i : tasks.keySet()) {
            System.out.println(tasks.get(i));
        }
    }
}
