package com.ey.method.migration.parser;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class MethodRoleXml {
    @XmlAttribute(name = "id")
    private String id;
    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "brief-description")
    private String briefDescription;
    @XmlAttribute(name = "variabilityBasedOnElement")
    private String variabilityBasedOn;
    @XmlAttribute(name = "method-link")
    private String methodLink;

    @XmlElementWrapper(name = "guidances")
    @XmlElement(name = "guidance-id")
    private List<String> relatedGuidanceIds;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getBriefDescription() { return briefDescription; }
    public String getVariabilityBasedOn() { return variabilityBasedOn; }
    public String getMethodLink() { return methodLink; }
    public List<String> getRelatedGuidanceIds() { return relatedGuidanceIds; }
}
