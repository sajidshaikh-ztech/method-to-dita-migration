package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "WorkProductToWorkProduct")
@IdClass(WorkProductToWorkProductId.class)
public class WorkProductToWorkProduct {

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Id
    @Column(name = "workProductID", length = 25)
    private String workProductID;

    @Id
    @Column(name = "relatedWorkProductID", length = 25)
    private String relatedWorkProductID;

    @Column(name = "relationshipType", length = 50)
    private String relationshipType;

    public WorkProductToWorkProduct() {}

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getWorkProductID() { return workProductID; }
    public void setWorkProductID(String workProductID) { this.workProductID = workProductID; }

    public String getRelatedWorkProductID() { return relatedWorkProductID; }
    public void setRelatedWorkProductID(String relatedWorkProductID) { this.relatedWorkProductID = relatedWorkProductID; }

    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }
}
