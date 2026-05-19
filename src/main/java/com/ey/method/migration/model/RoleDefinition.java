package com.ey.method.migration.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "RoleDefinition")
@IdClass(RoleDefinitionId.class)
public class RoleDefinition {

    @Id
    @Column(name = "roleID", length = 25)
    private String roleID;

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "presentationName", length = 255)
    private String presentationName;

    @Column(name = "briefDescription", length = 1000)
    private String briefDescription;

    @Column(name = "responsibilities", columnDefinition = "TEXT")
    private String responsibilities;

    @Column(name = "keyConsiderations", columnDefinition = "TEXT")
    private String keyConsiderations;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    @Column(name = "staffing", columnDefinition = "TEXT")
    private String staffing;

    @Column(name = "synonyms", length = 255)
    private String synonyms;

    @Column(name = "changeDate")
    private Date changeDate;

    @Column(name = "changeDescription", columnDefinition = "TEXT")
    private String changeDescription;

    @Column(name = "originatingProcess", length = 255)
    private String originatingProcess;

    @Column(name = "variabilityBasedOn", length = 255)
    private String variabilityBasedOn;

    // Getters and Setters
    public String getRoleID() { return roleID; }
    public void setRoleID(String roleID) { this.roleID = roleID; }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPresentationName() { return presentationName; }
    public void setPresentationName(String presentationName) { this.presentationName = presentationName; }

    public String getBriefDescription() { return briefDescription; }
    public void setBriefDescription(String briefDescription) { this.briefDescription = briefDescription; }

    public String getResponsibilities() { return responsibilities; }
    public void setResponsibilities(String responsibilities) { this.responsibilities = responsibilities; }

    public String getKeyConsiderations() { return keyConsiderations; }
    public void setKeyConsiderations(String keyConsiderations) { this.keyConsiderations = keyConsiderations; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getStaffing() { return staffing; }
    public void setStaffing(String staffing) { this.staffing = staffing; }

    public String getSynonyms() { return synonyms; }
    public void setSynonyms(String synonyms) { this.synonyms = synonyms; }

    public Date getChangeDate() { return changeDate; }
    public void setChangeDate(Date changeDate) { this.changeDate = changeDate; }

    public String getChangeDescription() { return changeDescription; }
    public void setChangeDescription(String changeDescription) { this.changeDescription = changeDescription; }

    public String getOriginatingProcess() { return originatingProcess; }
    public void setOriginatingProcess(String originatingProcess) { this.originatingProcess = originatingProcess; }

    public String getVariabilityBasedOn() { return variabilityBasedOn; }
    public void setVariabilityBasedOn(String variabilityBasedOn) { this.variabilityBasedOn = variabilityBasedOn; }
}
