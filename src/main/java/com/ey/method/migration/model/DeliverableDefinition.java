package com.ey.method.migration.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DeliverableDefinition")
@IdClass(DeliverableDefinitionId.class)
public class DeliverableDefinition {

    @Id
    @Column(name = "workProductID", length = 25)
    private String workProductID;

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "presentationName", length = 255)
    private String presentationName;

    @Column(name = "briefDescription", length = 1000)
    private String briefDescription;

    @Column(name = "clientValue", columnDefinition = "TEXT")
    private String clientValue;

    @Column(name = "internalDescription", columnDefinition = "TEXT")
    private String internalDescription;

    @Column(name = "externalDescription", columnDefinition = "TEXT")
    private String externalDescription;

    @Column(name = "packagingGuidance", columnDefinition = "TEXT")
    private String packagingGuidance;

    @Column(name = "changeDate")
    private Date changeDate;

    @Column(name = "changeDescription", length = 100)
    private String changeDescription;

    @Column(name = "originating_process", length = 100)
    private String originating_process;

    // Getters and Setters
    public String getWorkProductID() { return workProductID; }
    public void setWorkProductID(String workProductID) { this.workProductID = workProductID; }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPresentationName() { return presentationName; }
    public void setPresentationName(String presentationName) { this.presentationName = presentationName; }

    public String getBriefDescription() { return briefDescription; }
    public void setBriefDescription(String briefDescription) { this.briefDescription = briefDescription; }

    public String getClientValue() { return clientValue; }
    public void setClientValue(String clientValue) { this.clientValue = clientValue; }

    public String getInternalDescription() { return internalDescription; }
    public void setInternalDescription(String internalDescription) { this.internalDescription = internalDescription; }

    public String getExternalDescription() { return externalDescription; }
    public void setExternalDescription(String externalDescription) { this.externalDescription = externalDescription; }

    public String getPackagingGuidance() { return packagingGuidance; }
    public void setPackagingGuidance(String packagingGuidance) { this.packagingGuidance = packagingGuidance; }

    public Date getChangeDate() { return changeDate; }
    public void setChangeDate(Date changeDate) { this.changeDate = changeDate; }

    public String getChangeDescription() { return changeDescription; }
    public void setChangeDescription(String changeDescription) { this.changeDescription = changeDescription; }

    public String getOriginating_process() { return originating_process; }
    public void setOriginating_process(String originating_process) { this.originating_process = originating_process; }
}
