package com.ey.method.migration.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ReleaseInformation")
@IdClass(ReleaseInformationId.class)
public class ReleaseInformation {

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Id
    @Column(name = "guidanceID", length = 25)
    private String guidanceID;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "presentationName", length = 75)
    private String presentationName;

    @Column(name = "whatsNew", length = 3500)
    private String whatsNew;

    @Column(name = "revisionHistory", length = 16700)
    private String revisionHistory;

    @Column(name = "acknowledgements", length = 2100)
    private String acknowledgements;

    @Column(name = "internalHistory", length = 8300)
    private String internalHistory;

    @Column(name = "designerCommentary", length = 300)
    private String designerCommentary;

    @Column(name = "lastReviewed")
    private LocalDate lastReviewed;

    @Column(name = "processID", length = 36)
    private String processID;

    // Getters and Setters
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getGuidanceID() { return guidanceID; }
    public void setGuidanceID(String guidanceID) { this.guidanceID = guidanceID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPresentationName() { return presentationName; }
    public void setPresentationName(String presentationName) { this.presentationName = presentationName; }

    public String getWhatsNew() { return whatsNew; }
    public void setWhatsNew(String whatsNew) { this.whatsNew = whatsNew; }

    public String getRevisionHistory() { return revisionHistory; }
    public void setRevisionHistory(String revisionHistory) { this.revisionHistory = revisionHistory; }

    public String getAcknowledgements() { return acknowledgements; }
    public void setAcknowledgements(String acknowledgements) { this.acknowledgements = acknowledgements; }

    public String getInternalHistory() { return internalHistory; }
    public void setInternalHistory(String internalHistory) { this.internalHistory = internalHistory; }

    public String getDesignerCommentary() { return designerCommentary; }
    public void setDesignerCommentary(String designerCommentary) { this.designerCommentary = designerCommentary; }

    public LocalDate getLastReviewed() { return lastReviewed; }
    public void setLastReviewed(LocalDate lastReviewed) { this.lastReviewed = lastReviewed; }

    public String getProcessID() { return processID; }
    public void setProcessID(String processID) { this.processID = processID; }
}
