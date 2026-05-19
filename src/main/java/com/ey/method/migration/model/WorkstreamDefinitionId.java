package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class WorkstreamDefinitionId implements Serializable {
    private String workstreamID;
    private String contextID;

    public WorkstreamDefinitionId() {}
    public WorkstreamDefinitionId(String workstreamID, String contextID) {
        this.workstreamID = workstreamID;
        this.contextID = contextID;
    }

    public String getWorkstreamID() { return workstreamID; }
    public void setWorkstreamID(String workstreamID) { this.workstreamID = workstreamID; }
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkstreamDefinitionId that = (WorkstreamDefinitionId) o;
        return Objects.equals(workstreamID, that.workstreamID) && Objects.equals(contextID, that.contextID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workstreamID, contextID);
    }
}
