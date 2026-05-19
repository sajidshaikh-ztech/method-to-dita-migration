package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class WorkProductToGuidanceId implements Serializable {
    private String contextID;
    private String guidanceID;
    private String workProductID;

    public WorkProductToGuidanceId() {}

    public WorkProductToGuidanceId(String contextID, String guidanceID, String workProductID) {
        this.contextID = contextID;
        this.guidanceID = guidanceID;
        this.workProductID = workProductID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkProductToGuidanceId that = (WorkProductToGuidanceId) o;
        return Objects.equals(contextID, that.contextID) &&
               Objects.equals(guidanceID, that.guidanceID) &&
               Objects.equals(workProductID, that.workProductID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextID, guidanceID, workProductID);
    }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getGuidanceID() { return guidanceID; }
    public void setGuidanceID(String guidanceID) { this.guidanceID = guidanceID; }

    public String getWorkProductID() { return workProductID; }
    public void setWorkProductID(String workProductID) { this.workProductID = workProductID; }
}
