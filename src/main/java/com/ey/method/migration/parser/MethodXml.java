package com.ey.method.migration.parser;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "method")
@XmlAccessorType(XmlAccessType.FIELD)
public class MethodXml {

    @XmlAttribute(name = "external-id")
    private String externalId;

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "description")
    private String description;

    @XmlAttribute(name = "sponsor")
    private String sponsor;

    @XmlAttribute(name = "steward")
    private String steward;

    @XmlAttribute(name = "publish")
    private String publish;

    @XmlAttribute(name = "type")
    private String type;

    @XmlAttribute(name = "foundation")
    private String foundation;

    @XmlAttribute(name = "msc")
    private String msc;

    @XmlAttribute(name = "gsc")
    private String gsc;

    @XmlAttribute(name = "plan-xlsx")
    private String planXlsx;

    @XmlAttribute(name = "plan-mpp")
    private String planMpp;

    @XmlElement(name = "process-item")
    private ProcessItemXml rootProcessItem;

    @XmlElementWrapper(name = "work-products")
    @XmlElement(name = "work-product")
    private java.util.List<WorkProductXml> workProducts = new java.util.ArrayList<>();

    @XmlElementWrapper(name = "guidances")
    @XmlElement(name = "guidance")
    private java.util.List<GuidanceXml> guidances = new java.util.ArrayList<>();

    @XmlElementWrapper(name = "method-roles")
    @XmlElement(name = "method-role")
    private java.util.List<MethodRoleXml> methodRoles = new java.util.ArrayList<>();

    @XmlElementWrapper(name = "tasks")
    @XmlElement(name = "task")
    private java.util.List<TaskXml> tasks = new java.util.ArrayList<>();

    @XmlElementWrapper(name = "workstreams")
    @XmlElement(name = "workstream")
    private java.util.List<WorkstreamXml> workstreams = new java.util.ArrayList<>();

    // Getters
    public String getExternalId() { return externalId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getSponsor() { return sponsor; }
    public String getSteward() { return steward; }
    public String getPublish() { return publish; }
    public String getType() { return type; }
    public String getFoundation() { return foundation; }
    public String getMsc() { return msc; }
    public String getGsc() { return gsc; }
    public ProcessItemXml getRootProcessItem() { return rootProcessItem; }
    public java.util.List<WorkProductXml> getWorkProducts() { return workProducts; }
    public java.util.List<GuidanceXml> getGuidances() { return guidances; }
    public java.util.List<MethodRoleXml> getMethodRoles() { return methodRoles; }
    public java.util.List<TaskXml> getTasks() { return tasks; }
    public java.util.List<WorkstreamXml> getWorkstreams() { return workstreams; }
    public String getPlanXlsx() { return planXlsx; }
    public String getPlanMpp() { return planMpp; }
}
