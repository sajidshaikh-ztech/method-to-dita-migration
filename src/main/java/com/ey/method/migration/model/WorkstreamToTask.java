package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "WorkstreamToTask")
@IdClass(WorkstreamToTaskId.class)
public class WorkstreamToTask {

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Id
    @Column(name = "workstreamID", length = 25)
    private String workstreamID;

    @Id
    @Column(name = "taskID", length = 25)
    private String taskID;

    // Getters and Setters
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getWorkstreamID() { return workstreamID; }
    public void setWorkstreamID(String workstreamID) { this.workstreamID = workstreamID; }

    public String getTaskID() { return taskID; }
    public void setTaskID(String taskID) { this.taskID = taskID; }
}
