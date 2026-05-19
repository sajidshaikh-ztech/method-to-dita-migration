package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class WbsId implements Serializable {
    private String contextID;
    private String parentID;
    private String childID;

    public WbsId() {}

    public WbsId(String contextID, String parentID, String childID) {
        this.contextID = contextID;
        this.parentID = parentID;
        this.childID = childID;
    }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getParentID() { return parentID; }
    public void setParentID(String parentID) { this.parentID = parentID; }

    public String getChildID() { return childID; }
    public void setChildID(String childID) { this.childID = childID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WbsId wbsId = (WbsId) o;
        return Objects.equals(contextID, wbsId.contextID) &&
               Objects.equals(parentID, wbsId.parentID) &&
               Objects.equals(childID, wbsId.childID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextID, parentID, childID);
    }
}
