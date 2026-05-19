package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "WorkstreamDefinition")
@IdClass(WorkstreamDefinitionId.class)
public class WorkstreamDefinition {

    @Id
    @Column(name = "workstreamID", length = 25)
    private String workstreamID;

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "briefDescription", length = 1000)
    private String briefDescription;

    @Column(name = "presentationName", length = 255)
    private String presentationName;

    @Column(name = "purpose", length = 1000)
    private String purpose;

    @Column(name = "mainDescription", length = 3700)
    private String mainDescription;

    @Column(name = "keyConsiderations", length = 1000)
    private String keyConsiderations;

    @Column(name = "usageNotes", length = 1000)
    private String usageNotes;

    @Column(name = "changeDate")
    private java.sql.Date changeDate;

    @Column(name = "changeDescription", length = 255)
    private String changeDescription;

    @Column(name = "originatingProcess", length = 255)
    private String originatingProcess;

    // Getters and Setters
    public String getWorkstreamID() { return workstreamID; }
    public void setWorkstreamID(String workstreamID) { this.workstreamID = workstreamID; }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBriefDescription() { return briefDescription; }
    public void setBriefDescription(String briefDescription) { this.briefDescription = briefDescription; }

    public String getPresentationName() { return presentationName; }
    public void setPresentationName(String presentationName) { this.presentationName = presentationName; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getMainDescription() { return mainDescription; }
    public void setMainDescription(String mainDescription) { this.mainDescription = mainDescription; }

    public String getKeyConsiderations() { return keyConsiderations; }
    public void setKeyConsiderations(String keyConsiderations) { this.keyConsiderations = keyConsiderations; }

    public String getUsageNotes() { return usageNotes; }
    public void setUsageNotes(String usageNotes) { this.usageNotes = usageNotes; }

    public java.sql.Date getChangeDate() { return changeDate; }
    public void setChangeDate(java.sql.Date changeDate) { this.changeDate = changeDate; }

    public String getChangeDescription() { return changeDescription; }
    public void setChangeDescription(String changeDescription) { this.changeDescription = changeDescription; }

    public String getOriginatingProcess() { return originatingProcess; }
    public void setOriginatingProcess(String originatingProcess) { this.originatingProcess = originatingProcess; }
}
