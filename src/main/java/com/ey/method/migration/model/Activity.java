package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Activity")
public class Activity {

    @Id
    @Column(name = "activityID", length = 25)
    private String activityID;

    @Column(name = "contextID", length = 36)
    private String contextID;

    @Column(name = "type", length = 25)
    private String type;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "presentationName", length = 100)
    private String presentationName;

    @Column(name = "briefDescription", length = 1000)
    private String briefDescription;

    @Column(name = "summary", length = 5800)
    private String summary;

    @Column(name = "keyConsiderations", length = 4800)
    private String keyConsiderations;

    @Column(name = "goals", length = 1200)
    private String goals;

    @Column(name = "entryCriteria", length = 10)
    private String entryCriteria;

    @Column(name = "exitCriteria", length = 10)
    private String exitCriteria;

    @Column(name = "childWBS", length = 25)
    private String childWBS;

    // Getters and Setters
    public String getActivityID() { return activityID; }
    public void setActivityID(String activityID) { this.activityID = activityID; }

    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPresentationName() { return presentationName; }
    public void setPresentationName(String presentationName) { this.presentationName = presentationName; }

    public String getBriefDescription() { return briefDescription; }
    public void setBriefDescription(String briefDescription) { this.briefDescription = briefDescription; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getKeyConsiderations() { return keyConsiderations; }
    public void setKeyConsiderations(String keyConsiderations) { this.keyConsiderations = keyConsiderations; }

    public String getGoals() { return goals; }
    public void setGoals(String goals) { this.goals = goals; }

    public String getEntryCriteria() { return entryCriteria; }
    public void setEntryCriteria(String entryCriteria) { this.entryCriteria = entryCriteria; }

    public String getExitCriteria() { return exitCriteria; }
    public void setExitCriteria(String exitCriteria) { this.exitCriteria = exitCriteria; }

    public String getChildWBS() { return childWBS; }
    public void setChildWBS(String childWBS) { this.childWBS = childWBS; }
}
