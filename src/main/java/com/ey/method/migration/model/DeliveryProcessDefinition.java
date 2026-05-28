package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "DeliveryProcessDefinition")
public class DeliveryProcessDefinition {

    @Id
    @Column(name = "processID", length = 36)
    private String processID;

    @Column(name = "contextID", length = 36)
    private String contextID;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "presentationName", length = 75)
    private String presentationName;

    @Column(name = "externalID", length = 25)
    private String externalID;

    @Column(name = "clientIssue", length = 10)
    private String clientIssue;

    @Column(name = "mainDescription", length = 1400)
    private String mainDescription;

    @Column(name = "usageNotes", length = 1000)
    private String usageNotes;

    @Column(name = "notice", length = 10)
    private String notice;

    @Column(name = "keywords", length = 200)
    private String keywords;

    @Column(name = "sponsor", length = 50)
    private String sponsor;

    @Column(name = "steward", length = 50)
    private String steward;

    @Column(name = "publish", length = 10)
    private String publish;

    @Column(name = "pubType", length = 10)
    private String pubType;

    @Column(name = "subtype", length = 10)
    private String subtype;

    @Column(name = "foundation", length = 10)
    private String foundation;

    @Column(name = "mercuryServiceCode", length = 10)
    private String mercuryServiceCode;

    @Column(name = "globalServiceCode", length = 10)
    private String globalServiceCode;

    @Column(name = "childWBS", length = 25)
    private String childWBS;

    @Column(name = "shortname", length = 50)
    private String shortname;

    // Getters and Setters
    public String getProcessID() { return processID; }
    public void setProcessID(String processID) { this.processID = processID; }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPresentationName() { return presentationName; }
    public void setPresentationName(String presentationName) { this.presentationName = presentationName; }

    public String getExternalID() { return externalID; }
    public void setExternalID(String externalID) { this.externalID = externalID; }

    public String getClientIssue() { return clientIssue; }
    public void setClientIssue(String clientIssue) { this.clientIssue = clientIssue; }

    public String getMainDescription() { return mainDescription; }
    public void setMainDescription(String mainDescription) { this.mainDescription = mainDescription; }

    public String getUsageNotes() { return usageNotes; }
    public void setUsageNotes(String usageNotes) { this.usageNotes = usageNotes; }

    public String getNotice() { return notice; }
    public void setNotice(String notice) { this.notice = notice; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public String getSponsor() { return sponsor; }
    public void setSponsor(String sponsor) { this.sponsor = sponsor; }

    public String getSteward() { return steward; }
    public void setSteward(String steward) { this.steward = steward; }

    public String getPublish() { return publish; }
    public void setPublish(String publish) { this.publish = publish; }

    public String getPubType() { return pubType; }
    public void setPubType(String pubType) { this.pubType = pubType; }

    public String getSubtype() { return subtype; }
    public void setSubtype(String subtype) { this.subtype = subtype; }

    public String getFoundation() { return foundation; }
    public void setFoundation(String foundation) { this.foundation = foundation; }

    public String getMercuryServiceCode() { return mercuryServiceCode; }
    public void setMercuryServiceCode(String mercuryServiceCode) { this.mercuryServiceCode = mercuryServiceCode; }

    public String getGlobalServiceCode() { return globalServiceCode; }
    public void setGlobalServiceCode(String globalServiceCode) { this.globalServiceCode = globalServiceCode; }

    public String getChildWBS() { return childWBS; }
    public void setChildWBS(String childWBS) { this.childWBS = childWBS; }

    public String getShortname() { return shortname; }
    public void setShortname(String shortname) { this.shortname = shortname; }
}
