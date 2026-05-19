package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class TaskToWorkProductId implements Serializable {
    private String contextID;
    private String taskID;
    private String workProductID;
    private String relationshipType;

    public TaskToWorkProductId() {}
    public TaskToWorkProductId(String contextID, String taskID, String workProductID, String relationshipType) {
        this.contextID = contextID;
        this.taskID = taskID;
        this.workProductID = workProductID;
        this.relationshipType = relationshipType;
    }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }
    public String getTaskID() { return taskID; }
    public void setTaskID(String taskID) { this.taskID = taskID; }
    public String getWorkProductID() { return workProductID; }
    public void setWorkProductID(String workProductID) { this.workProductID = workProductID; }
    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskToWorkProductId that = (TaskToWorkProductId) o;
        return Objects.equals(contextID, that.contextID) &&
               Objects.equals(taskID, that.taskID) &&
               Objects.equals(workProductID, that.workProductID) &&
               Objects.equals(relationshipType, that.relationshipType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextID, taskID, workProductID, relationshipType);
    }
}
