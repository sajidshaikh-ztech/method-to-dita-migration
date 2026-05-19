package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class DeliverablePartsId implements Serializable {
    private String contextID;
    private String deliverableID;
    private String workProductID;

    public DeliverablePartsId() {}

    public DeliverablePartsId(String contextID, String deliverableID, String workProductID) {
        this.contextID = contextID;
        this.deliverableID = deliverableID;
        this.workProductID = workProductID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliverablePartsId that = (DeliverablePartsId) o;
        return Objects.equals(contextID, that.contextID) &&
               Objects.equals(deliverableID, that.deliverableID) &&
               Objects.equals(workProductID, that.workProductID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextID, deliverableID, workProductID);
    }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getDeliverableID() { return deliverableID; }
    public void setDeliverableID(String deliverableID) { this.deliverableID = deliverableID; }

    public String getWorkProductID() { return workProductID; }
    public void setWorkProductID(String workProductID) { this.workProductID = workProductID; }
}
