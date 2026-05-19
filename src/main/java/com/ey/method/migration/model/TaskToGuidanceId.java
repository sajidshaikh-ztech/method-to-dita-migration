package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class TaskToGuidanceId implements Serializable {
    private String contextID;
    private String taskID;
    private String guidanceID;

    public TaskToGuidanceId() {}
    public TaskToGuidanceId(String contextID, String taskID, String guidanceID) {
        this.contextID = contextID;
        this.taskID = taskID;
        this.guidanceID = guidanceID;
    }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }
    public String getTaskID() { return taskID; }
    public void setTaskID(String taskID) { this.taskID = taskID; }
    public String getGuidanceID() { return guidanceID; }
    public void setGuidanceID(String guidanceID) { this.guidanceID = guidanceID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskToGuidanceId that = (TaskToGuidanceId) o;
        return Objects.equals(contextID, that.contextID) &&
               Objects.equals(taskID, that.taskID) &&
               Objects.equals(guidanceID, that.guidanceID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextID, taskID, guidanceID);
    }
}
