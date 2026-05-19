package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "DeliverableParts")
@IdClass(DeliverablePartsId.class)
public class DeliverableParts {

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Id
    @Column(name = "deliverableID", length = 25)
    private String deliverableID;

    @Id
    @Column(name = "workProductID", length = 25)
    private String workProductID;

    public DeliverableParts() {}

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getDeliverableID() { return deliverableID; }
    public void setDeliverableID(String deliverableID) { this.deliverableID = deliverableID; }

    public String getWorkProductID() { return workProductID; }
    public void setWorkProductID(String workProductID) { this.workProductID = workProductID; }
}
