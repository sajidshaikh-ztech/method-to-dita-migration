package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "GuidanceToGuidance")
@IdClass(GuidanceToGuidanceId.class)
public class GuidanceToGuidance {

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Id
    @Column(name = "guidanceID", length = 25)
    private String guidanceID;

    @Id
    @Column(name = "relatedGuidance", length = 25)
    private String relatedGuidance;

    @Column(name = "relationshipType", length = 50)
    private String relationshipType;

    // Getters and Setters
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getGuidanceID() { return guidanceID; }
    public void setGuidanceID(String guidanceID) { this.guidanceID = guidanceID; }

    public String getRelatedGuidance() { return relatedGuidance; }
    public void setRelatedGuidance(String relatedGuidance) { this.relatedGuidance = relatedGuidance; }

    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }
}
