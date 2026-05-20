package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PursuitSupport")
@IdClass(PursuitSupportId.class)
public class PursuitSupport {

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

    @Column(name = "solutionOverview", columnDefinition = "TEXT")
    private String solutionOverview;

    @Column(name = "elevatorPitch", columnDefinition = "TEXT")
    private String elevatorPitch;

    @Column(name = "valueProp", columnDefinition = "TEXT")
    private String valueProp;

    @Column(name = "positioning", columnDefinition = "TEXT")
    private String positioning;

    @Column(name = "scoping", columnDefinition = "TEXT")
    private String scoping;

    @Column(name = "estimating", columnDefinition = "TEXT")
    private String estimating;

    @Column(name = "additionalInfo", columnDefinition = "TEXT")
    private String additionalInfo;

    // Getters and Setters
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getGuidanceID() { return guidanceID; }
    public void setGuidanceID(String guidanceID) { this.guidanceID = guidanceID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPresentationName() { return presentationName; }
    public void setPresentationName(String presentationName) { this.presentationName = presentationName; }

    public String getSolutionOverview() { return solutionOverview; }
    public void setSolutionOverview(String solutionOverview) { this.solutionOverview = solutionOverview; }

    public String getElevatorPitch() { return elevatorPitch; }
    public void setElevatorPitch(String elevatorPitch) { this.elevatorPitch = elevatorPitch; }

    public String getValueProp() { return valueProp; }
    public void setValueProp(String valueProp) { this.valueProp = valueProp; }

    public String getPositioning() { return positioning; }
    public void setPositioning(String positioning) { this.positioning = positioning; }

    public String getScoping() { return scoping; }
    public void setScoping(String scoping) { this.scoping = scoping; }

    public String getEstimating() { return estimating; }
    public void setEstimating(String estimating) { this.estimating = estimating; }

    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }
}
