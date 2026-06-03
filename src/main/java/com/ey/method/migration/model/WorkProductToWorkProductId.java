package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class WorkProductToWorkProductId implements Serializable {
    private String contextID;
    private String workProductID;
    private String relatedWorkProductID;

    public WorkProductToWorkProductId() {}

    public WorkProductToWorkProductId(String contextID, String workProductID, String relatedWorkProductID) {
        this.contextID = contextID;
        this.workProductID = workProductID;
        this.relatedWorkProductID = relatedWorkProductID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkProductToWorkProductId that = (WorkProductToWorkProductId) o;
        return Objects.equals(contextID, that.contextID) &&
               Objects.equals(workProductID, that.workProductID) &&
               Objects.equals(relatedWorkProductID, that.relatedWorkProductID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextID, workProductID, relatedWorkProductID);
    }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getWorkProductID() { return workProductID; }
    public void setWorkProductID(String workProductID) { this.workProductID = workProductID; }

    public String getRelatedWorkProductID() { return relatedWorkProductID; }
    public void setRelatedWorkProductID(String relatedWorkProductID) { this.relatedWorkProductID = relatedWorkProductID; }
}
