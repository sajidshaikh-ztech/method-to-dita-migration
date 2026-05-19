package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TaskToRole")
@IdClass(TaskToRoleId.class)
public class TaskToRole {

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Id
    @Column(name = "taskID", length = 25)
    private String taskID;

    @Id
    @Column(name = "roleID", length = 25)
    private String roleID;

    @Id
    @Column(name = "relationshipType", length = 50)
    private String relationshipType;

    // Getters and Setters
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getTaskID() { return taskID; }
    public void setTaskID(String taskID) { this.taskID = taskID; }

    public String getRoleID() { return roleID; }
    public void setRoleID(String roleID) { this.roleID = roleID; }

    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }
}
