package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ProjectPlan")
@IdClass(ProjectPlanId.class)
public class ProjectPlan {

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Id
    @Column(name = "guidanceID", length = 25)
    private String guidanceID;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "presentationName", length = 255)
    private String presentationName;

    @Column(name = "mainDescription", length = 1500)
    private String mainDescription;

    @Column(name = "howToUse", length = 2000)
    private String howToUse;

    @Column(name = "technicalConsiderations", length = 600)
    private String technicalConsiderations;

    // Getters and Setters
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getGuidanceID() { return guidanceID; }
    public void setGuidanceID(String guidanceID) { this.guidanceID = guidanceID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPresentationName() { return presentationName; }
    public void setPresentationName(String presentationName) { this.presentationName = presentationName; }

    public String getMainDescription() { return mainDescription; }
    public void setMainDescription(String mainDescription) { this.mainDescription = mainDescription; }

    public String getHowToUse() { return howToUse; }
    public void setHowToUse(String howToUse) { this.howToUse = howToUse; }

    public String getTechnicalConsiderations() { return technicalConsiderations; }
    public void setTechnicalConsiderations(String technicalConsiderations) { this.technicalConsiderations = technicalConsiderations; }
}
