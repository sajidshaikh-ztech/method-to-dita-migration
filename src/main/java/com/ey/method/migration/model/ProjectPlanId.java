package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class ProjectPlanId implements Serializable {
    private String contextID;
    private String guidanceID;

    public ProjectPlanId() {}
    public ProjectPlanId(String contextID, String guidanceID) {
        this.contextID = contextID;
        this.guidanceID = guidanceID;
    }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }
    public String getGuidanceID() { return guidanceID; }
    public void setGuidanceID(String guidanceID) { this.guidanceID = guidanceID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectPlanId that = (ProjectPlanId) o;
        return Objects.equals(contextID, that.contextID) && Objects.equals(guidanceID, that.guidanceID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextID, guidanceID);
    }
}
