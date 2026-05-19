package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "WorkProductToGuidance")
@IdClass(WorkProductToGuidanceId.class)
public class WorkProductToGuidance {

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Id
    @Column(name = "guidanceID", length = 25)
    private String guidanceID;

    @Id
    @Column(name = "workProductID", length = 25)
    private String workProductID;

    public WorkProductToGuidance() {}

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getGuidanceID() { return guidanceID; }
    public void setGuidanceID(String guidanceID) { this.guidanceID = guidanceID; }

    public String getWorkProductID() { return workProductID; }
    public void setWorkProductID(String workProductID) { this.workProductID = workProductID; }
}
