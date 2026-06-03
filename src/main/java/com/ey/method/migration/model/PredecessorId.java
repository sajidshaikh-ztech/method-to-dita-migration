package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class PredecessorId implements Serializable {
    private String contextID;
    private String WBSID;
    private String dependentOn;

    public PredecessorId() {}

    public PredecessorId(String contextID, String WBSID, String dependentOn) {
        this.contextID = contextID;
        this.WBSID = WBSID;
        this.dependentOn = dependentOn;
    }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getWBSID() { return WBSID; }
    public void setWBSID(String WBSID) { this.WBSID = WBSID; }

    public String getDependentOn() { return dependentOn; }
    public void setDependentOn(String dependentOn) { this.dependentOn = dependentOn; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PredecessorId that = (PredecessorId) o;
        return Objects.equals(contextID, that.contextID) &&
               Objects.equals(WBSID, that.WBSID) &&
               Objects.equals(dependentOn, that.dependentOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextID, WBSID, dependentOn);
    }
}
