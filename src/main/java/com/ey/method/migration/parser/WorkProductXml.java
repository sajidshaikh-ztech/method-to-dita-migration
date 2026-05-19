package com.ey.method.migration.parser;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class WorkProductXml {
    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "type")
    private String type;
    @XmlAttribute(name = "brief-description")
    private String briefDescription;
    @XmlAttribute(name = "method-link")
    private String methodLink;

    @XmlElementWrapper(name = "guidances")
    @XmlElement(name = "guidance-id")
    private java.util.List<String> guidanceIds;

    @XmlElementWrapper(name = "work-products")
    @XmlElement(name = "wp-id")
    private java.util.List<String> workProductIds;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getBriefDescription() { return briefDescription; }
    public String getMethodLink() { return methodLink; }
    public java.util.List<String> getGuidanceIds() { return guidanceIds; }
    public java.util.List<String> getWorkProductIds() { return workProductIds; }
}
