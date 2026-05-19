package com.ey.method.migration.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "WorkProductDefinition")
@IdClass(WorkProductDefinitionId.class)
public class WorkProductDefinition {

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

    @Column(name = "externalID", length = 25)
    private String externalID;

    @Column(name = "briefDescription", length = 1000)
    private String briefDescription;

    @Column(name = "purpose", length = 1600)
    private String purpose;

    @Column(name = "mainDescription", length = 17200)
    private String mainDescription;

    @Column(name = "keyConsiderations", length = 2200)
    private String keyConsiderations;

    @Column(name = "briefOutline", length = 9700)
    private String briefOutline;

    @Column(name = "selectedRepresentation", length = 1600)
    private String selectedRepresentation;

    @Column(name = "impactOfNotHaving", length = 700)
    private String impactOfNotHaving;

    @Column(name = "reasonsForNotNeeding", length = 400)
    private String reasonsForNotNeeding;

    @Column(name = "representationOptions", length = 3000)
    private String representationOptions;

    @Column(name = "methodSpecificInformation", length = 10)
    private String methodSpecificInformation;

    @Column(name = "synonym", length = 10)
    private String synonym;

    @Column(name = "changeDate")
    private Date changeDate;

    @Column(name = "changeDescription", length = 10)
    private String changeDescription;

    @Column(name = "originatingProcess", length = 10)
    private String originatingProcess;

    @Column(name = "variabilityBasedOn", length = 255)
    private String variabilityBasedOn;

    // Getters and Setters
    public String getWorkProductID() { return workProductID; }
    public void setWorkProductID(String workProductID) { this.workProductID = workProductID; }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPresentationName() { return presentationName; }
    public void setPresentationName(String presentationName) { this.presentationName = presentationName; }

    public String getExternalID() { return externalID; }
    public void setExternalID(String externalID) { this.externalID = externalID; }

    public String getBriefDescription() { return briefDescription; }
    public void setBriefDescription(String briefDescription) { this.briefDescription = briefDescription; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getMainDescription() { return mainDescription; }
    public void setMainDescription(String mainDescription) { this.mainDescription = mainDescription; }

    public String getKeyConsiderations() { return keyConsiderations; }
    public void setKeyConsiderations(String keyConsiderations) { this.keyConsiderations = keyConsiderations; }

    public String getBriefOutline() { return briefOutline; }
    public void setBriefOutline(String briefOutline) { this.briefOutline = briefOutline; }

    public String getSelectedRepresentation() { return selectedRepresentation; }
    public void setSelectedRepresentation(String selectedRepresentation) { this.selectedRepresentation = selectedRepresentation; }

    public String getImpactOfNotHaving() { return impactOfNotHaving; }
    public void setImpactOfNotHaving(String impactOfNotHaving) { this.impactOfNotHaving = impactOfNotHaving; }

    public String getReasonsForNotNeeding() { return reasonsForNotNeeding; }
    public void setReasonsForNotNeeding(String reasonsForNotNeeding) { this.reasonsForNotNeeding = reasonsForNotNeeding; }

    public String getRepresentationOptions() { return representationOptions; }
    public void setRepresentationOptions(String representationOptions) { this.representationOptions = representationOptions; }

    public String getMethodSpecificInformation() { return methodSpecificInformation; }
    public void setMethodSpecificInformation(String methodSpecificInformation) { this.methodSpecificInformation = methodSpecificInformation; }

    public String getSynonym() { return synonym; }
    public void setSynonym(String synonym) { this.synonym = synonym; }

    public Date getChangeDate() { return changeDate; }
    public void setChangeDate(Date changeDate) { this.changeDate = changeDate; }

    public String getChangeDescription() { return changeDescription; }
    public void setChangeDescription(String changeDescription) { this.changeDescription = changeDescription; }

    public String getOriginatingProcess() { return originatingProcess; }
    public void setOriginatingProcess(String originatingProcess) { this.originatingProcess = originatingProcess; }

    public String getVariabilityBasedOn() { return variabilityBasedOn; }
    public void setVariabilityBasedOn(String variabilityBasedOn) { this.variabilityBasedOn = variabilityBasedOn; }
}
