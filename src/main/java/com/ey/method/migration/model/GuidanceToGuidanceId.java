package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class GuidanceToGuidanceId implements Serializable {
    private String contextID;
    private String guidanceID;
    private String relatedGuidance;

    public GuidanceToGuidanceId() {}
    public GuidanceToGuidanceId(String contextID, String guidanceID, String relatedGuidance) {
        this.contextID = contextID;
        this.guidanceID = guidanceID;
        this.relatedGuidance = relatedGuidance;
    }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }
    public String getGuidanceID() { return guidanceID; }
    public void setGuidanceID(String guidanceID) { this.guidanceID = guidanceID; }
    public String getRelatedGuidance() { return relatedGuidance; }
    public void setRelatedGuidance(String relatedGuidance) { this.relatedGuidance = relatedGuidance; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuidanceToGuidanceId that = (GuidanceToGuidanceId) o;
        return Objects.equals(contextID, that.contextID) &&
               Objects.equals(guidanceID, that.guidanceID) &&
               Objects.equals(relatedGuidance, that.relatedGuidance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextID, guidanceID, relatedGuidance);
    }
}
