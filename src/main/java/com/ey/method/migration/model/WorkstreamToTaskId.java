package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class WorkstreamToTaskId implements Serializable {
    private String contextID;
    private String workstreamID;
    private String taskID;

    public WorkstreamToTaskId() {}
    public WorkstreamToTaskId(String contextID, String workstreamID, String taskID) {
        this.contextID = contextID;
        this.workstreamID = workstreamID;
        this.taskID = taskID;
    }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }
    public String getWorkstreamID() { return workstreamID; }
    public void setWorkstreamID(String workstreamID) { this.workstreamID = workstreamID; }
    public String getTaskID() { return taskID; }
    public void setTaskID(String taskID) { this.taskID = taskID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkstreamToTaskId that = (WorkstreamToTaskId) o;
        return Objects.equals(contextID, that.contextID) &&
               Objects.equals(workstreamID, that.workstreamID) &&
               Objects.equals(taskID, that.taskID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextID, workstreamID, taskID);
    }
}
