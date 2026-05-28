package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PlanAttachments")
@IdClass(PlanAttachmentsId.class)
public class PlanAttachments {

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Id
    @Column(name = "guidanceID", length = 25)
    private String guidanceID;

    @Column(name = "excelPlan", length = 255)
    private String excelPlan;

    @Column(name = "projectPlan", length = 255)
    private String projectPlan;

    // Getters and Setters
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getGuidanceID() { return guidanceID; }
    public void setGuidanceID(String guidanceID) { this.guidanceID = guidanceID; }

    public String getExcelPlan() { return excelPlan; }
    public void setExcelPlan(String excelPlan) { this.excelPlan = excelPlan; }

    public String getProjectPlan() { return projectPlan; }
    public void setProjectPlan(String projectPlan) { this.projectPlan = projectPlan; }
}
