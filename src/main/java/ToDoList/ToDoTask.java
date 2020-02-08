package ToDoList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ToDoTask {
    private String taskDescription;
    private TaskPriority priority;
    private Optional<LocalDateTime> completionDate;
    private String additionalInfo;
    private List<ToDoTask> requirements;

    public ToDoTask() {
        this.taskDescription = "";
        this.priority = TaskPriority.LOWEST;
        this.additionalInfo = "";
        this.completionDate = Optional.empty();
        this.requirements = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof ToDoTask) {
            ToDoTask task = (ToDoTask)obj;
            return this.taskDescription.equals(task.taskDescription);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.taskDescription,
                this.priority,
                this.completionDate,
                this.additionalInfo,
                this.requirements
        );
    }

    public void addRequirement(ToDoTask req) {
        this.requirements.add(req);
    }



    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Optional<LocalDateTime> getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Optional<LocalDateTime> completionDate) {
        this.completionDate = completionDate;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public List<ToDoTask> getRequirements() {
        return this.requirements;
    }
}
