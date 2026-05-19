package com.ey.method.migration.model;

import java.io.Serializable;
import java.util.Objects;

public class RoleDefinitionId implements Serializable {
    private String roleID;
    private String contextID;

    public RoleDefinitionId() {}
    public RoleDefinitionId(String roleID, String contextID) {
        this.roleID = roleID;
        this.contextID = contextID;
    }

    public String getRoleID() { return roleID; }
    public void setRoleID(String roleID) { this.roleID = roleID; }
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleDefinitionId that = (RoleDefinitionId) o;
        return Objects.equals(roleID, that.roleID) && Objects.equals(contextID, that.contextID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleID, contextID);
    }
}
