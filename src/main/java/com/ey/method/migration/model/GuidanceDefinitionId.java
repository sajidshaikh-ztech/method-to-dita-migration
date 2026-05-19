package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class GuidanceDefinitionId implements Serializable {
    private String guidanceID;
    private String contextID;

    public GuidanceDefinitionId() {}
    public GuidanceDefinitionId(String guidanceID, String contextID) {
        this.guidanceID = guidanceID;
        this.contextID = contextID;
    }

    public String getGuidanceID() { return guidanceID; }
    public void setGuidanceID(String guidanceID) { this.guidanceID = guidanceID; }
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuidanceDefinitionId that = (GuidanceDefinitionId) o;
        return Objects.equals(guidanceID, that.guidanceID) && Objects.equals(contextID, that.contextID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guidanceID, contextID);
    }
}
