package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TaskUsage")
@IdClass(TaskUsageId.class)
public class TaskUsage {

    @Id
    @Column(name = "descriptorID", length = 25)
    private String descriptorID;

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Column(name = "taskID", length = 25)
    private String taskID;

    @Column(name = "predecessorID", length = 25)
    private String predecessorID;

    // Getters and Setters
    public String getDescriptorID() { return descriptorID; }
    public void setDescriptorID(String descriptorID) { this.descriptorID = descriptorID; }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getTaskID() { return taskID; }
    public void setTaskID(String taskID) { this.taskID = taskID; }

    public String getPredecessorID() { return predecessorID; }
    public void setPredecessorID(String predecessorID) { this.predecessorID = predecessorID; }
}
