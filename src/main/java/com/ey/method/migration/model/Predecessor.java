package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Predecessor")
@IdClass(PredecessorId.class)
public class Predecessor {

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Id
    @Column(name = "WBSID", length = 36)
    private String WBSID;

    @Id
    @Column(name = "dependentOn", length = 36)
    private String dependentOn;

    @Column(name = "dependencyType", length = 10)
    private String dependencyType;

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getWBSID() { return WBSID; }
    public void setWBSID(String WBSID) { this.WBSID = WBSID; }

    public String getDependentOn() { return dependentOn; }
    public void setDependentOn(String dependentOn) { this.dependentOn = dependentOn; }

    public String getDependencyType() { return dependencyType; }
    public void setDependencyType(String dependencyType) { this.dependencyType = dependencyType; }
}
