package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "RoleToGuidance")
@IdClass(RoleToGuidanceId.class)
public class RoleToGuidance {

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Id
    @Column(name = "roleID", length = 25)
    private String roleID;

    @Id
    @Column(name = "guidanceID", length = 25)
    private String guidanceID;

    // Getters and Setters
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getRoleID() { return roleID; }
    public void setRoleID(String roleID) { this.roleID = roleID; }

    public String getGuidanceID() { return guidanceID; }
    public void setGuidanceID(String guidanceID) { this.guidanceID = guidanceID; }
}
