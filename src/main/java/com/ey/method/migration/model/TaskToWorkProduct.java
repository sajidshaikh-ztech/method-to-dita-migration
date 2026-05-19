package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TaskToWorkProduct")
@IdClass(TaskToWorkProductId.class)
public class TaskToWorkProduct {

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Id
    @Column(name = "taskID", length = 25)
    private String taskID;

    @Id
    @Column(name = "workProductID", length = 25)
    private String workProductID;

    @Id
    @Column(name = "relationshipType", length = 50)
    private String relationshipType;

    // Getters and Setters
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getTaskID() { return taskID; }
    public void setTaskID(String taskID) { this.taskID = taskID; }

    public String getWorkProductID() { return workProductID; }
    public void setWorkProductID(String workProductID) { this.workProductID = workProductID; }

    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }
}
