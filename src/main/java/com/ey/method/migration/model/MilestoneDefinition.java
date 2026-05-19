package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "MilestoneDefinition")
public class MilestoneDefinition {

    @Id
    @Column(name = "milestoneID", length = 25)
    private String milestoneID;

    @Column(name = "contextID", length = 36)
    private String contextID;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "presentationName", length = 255)
    private String presentationName;

    @Column(name = "briefDescription", length = 1000)
    private String briefDescription;

    @Column(name = "mainDescription", length = 1800)
    private String mainDescription;

    @Column(name = "keyConsiderations", length = 1000)
    private String keyConsiderations;

    // Getters and Setters
    public String getMilestoneID() { return milestoneID; }
    public void setMilestoneID(String milestoneID) { this.milestoneID = milestoneID; }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPresentationName() { return presentationName; }
    public void setPresentationName(String presentationName) { this.presentationName = presentationName; }

    public String getBriefDescription() { return briefDescription; }
    public void setBriefDescription(String briefDescription) { this.briefDescription = briefDescription; }

    public String getMainDescription() { return mainDescription; }
    public void setMainDescription(String mainDescription) { this.mainDescription = mainDescription; }

    public String getKeyConsiderations() { return keyConsiderations; }
    public void setKeyConsiderations(String keyConsiderations) { this.keyConsiderations = keyConsiderations; }
}
