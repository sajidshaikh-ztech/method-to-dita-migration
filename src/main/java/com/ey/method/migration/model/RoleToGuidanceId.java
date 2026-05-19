package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class RoleToGuidanceId implements Serializable {
    private String contextID;
    private String roleID;
    private String guidanceID;

    public RoleToGuidanceId() {}
    public RoleToGuidanceId(String contextID, String roleID, String guidanceID) {
        this.contextID = contextID;
        this.roleID = roleID;
        this.guidanceID = guidanceID;
    }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }
    public String getRoleID() { return roleID; }
    public void setRoleID(String roleID) { this.roleID = roleID; }
    public String getGuidanceID() { return guidanceID; }
    public void setGuidanceID(String guidanceID) { this.guidanceID = guidanceID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleToGuidanceId that = (RoleToGuidanceId) o;
        return Objects.equals(contextID, that.contextID) &&
               Objects.equals(roleID, that.roleID) &&
               Objects.equals(guidanceID, that.guidanceID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextID, roleID, guidanceID);
    }
}
