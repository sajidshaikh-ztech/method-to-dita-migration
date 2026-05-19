package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class TaskDefinitionId implements Serializable {
    private String taskID;
    private String contextID;

    public TaskDefinitionId() {}
    public TaskDefinitionId(String taskID, String contextID) {
        this.taskID = taskID;
        this.contextID = contextID;
    }

    public String getTaskID() { return taskID; }
    public void setTaskID(String taskID) { this.taskID = taskID; }
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskDefinitionId that = (TaskDefinitionId) o;
        return Objects.equals(taskID, that.taskID) && Objects.equals(contextID, that.contextID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskID, contextID);
    }
}
