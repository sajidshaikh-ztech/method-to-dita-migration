package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class TaskToRoleId implements Serializable {
    private String contextID;
    private String taskID;
    private String roleID;
    private String relationshipType;

    public TaskToRoleId() {}
    public TaskToRoleId(String contextID, String taskID, String roleID, String relationshipType) {
        this.contextID = contextID;
        this.taskID = taskID;
        this.roleID = roleID;
        this.relationshipType = relationshipType;
    }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }
    public String getTaskID() { return taskID; }
    public void setTaskID(String taskID) { this.taskID = taskID; }
    public String getRoleID() { return roleID; }
    public void setRoleID(String roleID) { this.roleID = roleID; }
    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskToRoleId that = (TaskToRoleId) o;
        return Objects.equals(contextID, that.contextID) &&
               Objects.equals(taskID, that.taskID) &&
               Objects.equals(roleID, that.roleID) &&
               Objects.equals(relationshipType, that.relationshipType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextID, taskID, roleID, relationshipType);
    }
}
