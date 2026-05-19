package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "OutcomeDefinition")
@IdClass(OutcomeDefinitionId.class)
public class OutcomeDefinition {

    @Id
    @Column(name = "workProductID", length = 25)
    private String workProductID;

    @Id
    @Column(name = "contextID", length = 36) // Matching Context table length
    private String contextID;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "presentationName", length = 76)
    private String presentationName;

    @Column(name = "briefDescription", length = 1000)
    private String briefDescription;

    @Column(name = "purpose", length = 400)
    private String purpose;

    @Column(name = "mainDescription", length = 3700)
    private String mainDescription;

    @Column(name = "keyConsiderations", length = 1000)
    private String keyConsiderations;

    @Column(name = "impactOfNotHaving", length = 250)
    private String impactOfNotHaving;

    @Column(name = "reasonsForNotNeeding", length = 150)
    private String reasonsForNotNeeding;

    @Column(name = "changeDate")
    private java.sql.Date changeDate;

    @Column(name = "changeHistory", length = 10)
    private String changeHistory;

    @Column(name = "originatingProcess", length = 10)
    private String originatingProcess;

    // Getters and Setters
    public String getWorkProductID() { return workProductID; }
    public void setWorkProductID(String workProductID) { this.workProductID = workProductID; }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPresentationName() { return presentationName; }
    public void setPresentationName(String presentationName) { this.presentationName = presentationName; }

    public String getBriefDescription() { return briefDescription; }
    public void setBriefDescription(String briefDescription) { this.briefDescription = briefDescription; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getMainDescription() { return mainDescription; }
    public void setMainDescription(String mainDescription) { this.mainDescription = mainDescription; }

    public String getKeyConsiderations() { return keyConsiderations; }
    public void setKeyConsiderations(String keyConsiderations) { this.keyConsiderations = keyConsiderations; }

    public String getImpactOfNotHaving() { return impactOfNotHaving; }
    public void setImpactOfNotHaving(String impactOfNotHaving) { this.impactOfNotHaving = impactOfNotHaving; }

    public String getReasonsForNotNeeding() { return reasonsForNotNeeding; }
    public void setReasonsForNotNeeding(String reasonsForNotNeeding) { this.reasonsForNotNeeding = reasonsForNotNeeding; }

    public java.sql.Date getChangeDate() { return changeDate; }
    public void setChangeDate(java.sql.Date changeDate) { this.changeDate = changeDate; }

    public String getChangeHistory() { return changeHistory; }
    public void setChangeHistory(String changeHistory) { this.changeHistory = changeHistory; }

    public String getOriginatingProcess() { return originatingProcess; }
    public void setOriginatingProcess(String originatingProcess) { this.originatingProcess = originatingProcess; }
}
