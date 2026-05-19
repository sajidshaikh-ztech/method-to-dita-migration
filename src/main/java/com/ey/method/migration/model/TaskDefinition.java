package com.ey.method.migration.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TaskDefinition")
@IdClass(TaskDefinitionId.class)
public class TaskDefinition {

    @Id
    @Column(name = "taskID", length = 25)
    private String taskID;

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "presentationName", length = 255)
    private String presentationName;

    @Column(name = "briefDescription", length = 1000)
    private String briefDescription;

    @Column(name = "objectives", columnDefinition = "TEXT")
    private String objectives;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "keyConsiderations", columnDefinition = "TEXT")
    private String keyConsiderations;

    @Column(name = "estimatingConsiderations", length = 100)
    private String estimatingConsiderations;

    @Column(name = "defaultDuration", length = 100)
    private String defaultDuration;

    @Column(name = "changeDate")
    private Date changeDate;

    @Column(name = "changeDescription", columnDefinition = "TEXT")
    private String changeDescription;

    @Column(name = "originatingProcess", length = 255)
    private String originatingProcess;

    @Column(name = "variabilityBasedOn", length = 255)
    private String variabilityBasedOn;

    // Getters and Setters
    public String getTaskID() { return taskID; }
    public void setTaskID(String taskID) { this.taskID = taskID; }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPresentationName() { return presentationName; }
    public void setPresentationName(String presentationName) { this.presentationName = presentationName; }

    public String getBriefDescription() { return briefDescription; }
    public void setBriefDescription(String briefDescription) { this.briefDescription = briefDescription; }

    public String getObjectives() { return objectives; }
    public void setObjectives(String objectives) { this.objectives = objectives; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getKeyConsiderations() { return keyConsiderations; }
    public void setKeyConsiderations(String keyConsiderations) { this.keyConsiderations = keyConsiderations; }

    public String getEstimatingConsiderations() { return estimatingConsiderations; }
    public void setEstimatingConsiderations(String estimatingConsiderations) { this.estimatingConsiderations = estimatingConsiderations; }

    public String getDefaultDuration() { return defaultDuration; }
    public void setDefaultDuration(String defaultDuration) { this.defaultDuration = defaultDuration; }

    public Date getChangeDate() { return changeDate; }
    public void setChangeDate(Date changeDate) { this.changeDate = changeDate; }

    public String getChangeDescription() { return changeDescription; }
    public void setChangeDescription(String changeDescription) { this.changeDescription = changeDescription; }

    public String getOriginatingProcess() { return originatingProcess; }
    public void setOriginatingProcess(String originatingProcess) { this.originatingProcess = originatingProcess; }

    public String getVariabilityBasedOn() { return variabilityBasedOn; }
    public void setVariabilityBasedOn(String variabilityBasedOn) { this.variabilityBasedOn = variabilityBasedOn; }
}
