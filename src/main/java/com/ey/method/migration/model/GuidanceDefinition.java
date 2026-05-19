package com.ey.method.migration.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "GuidanceDefinition")
@IdClass(GuidanceDefinitionId.class)
public class GuidanceDefinition {

    @Id
    @Column(name = "guidanceID", length = 25)
    private String guidanceID;

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "presentationName", length = 255, nullable = false)
    private String presentationName;

    @Column(name = "externalID", length = 25)
    private String externalID;

    @Column(name = "briefDescription", length = 1000)
    private String briefDescription;

    @Column(name = "mainDescription", columnDefinition = "LONGTEXT")
    private String mainDescription;

    @Column(name = "sourceType", length = 50)
    private String sourceType;

    @Column(name = "sourcePath", length = 255)
    private String sourcePath;

    @Column(name = "changeDate")
    private Date changeDate;

    @Column(name = "changeHistory", length = 100)
    private String changeHistory;

    @Column(name = "originatingProcess", length = 100)
    private String originatingProcess;

    // Getters and Setters
    public String getGuidanceID() { return guidanceID; }
    public void setGuidanceID(String guidanceID) { this.guidanceID = guidanceID; }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPresentationName() { return presentationName; }
    public void setPresentationName(String presentationName) { this.presentationName = presentationName; }

    public String getExternalID() { return externalID; }
    public void setExternalID(String externalID) { this.externalID = externalID; }

    public String getBriefDescription() { return briefDescription; }
    public void setBriefDescription(String briefDescription) { this.briefDescription = briefDescription; }

    public String getMainDescription() { return mainDescription; }
    public void setMainDescription(String mainDescription) { this.mainDescription = mainDescription; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getSourcePath() { return sourcePath; }
    public void setSourcePath(String sourcePath) { this.sourcePath = sourcePath; }

    public Date getChangeDate() { return changeDate; }
    public void setChangeDate(Date changeDate) { this.changeDate = changeDate; }

    public String getChangeHistory() { return changeHistory; }
    public void setChangeHistory(String changeHistory) { this.changeHistory = changeHistory; }

    public String getOriginatingProcess() { return originatingProcess; }
    public void setOriginatingProcess(String originatingProcess) { this.originatingProcess = originatingProcess; }
}
