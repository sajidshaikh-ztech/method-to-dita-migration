package com.ey.method.migration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "WBS")
@IdClass(WbsId.class)
public class WBS {

    @Id
    @Column(name = "contextID", length = 36)
    private String contextID;

    @Id
    @Column(name = "parentID", length = 36)
    private String parentID;

    @Id
    @Column(name = "childID", length = 36)
    private String childID;

    @Column(name = "`index`")
    private Integer itemIndex;

    // Getters and Setters
    public String getContextID() { return contextID; }
    public void setContextID(String contextID) { this.contextID = contextID; }

    public String getParentID() { return parentID; }
    public void setParentID(String parentID) { this.parentID = parentID; }

    public String getChildID() { return childID; }
    public void setChildID(String childID) { this.childID = childID; }

    public Integer getItemIndex() { return itemIndex; }
    public void setItemIndex(Integer itemIndex) { this.itemIndex = itemIndex; }
}
