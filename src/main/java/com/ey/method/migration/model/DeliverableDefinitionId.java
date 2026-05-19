package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class DeliverableDefinitionId implements Serializable {
    private String workProductID;
    private String contextID;

    public DeliverableDefinitionId() {}
    public DeliverableDefinitionId(String workProductID, String contextID) {
        this.workProductID = workProductID;
        this.contextID = contextID;
    }

    public String getWorkProductID() { return workProductID; }
    public void setWorkProductID(String workProductID) { this.workProductID = workProductID; }
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeliverableDefinitionId that = (DeliverableDefinitionId) o;
        return Objects.equals(workProductID, that.workProductID) && Objects.equals(contextID, that.contextID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workProductID, contextID);
    }
}
