package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class TaskUsageId implements Serializable {
    private String descriptorID;
    private String contextID;

    public TaskUsageId() {}

    public TaskUsageId(String descriptorID, String contextID) {
        this.descriptorID = descriptorID;
        this.contextID = contextID;
    }

    public String getDescriptorID() { return descriptorID; }
    public void setDescriptorID(String descriptorID) { this.descriptorID = descriptorID; }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskUsageId that = (TaskUsageId) o;
        return Objects.equals(descriptorID, that.descriptorID) &&
               Objects.equals(contextID, that.contextID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptorID, contextID);
    }
}
