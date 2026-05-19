package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "GettingStarted")
@IdClass(GettingStartedId.class)
public class GettingStarted {

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Id
    @Column(name = "guidanceID", length = 25)
    private String guidanceID;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "presentationName", length = 50)
    private String presentationName;

    @Column(name = "background", columnDefinition = "TEXT")
    private String background;

    @Column(name = "howToApply", columnDefinition = "TEXT")
    private String howToApply;

    @Column(name = "considerations", columnDefinition = "TEXT")
    private String considerations;

    @Column(name = "sizing", columnDefinition = "TEXT")
    private String sizing;

    @Column(name = "staffing", columnDefinition = "TEXT")
    private String staffing;

    @Column(name = "nextSteps", columnDefinition = "TEXT")
    private String nextSteps;

    @Column(name = "purpose", columnDefinition = "TEXT")
    private String purpose;

    // Getters and Setters
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getGuidanceID() { return guidanceID; }
    public void setGuidanceID(String guidanceID) { this.guidanceID = guidanceID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPresentationName() { return presentationName; }
    public void setPresentationName(String presentationName) { this.presentationName = presentationName; }

    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }

    public String getHowToApply() { return howToApply; }
    public void setHowToApply(String howToApply) { this.howToApply = howToApply; }

    public String getConsiderations() { return considerations; }
    public void setConsiderations(String considerations) { this.considerations = considerations; }

    public String getSizing() { return sizing; }
    public void setSizing(String sizing) { this.sizing = sizing; }

    public String getStaffing() { return staffing; }
    public void setStaffing(String staffing) { this.staffing = staffing; }

    public String getNextSteps() { return nextSteps; }
    public void setNextSteps(String nextSteps) { this.nextSteps = nextSteps; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}
